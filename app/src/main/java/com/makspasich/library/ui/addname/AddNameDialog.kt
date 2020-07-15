package com.makspasich.library.ui.addname

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.makspasich.library.R
import com.makspasich.library.databinding.AddNameFragmentBinding

import com.makspasich.library.twoWayBinding


class AddNameDialog : DialogFragment() {

    private lateinit var binding: AddNameFragmentBinding
    private val viewModel: AddNameViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val root = LayoutInflater.from(requireContext()).inflate(R.layout.add_name_fragment, null)
        binding = AddNameFragmentBinding.bind(root)
        val builder = AlertDialog.Builder(requireContext())
                .setTitle("Add name product")
                .setView(binding.root)
                .setPositiveButton("Save") { dialog: DialogInterface?, which: Int -> viewModel.addNameProduct() }
                .setNegativeButton("Cancel") { dialog: DialogInterface?, which: Int -> requireDialog().dismiss() }
        return builder.create()
    }

    @SuppressLint("FragmentLiveDataObserve")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.nameProductEt.twoWayBinding(requireParentFragment(), viewModel.nameProduct, { viewModel.setNameProduct(it) })
    }
}