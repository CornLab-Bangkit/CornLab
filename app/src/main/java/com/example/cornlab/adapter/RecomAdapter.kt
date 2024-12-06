package com.example.cornlab.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cornlab.data.response.ListNotesItem
import com.example.cornlab.databinding.ItemRowRecomBinding

class RecomAdapter(private val listener: OnItemClickListener) : ListAdapter<ListNotesItem, RecomAdapter.RecomViewHolder>(DIFF_CALLBACK) {

    interface OnItemClickListener {
        fun onItemClick(recomId: Int?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecomViewHolder {
        val binding = ItemRowRecomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecomViewHolder, position: Int) {
        val recom = getItem(position)
        holder.bind(recom)
    }

    inner class RecomViewHolder(private val binding: ItemRowRecomBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recom: ListNotesItem) {
            // Bind data name, summary, and imageCover
            binding.tvRecomName.text = recom.name
            binding.tvRecomSummary.text = recom.summary
            Glide.with(binding.root.context).load(recom.imageCover).into(binding.ivImageCover)

            binding.root.setOnClickListener {
                listener.onItemClick(recom.id)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListNotesItem>() {
            override fun areItemsTheSame(oldItem: ListNotesItem, newItem: ListNotesItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListNotesItem, newItem: ListNotesItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
