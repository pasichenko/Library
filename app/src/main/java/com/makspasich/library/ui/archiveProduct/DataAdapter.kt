package com.makspasich.library.ui.archiveProduct

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.makspasich.library.databinding.ItemProductBinding
import com.makspasich.library.models.Product
import java.util.*

class DataAdapter(private val query: Query) : RecyclerView.Adapter<DataViewHolder>() {
    private var context: Context? = null
    private val childEventListener: ChildEventListener?
    private val productIds: MutableList<String?> = ArrayList()
    private val products: MutableList<Product?> = ArrayList()
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemProductBinding.inflate(inflater, parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val product = products[holder.adapterPosition]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun cleanupListener() {
        if (childEventListener != null) {
            query.removeEventListener(childEventListener)
        }
    }

    companion object {
        private val TAG = DataAdapter::class.java.simpleName
    }

    init {
        val childEventListener: ChildEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.key)
                productIds.add(dataSnapshot.key)
                val product = dataSnapshot.getValue(Product::class.java)
                products.add(product)
                notifyItemInserted(productIds.size - 1)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.key)
                val product = dataSnapshot.getValue(Product::class.java)
                val productKey = dataSnapshot.key
                val productIndex = productIds.indexOf(productKey)
                if (productIndex > -1 && product != null) {
                    products.add(productIndex, product)
                    notifyItemChanged(productIndex)
                } else {
                    Log.w(TAG, "onChildChanged:unknown_child: $productKey")
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.key)
                val studentKey = dataSnapshot.key
                val studentIndex = productIds.indexOf(studentKey)
                if (studentIndex > -1) {
                    productIds.removeAt(studentIndex)
                    products.removeAt(studentIndex)
                    notifyItemRemoved(studentIndex)
                } else {
                    Log.w(TAG, "onChildRemoved:unknown_child:$studentKey")
                }
                // [END_EXCLUDE]
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.key)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "postMissings:onCancelled", databaseError.toException())
                Toast.makeText(context, "Failed to load missings.",
                        Toast.LENGTH_SHORT).show()
            }
        }
        query.addChildEventListener(childEventListener)

        // Store reference to listener so it can be removed on app stop
        this.childEventListener = childEventListener
    }
}