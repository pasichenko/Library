package com.makspasich.library.ui.detail

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.makspasich.library.EventObserver
import com.makspasich.library.R
import com.makspasich.library.databinding.DetailProductFragmentBinding
import com.makspasich.library.ui.addproduct.AddProductDialog

class DetailProductFragment : Fragment() {

    private lateinit var binding: DetailProductFragmentBinding
    private val viewModel: DetailProductViewModel by viewModels()
    private val args: DetailProductFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DetailProductFragmentBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.start(args.keyProduct)
        viewModel.product.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.keyTv.text = it.key
                binding.uidTv.text = it.uid
                binding.nameTv.text = it.name
                binding.sizeTv.text = it.size
                binding.monthTv.text = it.month
                binding.statusTv.apply {
                    text = if (it.isActive){
                        setBackgroundColor(resources.getColor(R.color.active))
                        "Active"
                    }else{
                        setBackgroundColor(resources.getColor(R.color.archive))
                        "Archive"
                    }
                }
            }
        })
        binding.editFab.setOnClickListener {
            viewModel.editTask()
        }
        setupNavigation()
    }

    private fun setupNavigation() {
        viewModel.deleteTaskEvent.observe(viewLifecycleOwner, EventObserver {
            val action = DetailProductFragmentDirections.actionDetailProductFragmentToNavHome()
            findNavController().navigate(action)
        })
        viewModel.editTaskEvent.observe(viewLifecycleOwner, EventObserver {
            AddProductDialog.newInstance(args.keyProduct).show(childFragmentManager, "tag")
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete -> {
                viewModel.deleteProduct()
                true
            }R.id.menu_archive -> {
                viewModel.archiveProduct()
                true
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.product_detail_menu, menu)
    }
}
