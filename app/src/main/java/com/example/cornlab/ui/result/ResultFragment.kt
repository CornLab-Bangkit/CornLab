package com.example.cornlab.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.example.cornlab.databinding.FragmentResultBinding
import com.example.cornlab.ui.analyze.AnalyzeViewModel
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import android.net.Uri
import android.view.animation.AlphaAnimation

class ResultFragment : Fragment() {

    private lateinit var analyzeViewModel: AnalyzeViewModel
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        analyzeViewModel = ViewModelProvider(requireActivity())[AnalyzeViewModel::class.java]

        analyzeViewModel.analysisData.observe(viewLifecycleOwner) { data ->
            binding.tvResult.text = data.result
            binding.tvCreatedAt.text = data.createdAt
            binding.tvSuggestion.text = data.suggestion

            Glide.with(this)
                .load(Uri.parse(data.imageUri))
                .into(binding.ivImageResult)
            Glide.with(this)
                .load(Uri.parse(data.imageUri))
                .into(binding.fullImage)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
