package com.example.cornlab.ui.detail

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.cornlab.R
import com.example.cornlab.data.response.DetailResponse
import com.example.cornlab.databinding.FragmentDetailBinding
import com.example.cornlab.ui.BaseFragment

class DetailFragment : BaseFragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val detailViewModel: DetailViewModel by viewModels()

    companion object {
        private const val RECOM_ID_KEY = "recomId"
    }

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

        // Memuat detail rekomendasi saat fragment muncul
        detailViewModel.getRecomDetails(recomId)

        detailViewModel.recomDetails.observe(viewLifecycleOwner) { recomDetails ->
            binding.progressBar.visibility = View.GONE
            recomDetails?.let {
                updateUI(it)
            }
        }
    }

    private fun updateUI(recomDetails: Recom) {
        binding.tvRecomName.text = recomDetails.name
        binding.tvRecomDescription.text = HtmlCompat.fromHtml(
            recomDetails.description ?: "",
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        binding.tvRecomOwner.text = recomDetails.ownerName
        binding.tvRecomTime.text = recomDetails.beginTime

        val sisaQuota = (recomDetails.quota ?: 0) - (recomDetails.registrants ?: 0)
        binding.tvRecomQuota.text = buildString {
            append("Sisa Quota: ")
            append(sisaQuota)
        }

        binding.imgRecomLogo.loadImage(recomDetails.imageLogo)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

fun ImageView.loadImage(url: String?) {
    Glide.with(this.context)
        .load(url)
        .centerCrop()
        .into(this)
}
