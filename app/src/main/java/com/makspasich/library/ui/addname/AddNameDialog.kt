package com.makspasich.library.ui.addname

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.makspasich.library.databinding.AddNameFragmentBinding
import com.makspasich.library.twoWayBinding


class AddNameDialog : DialogFragment() {

    private lateinit var binding: AddNameFragmentBinding
    private val viewModel: AddNameViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        binding = AddNameFragmentBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add name product")
                .setView(binding.root)
                .setPositiveButton("Save") { _: DialogInterface?, _: Int -> viewModel.addNameProduct() }
                .setNegativeButton("Cancel") { _: DialogInterface?, _: Int -> requireDialog().dismiss() }

        return builder.create()
    }

    @SuppressLint("FragmentLiveDataObserve")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.nameProductEt.twoWayBinding(requireParentFragment(), viewModel.nameProduct) {
            viewModel.setNameProduct(
                it
            )
        }
    }
}