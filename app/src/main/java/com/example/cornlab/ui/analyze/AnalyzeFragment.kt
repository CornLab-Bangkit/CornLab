package com.example.cornlab.ui.analyze

import android.app.Activity.RESULT_OK
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.core.net.toUri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import com.yalantis.ucrop.UCrop
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.camera.core.Preview
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.cornlab.R
import com.example.cornlab.data.local.HistoryDatabase
import com.example.cornlab.data.local.HistoryEntity
import com.example.cornlab.data.response.AnalyzeResponse
import com.example.cornlab.data.retrofit.AnalyzeApiConfig
import com.example.cornlab.databinding.FragmentAnalyzeBinding
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class AnalyzeFragment : Fragment(R.layout.fragment_analyze) {

    private lateinit var binding: FragmentAnalyzeBinding
    private lateinit var navController: NavController
    private lateinit var previewView: PreviewView
    private lateinit var capturedImage: ImageView
    private lateinit var imageCapture: ImageCapture
    private lateinit var cardContainer: MaterialCardView
    private lateinit var fabTakePic: FloatingActionButton
    private lateinit var fabAnalyze: FloatingActionButton
    private lateinit var fabDelImage: FloatingActionButton
    private lateinit var galleryButton: LinearLayout
    private lateinit var cameraButtonIcon: ImageView
    private lateinit var cameraButtonText: TextView
    private lateinit var selectRatio: ConstraintLayout
    private lateinit var tvScanLeaf: TextView
    private lateinit var tvStartAnalyze: TextView
    private var currentImageUri: Uri? = null

    private val defaultIconColor by lazy {
        ContextCompat.getColor(requireContext(), R.color.green)
    }
    private val resetIconColor by lazy {
        ContextCompat.getColor(requireContext(), R.color.dark_grey)
    }

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCameraWithAspectRatio(AspectRatio.RATIO_16_9)
            } else {
                Log.e("Permission", "Camera permission denied.")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnalyzeBinding.inflate(inflater, container, false)

        activity?.findViewById<CoordinatorLayout>(R.id.setBottomNav)?.visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()
        val toolbar = binding.toolbar

        NavigationUI.setupWithNavController(toolbar, navController)
        toolbar.title = ""

        previewView = binding.previewView
        capturedImage = binding.capturedImage
        fabTakePic = binding.fabTakepic
        fabDelImage = binding.fabDelImage
        cardContainer = binding.cardContainer
        galleryButton = binding.galleryButton
        cameraButtonIcon = binding.cameraButtonIcon
        cameraButtonText = binding.cameraButtonText
        fabAnalyze = binding.fabAnalyze
        selectRatio = binding.selectRatio
        tvScanLeaf = binding.tvScanLeaf
        tvStartAnalyze = binding.tvStartAnalyze

        // Cek sama minta izin buat gunain camera
        if (!hasCameraPermission()) {
            requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        } else {
            startCameraWithAspectRatio(AspectRatio.RATIO_16_9)
            updateAspectRatio(AspectRatio.RATIO_16_9, binding.ratio916)
        }

        val fabAnalyze = view.findViewById<FloatingActionButton>(R.id.fab_Analyze)

        galleryButton.setOnClickListener { startGallery() }
        fabTakePic.setOnClickListener { takePhoto() }
        fabDelImage.setOnClickListener { resetUIAfterDelete() }
        fabAnalyze.setOnClickListener { startAnalyze() }

        setupFabListeners()
    }

    fun File.reduceFileImage(): File {
        val bitmap = BitmapFactory.decodeFile(this.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > MAXIMAL_SIZE)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(this))
        return this
    }

    private fun startGallery() {
        pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
    }

    private val pickMedia =
        registerForActivityResult(PickVisualMedia()) { uri ->
            if (uri != null) {
                updateUIAfterPhotoTaken(uri)
                startCrop(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    // UCrop Launcher
    private val cropImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val croppedUri = UCrop.getOutput(result.data!!)
                if (croppedUri != null) {
                    val croppedFile = File(croppedUri.path!!).reduceFileImage()
                    updateUIAfterPhotoTaken(Uri.fromFile(croppedFile)) // Update UI setelah crop dan kompres selesai
                }
            } else if (result.resultCode == UCrop.RESULT_ERROR) {
                val cropError = UCrop.getError(result.data!!)
                Log.e("CropError", cropError?.message ?: "Unknown error")
            }
        }

    private fun startCrop(sourceUri: Uri) {
        val fileName = "cropped_image_${System.currentTimeMillis()}.jpg"
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, fileName))
        val cropIntent = UCrop.of(sourceUri, destinationUri)
            .withMaxResultSize(800, 800)
            .getIntent(requireContext())

        cropImage.launch(cropIntent) // Luncurkan UCrop
    }

    private fun setupFabListeners() {
        binding.ratio11.setOnClickListener {
            updateAspectRatio(AspectRatio.RATIO_DEFAULT, binding.ratio11)
        }

        binding.ratio916.setOnClickListener {
            updateAspectRatio(AspectRatio.RATIO_16_9, binding.ratio916)
        }

        binding.ratio34.setOnClickListener {
            updateAspectRatio(AspectRatio.RATIO_4_3, binding.ratio34)
        }
    }

    private fun updateAspectRatio(aspectRatio: Int, activeFab: FloatingActionButton) {
        // Update warna FAB
        activeFab.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
        resetFabColors()

        // Update rasio kamera
        startCameraWithAspectRatio(aspectRatio)

        // Update PreviewView agar gambar sesuai lebar
        adjustPreviewView(aspectRatio)
    }

    private fun resetFabColors() {
        binding.ratio11.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.fab_bground2)
        binding.ratio916.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.fab_bground2)
        binding.ratio34.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.fab_bground2)
    }

    private fun startCameraWithAspectRatio(aspectRatio: Int) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .setTargetAspectRatio(aspectRatio)
                .build()
                .also {
                    it.surfaceProvider = binding.previewView.surfaceProvider
                }

            imageCapture = ImageCapture.Builder()
                .setTargetAspectRatio(aspectRatio)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun adjustPreviewView(aspectRatio: Int) {
        val params = binding.previewView.layoutParams

        when (aspectRatio) {
            AspectRatio.RATIO_16_9 -> {
                params.height = ViewGroup.LayoutParams.MATCH_PARENT
                binding.previewView.setBackgroundColor(Color.TRANSPARENT)
            }
            AspectRatio.RATIO_4_3 -> {
                params.height = (binding.previewView.width * 4) / 3
                binding.previewView.setBackgroundColor(Color.TRANSPARENT)
            }
            AspectRatio.RATIO_DEFAULT -> {
                params.height = binding.previewView.width
                binding.previewView.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        binding.previewView.layoutParams = params

    }


    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun takePhoto() {
        val photoFile = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "captured_image${System.currentTimeMillis()}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraX", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val compressedFile = photoFile.reduceFileImage()
                    updateUIAfterPhotoTaken(Uri.fromFile(compressedFile))
                }
            }
        )
    }

    private fun updateUIAfterPhotoTaken(savedUri: Uri) {
        currentImageUri = savedUri
        capturedImage.setImageURI(savedUri)
        capturedImage.visibility = View.VISIBLE
        previewView.visibility = View.GONE
        selectRatio.visibility = View.GONE
        tvScanLeaf.visibility = View.GONE
        tvStartAnalyze.visibility = View.VISIBLE

        // Cek ukuran gambar yang diambil
        val imageFile = File(savedUri.path!!)
        val imageSizeInBytes = imageFile.length()
        val imageSizeInKB = imageSizeInBytes / 1024 // konversi ke KB
        val imageSizeInMB = imageSizeInKB / 1024 // konversi ke MB

        // Log ukuran gambar
        Log.d("CapturedImageSize", "Image Size: $imageSizeInKB KB / $imageSizeInMB MB")

        // After take a pic, button buat ngambil gambar bakal diubah jadi button buat analisis
        fabTakePic.visibility = View.GONE
        fabDelImage.visibility = View.VISIBLE
        fabAnalyze.visibility = View.VISIBLE

        // Update warna buttom icon menjadi dark grey saat foto diambil
        cameraButtonIcon.setColorFilter(resetIconColor)
        cameraButtonText.setTextColor(resetIconColor)
    }

    private fun resetUIAfterDelete() {
        currentImageUri = null
        capturedImage.setImageURI(null)
        capturedImage.visibility = View.GONE
        previewView.visibility = View.VISIBLE
        selectRatio.visibility = View.VISIBLE
        tvScanLeaf.visibility = View.VISIBLE
        tvStartAnalyze.visibility = View.GONE

        fabTakePic.visibility = View.VISIBLE
        fabAnalyze.visibility = View.GONE
        fabDelImage.visibility = View.GONE

        // Kembalikan warna buttom icon ke warna hijau
        cameraButtonIcon.setColorFilter(defaultIconColor)
        cameraButtonText.setTextColor(defaultIconColor)

        // Hapus file gambar biar ga jadi sampah di aplikasi
        val photoFile = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "captured_image${System.currentTimeMillis()}.jpg"
        )
        if (photoFile.exists()) {
            photoFile.delete()
            Log.d("CameraX", "Captured image deleted")
        }
        if (currentImageUri != null) {
            Log.d("ResetUI", "Image URI reset: $currentImageUri")
        }
    }

    private fun startAnalyze() {
        if (currentImageUri != null) {
            val file = File(currentImageUri!!.path!!)
            val requestBody = file.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData("image", file.name, requestBody)
            val analyzeViewModel = ViewModelProvider(requireActivity())[AnalyzeViewModel::class.java]
            val analyzeService = AnalyzeApiConfig.getAnalyzeApiService()
            lifecycleScope.launch {
                try {
                    showLoading(true)
                    val response = analyzeService.startAnalyze(multipartBody)
                    if (response.isSuccessful) {
                        val analyzeResponse = response.body()
                        analyzeResponse?.data?.let { data ->
                            data.imageUri = currentImageUri.toString()
                            analyzeViewModel.setAnalysisData(data)

                            saveToHistory(
                                uri = currentImageUri!!,
                                result = data.result,
                                suggestion = data.suggestion,
                                createdAt = data.createdAt
                            )
                            navController.navigate(R.id.action_analyze_to_result)
                        } ?: run {
                            showToast("No data received from analysis")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        showToast("Analysis failed: $errorBody")
                    }
                } catch (e: Exception) {
                    showToast("An error occurred: ${e.message}")
                } finally {
                    showLoading(false)
                }
            }
        } else {
            showToast("No image to analyze")
        }
    }

    private fun saveToHistory(uri: Uri, result: String?, suggestion: String?, createdAt: String?) {
        val historyDatabase = HistoryDatabase.getInstance(requireContext())
        val historyDao = historyDatabase.historyDao()

        lifecycleScope.launch(Dispatchers.IO) {
            val historyEntity = HistoryEntity(
                imageUri = uri.toString(),
                result = result,
                suggestion = suggestion,
                createdAt = createdAt
            )
            historyDao.insert(historyEntity)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.findViewById<CoordinatorLayout>(R.id.setBottomNav)?.visibility = View.VISIBLE
    }

    companion object {
        private const val MAXIMAL_SIZE = 1000000
    }
}
