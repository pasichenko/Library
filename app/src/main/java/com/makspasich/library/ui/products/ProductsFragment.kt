package com.makspasich.library.ui.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.makspasich.library.R
import com.makspasich.library.databinding.FragmentProductsBinding
import com.makspasich.library.model.Product
import com.makspasich.library.screens.products.ProductsScreen
import com.makspasich.library.theme.LibraryTheme
import com.makspasich.library.util.PRODUCT_KEY_PATTERN


class ProductsFragment : Fragment() {
    private lateinit var binding: FragmentProductsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductsBinding.inflate(inflater, container, false)
        binding.composeView.setContent {
            LibraryTheme {
                ProductsScreen(onProductItemClick = { key, view ->
                    Navigation.createNavigateOnClickListener(
                        ProductsFragmentDirections.actionOpenDetailProductFragment(key)
                    )
                        .onClick(view)
                })
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fab.setOnClickListener {
            val optionsBuilder = GmsBarcodeScannerOptions.Builder()
            val gmsBarcodeScanner =
                GmsBarcodeScanning.getClient(requireContext(), optionsBuilder.build())
            gmsBarcodeScanner
                .startScan()
                .addOnSuccessListener { barcode ->
                    val checkNotNull = checkNotNull(barcode.rawValue)
                    if (PRODUCT_KEY_PATTERN.containsMatchIn(checkNotNull)) {
                        function(checkNotNull)
                    } else {
                        Snackbar.make(binding.root, "Невідомий QR", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }
                .addOnFailureListener { e: Exception ->
                    Snackbar.make(binding.root, "Помилка при скануванні QR", Snackbar.LENGTH_SHORT)
                        .show()
                }
                .addOnCanceledListener { }
        }
    }

    private fun function(keyProduct: String) {
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