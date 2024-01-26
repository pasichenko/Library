package com.makspasich.library.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.makspasich.library.EventObserver
import com.makspasich.library.R
import com.makspasich.library.databinding.DetailProductFragmentBinding
import com.makspasich.library.databinding.StateButtonBinding
import com.makspasich.library.formatDate
import com.makspasich.library.models.State
import com.makspasich.library.toText

class DetailProductFragment : Fragment(), MenuProvider {

    private lateinit var binding: DetailProductFragmentBinding
    private val viewModel: DetailProductViewModel by viewModels()
    private val args: DetailProductFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DetailProductFragmentBinding.inflate(inflater, container, false)
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
            }
        }
        binding.editFab.setOnClickListener {
            viewModel.editTask()
        }
        for (state in State.entries) {
            val stateBinding = StateButtonBinding.inflate(layoutInflater)
            stateBinding.root.text = state.toText()
            stateBinding.root.setOnClickListener { viewModel.updateState(state) }
            viewModel.product.observe(viewLifecycleOwner) {
                if (it.state == state) {
                    stateBinding.root.isChecked = true
                }
            }
            binding.statusContainerBtn.addView(stateBinding.root)
        }
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

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
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

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.product_detail_menu, menu)
    }
}
