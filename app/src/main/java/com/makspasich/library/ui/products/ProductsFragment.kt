package com.makspasich.library.ui.products

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.makspasich.library.R
import com.makspasich.library.barcodescanner.LiveBarcodeScanningActivity
import com.makspasich.library.databinding.FragmentProductsBinding
import com.makspasich.library.databinding.TextViewStateBinding
import com.makspasich.library.databinding.TextViewYearBinding
import com.makspasich.library.models.Product
import com.makspasich.library.models.State
import com.makspasich.library.models.TagName
import com.makspasich.library.toText
import com.makspasich.library.ui.filter_products_dialog.FilterProductsDialog
import java.util.Calendar

class ProductsFragment : Fragment(), FilterProductsDialog.FilterListener, MenuProvider {
    private lateinit var binding: FragmentProductsBinding
    private val viewModel: ProductsViewModel by viewModels()
    private lateinit var adapter: DataAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.fab.setOnClickListener {
            val intent = Intent(context, LiveBarcodeScanningActivity::class.java)
            activityResultLauncher.launch(intent)
        }
        val productsCollection = Firebase.firestore.collection("products")
        adapter = object : DataAdapter(productsCollection) {}
        binding.recyclerView.adapter = adapter
        viewModel.query.observe(viewLifecycleOwner) { adapter.setQuery(it) }
        viewModel.filters
        adapter.productsLiveData.observe(viewLifecycleOwner) { products ->
            val mapYearStatesMap: MutableMap<Int, MutableMap<State, Int>> = HashMap()
            for (product in products) {
                product?.let {
                    val state = it.state
                    val calendarTimestamp = Calendar.getInstance()
                    calendarTimestamp.timeInMillis = it.timestamp ?: 0L
                    val yearTimestamp = calendarTimestamp.get(Calendar.YEAR)
                    calendarTimestamp.timeInMillis = it.timestamp ?: 0L
                    val yearExpirationTimestamp = calendarTimestamp.get(Calendar.YEAR)

                    if (mapYearStatesMap.containsKey(yearTimestamp)) {
                        mapYearStatesMap[yearTimestamp]!![state] =
                            (mapYearStatesMap[yearTimestamp]!![state] ?: 0) + 1
                    } else {
                        mapYearStatesMap.put(yearTimestamp, mutableMapOf(Pair(state, 1)))
                    }
                }
            }
            binding.listContainer.removeAllViews()
            mapYearStatesMap.entries.sortedByDescending { it.key }.forEach { yearEntry ->
                val inflate =
                    LayoutInflater.from(binding.listContainer.context)
                val textViewYearBinding = TextViewYearBinding.inflate(inflate)
                textViewYearBinding.root.text = yearEntry.key.toString()
                binding.listContainer.addView(textViewYearBinding.root)
                yearEntry.value.entries.sortedByDescending { it.key }.forEach { stateEntry ->
                    val textViewStateBinding = TextViewStateBinding.inflate(inflate)
                    textViewStateBinding.stateTv.text = stateEntry.key.toText()
                    textViewStateBinding.countStateTv.text = stateEntry.value.toString()
                    binding.listContainer.addView(textViewStateBinding.root)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.products_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
        when (menuItem.itemId) {
            R.id.menu_filter -> {
                FilterProductsDialog(this@ProductsFragment, viewModel.filters).show(
                    childFragmentManager,
                    "FilterProductsDialog"
                )
                true
            }

            else -> false
        }

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { intent ->
                intent.getStringExtra("keyProduct")?.let { keyProduct ->
                    Firebase.firestore.collection("products")
                        .document(keyProduct)
                        .get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                val product = document.toObject<Product>()
                                product?.let {
                                    val action =
                                        ProductsFragmentDirections.actionOpenDetailProductFragment(
                                            keyProduct
                                        )
                                    findNavController().navigate(action)
                                }
                            } else {
                                val action = ProductsFragmentDirections
                                    .actionAddEditProductFragment(
                                        keyProduct,
                                        true,
                                        getString(R.string.add_product)
                                    )
                                findNavController().navigate(action)
                            }
                        }
                }
            }
        }
    }

    override fun onFilter(filters: List<TagName>) {
        var query: Query = Firebase.firestore.collection("products")

        for (filter in filters) {
            query = query.whereEqualTo("tags.${filter.key}.key", filter.key)
        }
        viewModel.setQuery(query)
        viewModel.filters = filters
    }
}