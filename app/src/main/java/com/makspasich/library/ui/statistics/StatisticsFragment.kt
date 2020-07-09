package com.makspasich.library.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.Query
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.makspasich.library.databinding.FragmentStatisticsBinding

class StatisticsFragment : Fragment() {
    private lateinit var binding: FragmentStatisticsBinding
    private val statisticsViewModel: StatisticsViewModel by viewModels()
    private val query: Query = Firebase.database.reference.child("product-statistic")
    private lateinit var adapter: DataAdapter
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adapter = DataAdapter(query)
        binding.recyclerView.adapter = adapter
    }

    override fun onStop() {
        super.onStop()
        adapter.cleanupListener()
    }
}