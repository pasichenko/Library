package com.makspasich.library.ui.addeditproduct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.makspasich.library.EventObserver
import com.makspasich.library.R
import com.makspasich.library.databinding.AddEditProductFragmentBinding
import com.makspasich.library.databinding.CatChipGroupItemBinding
import com.makspasich.library.databinding.CatChipGroupItemChoiceBinding
import com.makspasich.library.formatDate
import com.makspasich.library.twoWayBinding
import com.makspasich.library.ui.addname.AddNameDialog


class AddEditProductFragment : Fragment() {

    private lateinit var binding: AddEditProductFragmentBinding
    private val viewModel: AddEditProductViewModel by viewModels()
    private val args: AddEditProductFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddEditProductFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.start(args.keyProduct, args.isNewProduct)
        binding.saveFab.setOnClickListener {
            viewModel.saveProduct()
            viewModel.updateQRState()
        }
        setupFieldsLiveData()
        setupNavigation()
        setupPlaceholderChipName()
        setupChipNamesData()
        viewModel.timestampLiveData.observe(viewLifecycleOwner) {
            binding.timestampEt.setText(it.formatDate("dd MMM yyyy"))
        }
        viewModel.expirationTimestampLiveData.observe(viewLifecycleOwner) {
            binding.expirationTimestampEt.setText(it.formatDate("dd MMM yyyy"))
        }

        binding.timestampEt.setOnClickListener {
            val picker = materialDatePicker { selection: Long? ->
                viewModel.setTimestampProduct(selection)
            }
            picker.show(childFragmentManager, picker.toString())
        }
        binding.expirationTimestampEt.setOnClickListener {
            val picker = materialDatePicker { selection: Long? ->
                viewModel.setExpirationTimestampProduct(selection)
            }
            picker.show(childFragmentManager, picker.toString())
        }
//        viewModel.sizeLiveData.observe(viewLifecycleOwner){
//            binding.sizeSlider.value = it.toFloat()
//        }
//        binding.sizeSlider.addOnChangeListener(Slider.OnChangeListener { slider, value, fromUser ->
//            viewModel.setSizeProduct(BigDecimal(value.toString()).setScale(2,RoundingMode.FLOOR))
//        })
    }

    private fun materialDatePicker(listener: MaterialPickerOnPositiveButtonClickListener<Long>): MaterialDatePicker<Long> {
        val builder = MaterialDatePicker.Builder.datePicker()
        builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        val picker: MaterialDatePicker<Long> = builder.build()
        picker.addOnPositiveButtonClickListener(listener)
        return picker
    }

    private fun setupFieldsLiveData() {
        binding.keyEt.twoWayBinding(
            requireParentFragment(),
            viewModel.keyLiveData
        ) { viewModel.setKeyProduct(it) }
        binding.nameProductEt.twoWayBinding(
            requireParentFragment(),
            viewModel.nameLiveData
        ) { viewModel.setNameProduct(it) }
        binding.sizeEt.twoWayBinding(
            requireParentFragment(),
            viewModel.sizeLiveData
        ) { viewModel.setSizeProduct(it) }

    }

    private fun setupNavigation() {
        viewModel.productUpdatedEvent.observe(viewLifecycleOwner,
            EventObserver {
                if (args.isNewProduct) {
                    val action = AddEditProductFragmentDirections
                        .actionAddEditProductFragmentToNavActive()
                    findNavController().navigate(action)
                } else {
                    val action = AddEditProductFragmentDirections
                        .actionAddEditProductFragmentToDetailProductFragment(args.keyProduct)
                    findNavController().navigate(action)
                }
            })
    }

    private fun setupPlaceholderChipName() {
        val chipPlaceholder =
            CatChipGroupItemBinding.inflate(layoutInflater, binding.chipsNames, false)
        chipPlaceholder.root.apply {
            text = context.getString(R.string.data_loading)
            isCheckable = false
        }
        binding.chipsNames.addView(chipPlaceholder.root)
    }

    private fun setupChipNamesData() {
        viewModel.dataLoading.observe(viewLifecycleOwner) { isLoading ->
            if (!isLoading) {
                viewModel.allTagsLiveData.observe(viewLifecycleOwner) { tags ->
                    binding.chipsNames.removeAllViews()
                    for (tag in tags) {
                        val chip = CatChipGroupItemChoiceBinding.inflate(
                            layoutInflater,
                            binding.chipsNames,
                            false
                        )
                        chip.root.text = tag.name
                        viewModel.productTagsLiveData.value?.let { productTags ->
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
                        binding.chipsNames.addView(chip.root)
                    }
                    val chipAdd = getChipAdd(text = "Add name") {
                        AddNameDialog().show(childFragmentManager, "addName")
                    }
                    binding.chipsNames.addView(chipAdd)
                }
            }
        }
    }


    private fun getChipAdd(text: String, onClickListener: View.OnClickListener): Chip {
        val chipBinding = CatChipGroupItemBinding.inflate(layoutInflater, binding.chipsNames, false)
        chipBinding.root.text = text
        chipBinding.root.setOnClickListener(onClickListener)
        return chipBinding.root
    }
}