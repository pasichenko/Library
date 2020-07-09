package com.makspasich.library.ui.addproduct

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputLayout
import com.makspasich.library.R
import com.makspasich.library.afterTextChanged
import com.makspasich.library.databinding.AddProductBinding
import com.makspasich.library.models.ProductName
import com.makspasich.library.models.ProductSize
import com.makspasich.library.twoWayBinding

class AddProductDialog private constructor() : DialogFragment() {
    private lateinit var binding: AddProductBinding
    private val viewModel: AddProductViewModel by viewModels()
    private lateinit var arrayNamesAdapter: ArrayAdapter<ProductName>
    private lateinit var arraySizesAdapter: ArrayAdapter<ProductSize>
    private lateinit var arrayMonthAdapter: ArrayAdapter<String>
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val root = LayoutInflater.from(requireContext()).inflate(R.layout.add_product, null)
        binding = AddProductBinding.bind(root)
        val builder = AlertDialog.Builder(requireContext())
                .setTitle("Save product")
                .setView(binding.root)
                .setPositiveButton("Save") { dialog: DialogInterface?, which: Int -> viewModel.addProduct() }
                .setNegativeButton("Cancel") { dialog: DialogInterface?, which: Int -> requireDialog().dismiss() }
        return builder.create()
    }

    @SuppressLint("FragmentLiveDataObserve")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupFieldsLiveData()
        arguments?.let {
            viewModel.start(it.getString("key"));
            viewModel.setYearProduct(it.getString("year"))

        }
        viewModel.namesLiveData.observe(requireParentFragment(), Observer {
            arrayNamesAdapter = ArrayAdapter(requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    it)
            binding.nameEt.setAdapter(arrayNamesAdapter)
        })
        viewModel.sizesLiveData.observe(requireParentFragment(), Observer {
            arraySizesAdapter = ArrayAdapter(requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    it)
            binding.sizeEt.setAdapter(arraySizesAdapter)
        })
        val listMonth: MutableList<String> = mutableListOf("January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December")
        arrayMonthAdapter = ArrayAdapter(requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                listMonth)
        binding.monthEt.setAdapter(arrayMonthAdapter)
        initializeAutoCompleteSpinners(binding.nameTil, binding.nameEt, "Set name")
        initializeAutoCompleteSpinners(binding.sizeTil, binding.sizeEt, "Set size")
        initializeAutoCompleteSpinners(binding.monthTil, binding.monthEt, "Set size")

        binding.nameEt.setOnItemClickListener { _, _, i, _ -> viewModel.setNameProduct(arrayNamesAdapter.getItem(i)!!.name) }
        binding.sizeEt.setOnItemClickListener { _, _, i, _ -> viewModel.setSizeProduct(arraySizesAdapter.getItem(i)!!.size) }
        binding.monthEt.setOnItemClickListener { _, _, i, _ -> viewModel.setMonthProduct(arrayMonthAdapter.getItem(i)) }

    }

    private fun setupFieldsLiveData() {
        binding.keyEt.twoWayBinding(requireParentFragment(), viewModel.keyLiveData, { viewModel.setKeyProduct(it) })
        binding.yearEt.twoWayBinding(requireParentFragment(), viewModel.yearLiveData, { viewModel.setYearProduct(it) })
        binding.nameEt.twoWayBinding(requireParentFragment(), viewModel.nameLiveData, { viewModel.setNameProduct(it) })
        binding.sizeEt.twoWayBinding(requireParentFragment(), viewModel.sizeLiveData, { viewModel.setSizeProduct(it) })
        binding.monthEt.twoWayBinding(requireParentFragment(), viewModel.monthLiveData, { viewModel.setMonthProduct(it) })
    }

    private fun initializeAutoCompleteSpinners(textInputLayout: TextInputLayout, autoCompleteTextView: AutoCompleteTextView, errorText: String) {
        autoCompleteTextView.onFocusChangeListener = OnFocusChangeListener { v: View?, hasFocus: Boolean -> if (hasFocus) autoCompleteTextView.showDropDown() }
        autoCompleteTextView.setOnTouchListener { v: View?, event: MotionEvent? ->
            autoCompleteTextView.showDropDown()
            false
        }
        autoCompleteTextView.afterTextChanged {
            if (it.isEmpty()) {
                textInputLayout.error = errorText
            } else {
                textInputLayout.error = null
            }
        }
    }

    companion object {
        fun newInstance(keyProduct: String): AddProductDialog {
            val dialog = AddProductDialog()
            val bundle = Bundle()
            bundle.putString("key", keyProduct)
            bundle.putString("year", keyProduct.split("_")[1])
            dialog.arguments = bundle
            return dialog
        }
    }
}