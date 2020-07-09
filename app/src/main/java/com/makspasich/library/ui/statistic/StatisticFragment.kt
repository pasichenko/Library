package com.makspasich.library.ui.statistic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.makspasich.library.R
import com.makspasich.library.databinding.FragmentStatisticBinding

class StatisticFragment : Fragment() {
    private lateinit var binding: FragmentStatisticBinding
    private val statisticViewModel: StatisticViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_statistic, container, false)
        val textView = root.findViewById<TextView>(R.id.text_slideshow)
        statisticViewModel.text.observe(viewLifecycleOwner, Observer { s -> textView.text = s })
        return root
    }
}