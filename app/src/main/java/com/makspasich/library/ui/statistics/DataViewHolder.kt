package com.makspasich.library.ui.statistics

import androidx.recyclerview.widget.RecyclerView
import com.makspasich.library.R
import com.makspasich.library.databinding.ItemStatisticBinding
import com.makspasich.library.models.ProductStatistic

class DataViewHolder(private val binding: ItemStatisticBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(product: ProductStatistic?) {
        product?.let {
            binding.nameTv.text = it.name
            binding.countActiveTv.text = itemView.context.getString(R.string.count_active, it.countActive)
            binding.countArchiveTv.text = itemView.context.getString(R.string.count_archive, it.countArchive)
        }
    }
}