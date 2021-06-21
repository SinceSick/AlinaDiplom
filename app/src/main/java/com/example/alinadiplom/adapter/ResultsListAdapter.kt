package com.example.alinadiplom.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alinadiplom.databinding.ItemResultBinding

class ResultsListAdapter(private val resultListener: ResultListener, var results: ArrayList<String>) : RecyclerView.Adapter<ResultsListAdapter.ResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultViewHolder(this.resultListener, binding)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.binding.nameTV.text = results[position]
    }

    override fun getItemCount(): Int {
        return results.size
    }

    class ResultViewHolder(private val resultListener: ResultListener, val binding: ItemResultBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            binding.itemCV.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            resultListener.onResultClick(adapterPosition)
        }
    }

    interface ResultListener {
        fun onResultClick(position: Int)
    }
}