package com.makspasich.library.ui.archiveProduct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.makspasich.library.databinding.FragmentArchiveProductBinding

class ArchiveProductFragment : Fragment() {
    private lateinit var binding: FragmentArchiveProductBinding
    private val archiveProductViewModel: ArchiveProductViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentArchiveProductBinding.inflate(inflater, container, false)
        val query: Query = FirebaseDatabase.getInstance().reference
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = DataAdapter(query)
        return binding.root
    }
}