package com.makspasich.library

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

abstract class CustomAdapter<T>(context1: Context, private val textViewResourceId: Int,
                                private val objects: List<T>) : ArrayAdapter<T>(context1, textViewResourceId, objects) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: TextView = convertView as TextView?
                ?: LayoutInflater.from(context).inflate(textViewResourceId, parent, false) as TextView
        view.text = adapter(objects[position])
        return view
    }

    abstract fun adapter(t: T): String?

}