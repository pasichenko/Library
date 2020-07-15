package com.makspasich.library.ui.products

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.makspasich.library.R
import com.makspasich.library.barcodescanner.LiveBarcodeScanningActivity
import com.makspasich.library.databinding.FragmentProductsBinding
import com.makspasich.library.models.Product

class ProductsFragment : Fragment() {
    private lateinit var binding: FragmentProductsBinding
    private val viewModel: ProductsViewModel by viewModels()
    private lateinit var adapter: DataAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProductsBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.fab.setOnClickListener {
            val intent = Intent(context, LiveBarcodeScanningActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
        viewModel.query.observe(viewLifecycleOwner, Observer {
            adapter = DataAdapter(it)
            binding.recyclerView.adapter = adapter
        })
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.setFiltering(ProductsFilterType.ALL_PRODUCTS)
    }

    override fun onStop() {
        super.onStop()
        adapter.cleanupListener()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.products_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                R.id.menu_filter -> {
                    showFilteringPopUpMenu()
                    true
                }
                else -> false
            }

    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>(R.id.menu_filter) ?: return
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(R.menu.filter_products, menu)

            setOnMenuItemClickListener {
                viewModel.setFiltering(
                        when (it.itemId) {
                            R.id.all_products -> ProductsFilterType.ALL_PRODUCTS
                            R.id.sort_by_name -> ProductsFilterType.SORT_BY_NAME
                            R.id.sort_by_size -> ProductsFilterType.SORT_BY_SIZE
                            else -> ProductsFilterType.ALL_PRODUCTS
                        }
                )
                true
            }
            show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.let { intent ->
                intent.getStringExtra("keyProduct")?.let { keyProduct ->
                    Firebase.database.reference.child("active").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.child(keyProduct).exists()) {
                                val store = snapshot.child(keyProduct).getValue<Product>()
                                store?.let {
                                    val action = ProductsFragmentDirections.actionOpenDetailProductFragment(keyProduct)
                                    findNavController().navigate(action)
                                }
                            } else {
                                val action = ProductsFragmentDirections
                                        .actionAddEditProductFragment(
                                                keyProduct = keyProduct,
                                                isNewProduct = true,
                                                title = getString(R.string.add_product))
                                findNavController().navigate(action)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                }
            }
        }
    }

    companion object {
        const val REQUEST_CODE = 9001
    }
}