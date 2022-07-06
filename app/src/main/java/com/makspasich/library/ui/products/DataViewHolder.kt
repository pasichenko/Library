package com.makspasich.library.ui.products

import android.view.LayoutInflater
import androidx.core.view.ViewCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.makspasich.library.R
import com.makspasich.library.databinding.ItemProductBinding
import com.makspasich.library.formatDate
import com.makspasich.library.models.Product
import com.makspasich.library.toText

class DataViewHolder(
    private val binding: ItemProductBinding
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(product: Product?) {
        product?.let {
            binding.nameTv.text = it.name
            binding.sizeTv.text = it.size
            binding.timestampTv.text = it.timestamp?.formatDate("yyyy-MM-dd")
            binding.expirationTimestampTv.text = it.expirationTimestamp?.formatDate("yyyy-MM-dd")
            binding.stateTv.text = it.state.toText()
            binding.idTv.text = it.key
            itemView.setOnClickListener { view ->
                val action = ProductsFragmentDirections.actionOpenDetailProductFragment(it.key!!)
                Navigation.createNavigateOnClickListener(action).onClick(view)
            }
            binding.tagsChipGroup.removeAllViews()

            for (tag in it.tags.entries) {
                val chip = LayoutInflater.from(binding.root.context).inflate(
                    R.layout.cat_chip_group_item_assist,
                    binding.tagsChipGroup,
                    false
                ) as Chip
                chip.id = ViewCompat.generateViewId()
                chip.text = tag.value.name
                binding.tagsChipGroup.addView(chip)
            }
        }
    }


}