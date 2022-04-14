package com.example.midv2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.midv2.database.History
import com.example.midv2.databinding.HistoryItemBinding

class HistoryAdapter(): ListAdapter<History, HistoryAdapter.HistoryViewHolder>(DiffCallback) {

    class HistoryViewHolder(private var binding: HistoryItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(history: History) {
            binding.textDate.text = history.dateTime.toString()
            binding.textGoal.text = history.goalTime.toString()+" min"
            if (history.isSuccess) {
                binding.textSuccess.text = "Succeed"
            } else {
                binding.textSuccess.text = "Failed"
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val viewHolder = HistoryViewHolder(
            HistoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        return viewHolder
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<History>() {
            override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
                return oldItem.dateTime == newItem.dateTime
            }

            override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
                return oldItem == newItem
            }
        }
    }

}