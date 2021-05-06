package com.touchsides.wordreader.ui.wordreader

import android.content.Context
import android.view.ViewGroup

import androidx.recyclerview.widget.ListAdapter
import com.touchsides.wordreader.utils.FileComparator

class CustomAdapter(
    private val context: Context,
    private val fileClickListener: FileClickListener
) : ListAdapter<String, FileViewHolder>(FileComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        return FileViewHolder.from(parent, context)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = getItem(position)
        holder.bind(file)
        holder.setOnFileClickListener(fileClickListener)
    }
}