package com.makspasich.library.ui.activeProduct

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.makspasich.library.BarcodeScannerActivity
import com.makspasich.library.databinding.FragmentActiveProductBinding
import com.makspasich.library.models.Product
import com.makspasich.library.ui.addproduct.AddProductDialog

class ActiveProductFragment : Fragment() {
    private lateinit var binding: FragmentActiveProductBinding
    private val activeProductViewModel: ActiveProductViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentActiveProductBinding.inflate(inflater, container, false)
        val query: Query = FirebaseDatabase.getInstance().reference.child("active")
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = DataAdapter(query)
        binding.fab.setOnClickListener {
            val intent = Intent(context, BarcodeScannerActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
        return binding.root
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
                                    val action = ActiveProductFragmentDirections.actionOpenDetailProductFragment(keyProduct)
                                    findNavController().navigate(action)
                                }
                            } else {
                                AddProductDialog.newInstance(keyProduct).show(childFragmentManager, "tag")
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