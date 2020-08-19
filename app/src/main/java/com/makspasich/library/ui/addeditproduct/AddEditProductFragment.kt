package com.makspasich.library.ui.addeditproduct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.makspasich.library.EventObserver
import com.makspasich.library.R
import com.makspasich.library.databinding.AddEditProductFragmentBinding
import com.makspasich.library.twoWayBinding
import com.makspasich.library.ui.addname.AddNameDialog
import com.makspasich.library.ui.addsize.AddSizeDialog


class AddEditProductFragment : Fragment() {

    private lateinit var binding: AddEditProductFragmentBinding
    private val viewModel: AddEditProductViewModel by viewModels()
    private val args: AddEditProductFragmentArgs by navArgs()

    private lateinit var arrayMonthAdapter: ArrayAdapter<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = AddEditProductFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.start(args.keyProduct, args.isNewProduct)
        setupFab()
        setupFieldsLiveData()
        setupNavigation()
        setupPlaceholderChipName()
        setupPlaceholderChipSize()
        setupChipNamesData()
        setupChipSizesData()
        setupMonthSpinner()
    }

    private fun setupFab() {
        binding.saveFab.setOnClickListener {
            viewModel.saveProduct()
        }
    }

    private fun setupFieldsLiveData() {
        binding.keyEt.twoWayBinding(requireParentFragment(), viewModel.keyLiveData, { viewModel.setKeyProduct(it) })
        binding.yearEt.twoWayBinding(requireParentFragment(), viewModel.yearLiveData, { viewModel.setYearProduct(it) })
        binding.expirationDateEt.twoWayBinding(requireParentFragment(), viewModel.expirationDateLiveData, { viewModel.setExpirationDateProduct(it) })
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
        val chipPlaceholder = layoutInflater.inflate(R.layout.cat_chip_group_item_choice, binding.chipsNames, false) as Chip
        chipPlaceholder.apply {
            text = context.getString(R.string.data_loading)
            isCheckable = false
        }
        binding.chipsNames.addView(chipPlaceholder)
    }

    private fun setupPlaceholderChipSize() {
        val chipPlaceholder = layoutInflater.inflate(R.layout.cat_chip_group_item_choice, binding.chipsSizes, false) as Chip
        chipPlaceholder.apply {
            text = context.getString(R.string.data_loading)
            isCheckable = false
        }
        binding.chipsSizes.addView(chipPlaceholder)
    }

    private fun setupChipNamesData() {
        viewModel.namesLiveData.observe(viewLifecycleOwner, Observer { list ->
            binding.chipsNames.removeAllViews()
            var checkedId = -1
            for (productName in list) {
                val chip = layoutInflater.inflate(R.layout.cat_chip_group_item_choice, binding.chipsNames, false) as Chip
                chip.id = ViewCompat.generateViewId()
                chip.text = productName.name
                viewModel.nameLiveData.value?.let {
                    if (it == productName.name) {
                        checkedId = chip.id
                    }
                }
                chip.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        viewModel.setNameProduct(productName.name)
                    } else {
                        if (productName.name == viewModel.nameLiveData.value) {
                            viewModel.setNameProduct(null)
                        }
                    }
                }
                binding.chipsNames.addView(chip)
            }
            if (checkedId != -1) {
                binding.chipsNames.check(checkedId)
            }
            val chipAdd = getChipAdd(
                    text = "Add name",
                    root = binding.chipsNames,
                    onClickListener = View.OnClickListener {
                        AddNameDialog().show(childFragmentManager, "addName")
                    })
            binding.chipsNames.addView(chipAdd)
        })
    }

    private fun setupChipSizesData() {
        viewModel.sizesLiveData.observe(viewLifecycleOwner, Observer { list ->
            binding.chipsSizes.removeAllViews()
            var checkedId = -1
            for (productSize in list) {
                val chip = layoutInflater.inflate(R.layout.cat_chip_group_item_choice, binding.chipsSizes, false) as Chip
                chip.id = ViewCompat.generateViewId()
                chip.text = productSize.size
                viewModel.sizeLiveData.value?.let {
                    if (it == productSize.size) {
                        checkedId = chip.id
                    }
                }
                chip.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        viewModel.setSizeProduct(productSize.size)
                    } else {
                        if (productSize.size == viewModel.sizeLiveData.value) {
                            viewModel.setSizeProduct(null)
                        }
                    }
                }
                binding.chipsSizes.addView(chip)
            }
            if (checkedId != -1) {
                binding.chipsSizes.check(checkedId)
            }
            val chipAdd = getChipAdd(
                    text = "Add size",
                    root = binding.chipsSizes,
                    onClickListener = View.OnClickListener {
                        AddSizeDialog().show(childFragmentManager, "addSize")
                    })
            binding.chipsSizes.addView(chipAdd)
        })
    }

    private fun setupMonthSpinner() {
        val listMonth: List<String> = resources.getStringArray(R.array.months).toList()
        arrayMonthAdapter = ArrayAdapter(requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                listMonth)
        binding.monthEt.setAdapter(arrayMonthAdapter)
        viewModel.monthLiveData.observe(viewLifecycleOwner, Observer {
            for (month in listMonth) {
                if (month == it) {
                    binding.monthEt.setText(it, false)
                    break
                }
            }
        })
        binding.monthEt.setOnItemClickListener { _, _, i, _ ->
            val month = arrayMonthAdapter.getItem(i)
            viewModel.setMonthProduct(month)
            binding.monthEt.setText(month, false)
        }
        initializeAutoCompleteSpinners(binding.monthEt)
    }

    private fun getChipAdd(text: String, root: ViewGroup, onClickListener: View.OnClickListener): Chip {
        val chip = layoutInflater.inflate(R.layout.cat_chip_group_item, root, false) as Chip
        chip.id = ViewCompat.generateViewId()
        chip.text = text
        chip.setOnClickListener(onClickListener)
        return chip
    }

    private fun initializeAutoCompleteSpinners(autoCompleteTextView: AutoCompleteTextView) {
        autoCompleteTextView.onFocusChangeListener = View.OnFocusChangeListener { v: View?, hasFocus: Boolean -> if (hasFocus) autoCompleteTextView.showDropDown() }
        autoCompleteTextView.setOnTouchListener { v: View?, event: MotionEvent? ->
            autoCompleteTextView.showDropDown()
            false
        }
    }
}