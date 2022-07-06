package com.makspasich.library.ui.detail

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.makspasich.library.EventObserver
import com.makspasich.library.R
import com.makspasich.library.databinding.DetailProductFragmentBinding
import com.makspasich.library.formatDate
import com.makspasich.library.models.State

class DetailProductFragment : Fragment() {

    private lateinit var binding: DetailProductFragmentBinding
    private val viewModel: DetailProductViewModel by viewModels()
    private val args: DetailProductFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DetailProductFragmentBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.start(args.keyProduct)
        viewModel.product.observe(viewLifecycleOwner) {
            it?.let {
                binding.keyTv.text = String.format("Key: %s", it.key)
                binding.uidTv.text = String.format("UID: %s", it.uid)
                binding.nameTv.text = String.format("Name: %s", it.name)
                binding.sizeTv.text = String.format("Size: %s", it.size.toString())
                binding.timestampTv.text =
                    String.format("Timestamp: %s", it.timestamp?.formatDate("yyyy-MM-dd"))
                binding.expirationTimestampTv.text =
                    String.format(
                        "Expired at: %s",
                        it.expirationTimestamp?.formatDate("yyyy-MM-dd")
                    )
//                binding.statusTv.text = String.format("Status: %s", it.state)
                when (it.state) {
                    State.CREATED -> binding.statusCreatedBtn.isChecked = true
                    State.UNDERGROUND -> binding.statusUndergroundBtn.isChecked = true
                    State.FOREGROUND -> binding.statusForegroundBtn.isChecked = true
                    else -> binding.statusUndefinedBtn.isChecked = true
                }
            }
        }
        binding.editFab.setOnClickListener {
            viewModel.editTask()
        }
        binding.statusCreatedBtn.setOnClickListener { viewModel.updateState(State.CREATED) }
        binding.statusUndergroundBtn.setOnClickListener { viewModel.updateState(State.UNDERGROUND) }
        binding.statusForegroundBtn.setOnClickListener { viewModel.updateState(State.FOREGROUND) }
        binding.statusUndefinedBtn.setOnClickListener { viewModel.updateState(State.UNDEFINED) }
        binding.statusDeletedBtn.setOnClickListener { viewModel.updateState(State.DELETED) }
        setupNavigation()
    }

    private fun setupNavigation() {
        viewModel.deleteTaskEvent.observe(viewLifecycleOwner, EventObserver {
            val action = DetailProductFragmentDirections.actionDetailProductFragmentToNavHome()
            findNavController().navigate(action)
        })
        viewModel.editTaskEvent.observe(viewLifecycleOwner, EventObserver {
            val action = DetailProductFragmentDirections
                .actionDetailProductFragmentToAddEditProductFragment(
                    args.keyProduct,
                    false,
                    getString(R.string.edit_product)
                )
            findNavController().navigate(action)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.title_dialog_delete_product))
                    .setMessage(getString(R.string.message_delete_product))
                    .setIcon(R.drawable.ic_warning)
                    .setPositiveButton(android.R.string.ok) { _, _ -> viewModel.deleteProduct() }
                    .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
                    .show()
                true
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.product_detail_menu, menu)
    }
}
