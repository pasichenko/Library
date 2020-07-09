package com.makspasich.library.ui.archiveProduct

import androidx.recyclerview.widget.RecyclerView
import com.makspasich.library.databinding.ItemProductBinding
import com.makspasich.library.models.Product

class DataViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(product: Product?) {
        product?.let {
            binding.nameTv.text = it.name
            binding.sizeTv.text = it.size
            binding.yearTv.text = it.key!!.split("_")[1]
            binding.dateTv.text = it.month
            binding.idTv.text = it.key!!.split("_")[2]
        }
    }
}