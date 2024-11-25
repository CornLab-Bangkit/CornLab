package com.example.cornlab.ui.home.husk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cornlab.adapter.RecomAdapter
import com.example.cornlab.R
import com.example.cornlab.databinding.FragmentHomeHuskBinding
import com.example.cornlab.ui.BaseFragment

class HuskFragment : BaseFragment(), RecomAdapter.OnItemClickListener {

    private var _binding: FragmentHomeHuskBinding? = null
    private val binding get() = _binding!!
    private val cornViewModel: HuskViewModel by viewModels()
    private lateinit var adapter: RecomAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeHuskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        loadData()

        isInternetAvailable.observe(viewLifecycleOwner) { isAvailable ->
            if (isAvailable) {
                loadData()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = RecomAdapter(this)
        binding.rvHusk.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHusk.setHasFixedSize(true)
        binding.rvHusk.adapter = adapter
    }

    private fun observeViewModel() {
        cornViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        cornViewModel.recomList.observe(viewLifecycleOwner) { recoms ->
            adapter.submitList(recoms)
            binding.rvHusk.visibility = if (recoms.isEmpty()) View.GONE else View.VISIBLE
        }

        cornViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                showError(it)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvHusk.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun loadData() {
        cornViewModel.getHuskRecoms()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(recomId: Int?) {
        recomId?.let {
            val bundle = Bundle().apply {
                putInt("recomId", it)
            }
            findNavController().navigate(R.id.action_husk_to_detail, bundle)
        }
    }
}
