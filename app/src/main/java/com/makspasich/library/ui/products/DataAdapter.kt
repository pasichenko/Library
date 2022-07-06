package com.makspasich.library.ui.products

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import com.makspasich.library.databinding.ItemProductBinding
import com.makspasich.library.models.Product

open class DataAdapter(private var query: Query) : RecyclerView.Adapter<DataViewHolder>(),
    EventListener<QuerySnapshot> {
    private val products: MutableList<Product?> = ArrayList()
    private val _productsLiveData: MutableLiveData<MutableList<Product?>> =
        MutableLiveData<MutableList<Product?>>()
    val productsLiveData: LiveData<MutableList<Product?>> = _productsLiveData

    private var registration: ListenerRegistration? = null

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

    companion object {
        private val TAG = DataAdapter::class.java.simpleName
    }

    override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) {
            Log.w(TAG, "onEvent:error", error)
            onError(error)
            return
        }

        if (value == null) {
            return
        }

        Log.d(TAG, "onEvent:numChanges:" + value.documentChanges.size)
        for (change in value.documentChanges) {
            when (change.type) {
                DocumentChange.Type.ADDED -> onDocumentAdded(change)
                DocumentChange.Type.MODIFIED -> onDocumentModified(change)
                DocumentChange.Type.REMOVED -> onDocumentRemoved(change)
            }
        }
        _productsLiveData.value = products.toMutableList()
        onDataChanged()
    }

    private fun onDocumentAdded(change: DocumentChange) {
        products.add(change.newIndex, change.document.toObject())
        notifyItemInserted(change.newIndex)
    }

    private fun onDocumentModified(change: DocumentChange) {
        if (change.oldIndex == change.newIndex) {
            // Item changed but remained in same position
            products[change.oldIndex] = change.document.toObject()
            notifyItemChanged(change.oldIndex)
        } else {
            // Item changed and changed position
            products.removeAt(change.oldIndex)
            products.add(change.newIndex, change.document.toObject())
            notifyItemMoved(change.oldIndex, change.newIndex)
        }
    }

    private fun onDocumentRemoved(change: DocumentChange) {
        products.removeAt(change.oldIndex)
        notifyItemRemoved(change.oldIndex)
    }

    open fun onError(e: FirebaseFirestoreException) {
        Log.w(TAG, "onError", e)
    }

    open fun onDataChanged() {}
    fun startListening() {
        if (registration == null) {
            registration = query.addSnapshotListener(this)
        }
    }

    fun stopListening() {
        registration?.remove()
        registration = null
        products.clear()
        notifyDataSetChanged()
    }

    fun setQuery(query: Query) {
        stopListening()
        products.clear()
        notifyDataSetChanged()
        this.query = query
        startListening()
    }

    init {
        _productsLiveData.value = mutableListOf()
    }
}