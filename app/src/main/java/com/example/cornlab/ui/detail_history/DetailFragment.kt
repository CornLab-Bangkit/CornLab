package com.example.cornlab.ui.detail_history

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.cornlab.data.local.HistoryDatabase
import com.example.cornlab.databinding.FragmentDetailHistoryBinding

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailHistoryBinding? = null
    private val binding get() = _binding!!

    private val historyDao by lazy {
        HistoryDatabase.getInstance(requireContext()).historyDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val toolbar = binding.toolbar
        NavigationUI.setupWithNavController(toolbar, navController)
        toolbar.title = ""

        val historyId = arguments?.getInt("historyId")
        if (historyId == null) {
            showToast("Invalid history ID")
            return
        }

        // Observasi data history berdasarkan ID
        historyDao.getHistoryById(historyId).observe(viewLifecycleOwner) { history ->
            history?.let {
                binding.tvResult.text = it.result ?: "No Result"
                binding.tvCreatedAt.text = it.createdAt ?: "Unknown Date"
                binding.tvSuggestion.text = it.suggestion ?: "No Suggestion"
                binding.ivImageResult.setImageURI(Uri.parse(it.imageUri))
                binding.fullImage.setImageURI(Uri.parse(it.imageUri))

            } ?: run {
                showToast("History not found")
            }
        }

        binding.ivImageResult.setOnClickListener {
            fadeIn(binding.fullImageOverlay)
        }
        binding.btnCloseOverlay.setOnClickListener {
            fadeOut(binding.fullImageOverlay)
        }
    }

    private fun fadeIn(view: View) {
        view.visibility = View.VISIBLE
        val animation = AlphaAnimation(0f, 1f)
        animation.duration = 300
        view.startAnimation(animation)
    }

    private fun fadeOut(view: View) {
        val animation = AlphaAnimation(1f, 0f)
        animation.duration = 300
        animation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
            override fun onAnimationStart(animation: android.view.animation.Animation?) {}
            override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                view.visibility = View.GONE
            }
            override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
        })
        view.startAnimation(animation)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
