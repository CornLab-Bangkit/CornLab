package com.example.cornlab.ui.history

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cornlab.R
import com.example.cornlab.data.local.HistoryEntity
import com.example.cornlab.databinding.ItemRowHistoryBinding

class HistoryAdapter(
    private val viewModel: HistoryViewModel
) : ListAdapter<HistoryEntity, HistoryAdapter.HistoryViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HistoryEntity>() {
            override fun areItemsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemRowHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HistoryViewHolder(private val binding: ItemRowHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(history: HistoryEntity) {
            binding.ivImage.setImageURI(Uri.parse(history.imageUri))

            binding.tvResult.text = history.result ?: "No Result"
            binding.tvDate.text = history.createdAt ?: "Unknown Date"

            binding.root.setOnClickListener {
                val historyItem = getItem(bindingAdapterPosition)
                historyItem.id?.let { id ->
                    val bundle = Bundle().apply {
                        putInt("historyId", id)
                    }
                    it.findNavController().navigate(R.id.action_history_to_detailHistory, bundle)
                }
            }

            binding.btnDelete.setOnClickListener {
                history.id?.let { id ->
                    viewModel.deleteHistoryById(id)
                }
            }
        }
    }
}
