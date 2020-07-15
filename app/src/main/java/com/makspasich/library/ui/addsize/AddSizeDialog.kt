package com.makspasich.library.ui.addsize

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.makspasich.library.R
import com.makspasich.library.databinding.AddSizeFragmentBinding
import com.makspasich.library.twoWayBinding

class AddSizeDialog : DialogFragment() {

    private lateinit var binding: AddSizeFragmentBinding
    private val viewModel: AddSizeViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val root = LayoutInflater.from(requireContext()).inflate(R.layout.add_size_fragment, null)
        binding = AddSizeFragmentBinding.bind(root)
        val builder = AlertDialog.Builder(requireContext())
                .setTitle("Add size product")
                .setView(binding.root)
                .setPositiveButton("Save") { dialog: DialogInterface?, which: Int -> viewModel.addSizeProduct() }
                .setNegativeButton("Cancel") { dialog: DialogInterface?, which: Int -> requireDialog().dismiss() }
        return builder.create()
    }

    @SuppressLint("FragmentLiveDataObserve")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.sizeProductEt.twoWayBinding(requireParentFragment(), viewModel.sizeProduct, { viewModel.setSizeProduct(it) })
    }
}