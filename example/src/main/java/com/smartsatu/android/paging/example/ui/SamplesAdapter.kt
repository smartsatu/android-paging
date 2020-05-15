package com.smartsatu.android.paging.example.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.smartsatu.android.paging.example.BR
import com.smartsatu.android.paging.example.R
import com.smartsatu.android.paging.example.databinding.ItemSampleBinding

class SamplesAdapter(private val itemCallback: ItemCallback) : ListAdapter<Sample, BindingViewHolder<ItemSampleBinding>>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<ItemSampleBinding> {
        val binding = DataBindingUtil.inflate<ItemSampleBinding>(LayoutInflater.from(parent.context),
                R.layout.item_sample, parent, false)
        return BindingViewHolder<ItemSampleBinding>(binding).apply {
            setOnClickListener(object : BindingViewHolder.OnClickListener<ItemSampleBinding> {
                override fun onClick(view: View, holder: BindingViewHolder<ItemSampleBinding>) {
                    itemCallback.onItemClick(getItem(holder.adapterPosition))
                }
            })
        }
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ItemSampleBinding>, position: Int) {
        holder.bind(BR.sample, getItem(holder.getAdapterPosition()))
    }

    interface ItemCallback {
        fun onItemClick(sample: Sample)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Sample>() {

        override fun areItemsTheSame(oldItem: Sample, newItem: Sample) = oldItem == newItem

        override fun areContentsTheSame(oldItem: Sample, newItem: Sample) = oldItem == newItem
    }
}