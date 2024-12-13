package com.example.cornlab.ui.home.corn

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
import com.example.cornlab.databinding.FragmentHomeCornBinding
import com.example.cornlab.ui.BaseFragment

class CornFragment : BaseFragment(), RecomAdapter.OnItemClickListener {

    private var _binding: FragmentHomeCornBinding? = null
    private val binding get() = _binding!!
    private val cornViewModel: CornViewModel by viewModels()
    private lateinit var adapter: RecomAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeCornBinding.inflate(inflater, container, false)
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
        binding.rvCorn.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCorn.setHasFixedSize(true)
        binding.rvCorn.adapter = adapter
    }

    private fun observeViewModel() {
        cornViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        cornViewModel.recomList.observe(viewLifecycleOwner) { recoms ->
            adapter.submitList(recoms)
            binding.rvCorn.visibility = if (recoms.isEmpty()) View.GONE else View.VISIBLE
        }

        cornViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                showError(it)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvCorn.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun loadData() {
        cornViewModel.getCornRecoms()
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
            findNavController().navigate(R.id.action_corn_to_detail, bundle)
        }
    }
}