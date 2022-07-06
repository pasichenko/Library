package com.makspasich.library.ui.filter_products_dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.makspasich.library.databinding.CatChipGroupItemChoiceBinding
import com.makspasich.library.databinding.FilterProductsFragmentBinding
import com.makspasich.library.models.TagName


class FilterProductsDialog(private val filterListener: FilterListener) : DialogFragment() {

    private lateinit var binding: FilterProductsFragmentBinding
    private val viewModel: FilterProductsViewModel by viewModels()

    interface FilterListener {

        fun onFilter(filters: List<TagName>)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        binding = FilterProductsFragmentBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Filtering")
            .setView(binding.root)
            .setPositiveButton("Save") { _: DialogInterface?, _: Int -> onSearchClicked() }
            .setNegativeButton("Cancel") { _: DialogInterface?, _: Int -> onCancelClicked() }
            .setNeutralButton("Clear") { _: DialogInterface?, _: Int -> onClearClicked() }

        viewModel.allTagsLiveData.observe(this) { tags ->
            binding.chipGroup.removeAllViews()
            for (tag in tags) {
                val chip = CatChipGroupItemChoiceBinding.inflate(
                    layoutInflater,
                    binding.chipGroup,
                    false
                )
                chip.root.text = tag.name
                viewModel.filterTagsLiveData.value?.let { productTags ->
                    for (productTag in productTags) {
                        if (productTag.key == tag.key) {
                            chip.root.isChecked = true
                        }
                    }
                }
                chip.root.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        viewModel.addTagProduct(tag)
                    } else {
                        viewModel.removeTagProduct(tag)
                    }
                }
                binding.chipGroup.addView(chip.root)
            }
        }

        return builder.create()
    }

    private fun onSearchClicked() {
        filterListener.onFilter(viewModel.filterTagsLiveData.value!!.toList())
        dismiss()
    }

    private fun onCancelClicked() {
        dismiss()
    }

    private fun onClearClicked() {
        filterListener.onFilter(ArrayList())
        dismiss()
    }
}