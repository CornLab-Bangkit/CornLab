package com.example.cornlab.ui.analyze

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.navigation.fragment.findNavController
import com.example.cornlab.R
import com.example.cornlab.databinding.FragmentAnalyzeBinding
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class AnalyzeFragment : Fragment(R.layout.fragment_analyze) {

    private lateinit var binding: FragmentAnalyzeBinding
    private lateinit var previewView: PreviewView
    private lateinit var capturedImage: ImageView
    private lateinit var imageCapture: ImageCapture
    private lateinit var cardContainer: MaterialCardView
    private lateinit var fabTakePic: FloatingActionButton
    private lateinit var fabAnalyze: FloatingActionButton
    private lateinit var fabDelImage: FloatingActionButton
    private lateinit var galleryButton: LinearLayout

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
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

        val toolbar = binding.toolbar
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Hide Bottom NavBar
        activity?.findViewById<CoordinatorLayout>(R.id.setBottomNav)?.visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        previewView = binding.previewView
        capturedImage = binding.capturedImage
        fabTakePic = binding.fabTakepic
        fabAnalyze = binding.fabAnalyze
        fabDelImage = binding.fabDelImage
        cardContainer = binding.cardContainer
        galleryButton = binding.galleryButton

        // Cek sama minta izin buat gunain camera
        if (!hasCameraPermission()) {
            requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        } else {
            startCamera()
        }

        // Action button buat ngambil foto
        fabTakePic.setOnClickListener {
            takePhoto()
        }

        // Action button buat hapus gambar hasil captured
        fabDelImage.setOnClickListener {
            resetUIAfterDelete()
        }

        // Action button buat analisis gambar
        fabAnalyze.setOnClickListener {
            Log.d("Analyze", "Analyze button clicked")
            // Nanti di isi logic buat analisis gambar
        }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                Log.d("CameraX", "CameraProvider initialized successfully")

                val preview = Preview.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    .build()
                    .also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }

                imageCapture = ImageCapture.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    .build()

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Log.e("CameraX", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val photoFile = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "captured_image.jpg"
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
                    val savedUri = Uri.fromFile(photoFile)
                    updateUIAfterPhotoTaken(savedUri)
                }
            }
        )
    }

    private fun updateUIAfterPhotoTaken(savedUri: Uri) {
        capturedImage.setImageURI(savedUri)
        capturedImage.visibility = View.VISIBLE
        previewView.visibility = View.GONE

        // After take a pic, button buat ngambil gambar bakal diubah jadi button buat analisis
        fabTakePic.visibility = View.GONE
        fabAnalyze.visibility = View.VISIBLE
        fabDelImage.visibility = View.VISIBLE
    }

    private fun resetUIAfterDelete() {
        capturedImage.setImageURI(null)
        capturedImage.visibility = View.GONE
        previewView.visibility = View.VISIBLE

        fabTakePic.visibility = View.VISIBLE
        fabAnalyze.visibility = View.GONE
        fabDelImage.visibility = View.GONE

        // Hapus file gambar biar ga jadi sampah di aplikasi
        val photoFile = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "captured_image.jpg"
        )
        if (photoFile.exists()) {
            photoFile.delete()
            Log.d("CameraX", "Captured image deleted")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        activity?.findViewById<CoordinatorLayout>(R.id.setBottomNav)?.visibility = View.VISIBLE
    }
}
