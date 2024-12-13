package com.example.cornlab.ui.detail_recom

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.cornlab.data.response.Recommendation
import com.example.cornlab.databinding.FragmentDetailBinding
import com.example.cornlab.ui.BaseFragment

class DetailFragment : BaseFragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val detailViewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recomId = arguments?.getInt(RECOM_ID_KEY) ?: return

        binding.progressBar.visibility = View.VISIBLE

        detailViewModel.getRecomDetails(recomId)

        detailViewModel.recomDetails.observe(viewLifecycleOwner) { recomDetails ->
            binding.progressBar.visibility = View.GONE
            recomDetails?.let {
                updateUI(it)
            }
        }

        detailViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                // Reset error agar Toast tidak ditampilkan berulang
                detailViewModel.resetError()
            }
        }

    }

    private fun updateUI(recomDetails: Recommendation) {
        binding.tvRecomName.text = recomDetails.name
        binding.tvRecomDescription.text = recomDetails.description
        binding.tvRecomSteps.text = HtmlCompat.fromHtml(
            (recomDetails.steps?: ""),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        binding.ivImageCover.loadImage(recomDetails.imageCover)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val RECOM_ID_KEY = "recomId"
    }
}

fun ImageView.loadImage(url: String?) {
    Glide.with(this.context)
        .load(url)
        .centerCrop()
        .into(this)
}