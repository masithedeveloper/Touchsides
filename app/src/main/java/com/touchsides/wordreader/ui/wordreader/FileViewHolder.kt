package com.touchsides.wordreader.ui.wordreader

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.touchsides.wordreader.databinding.ListItemBinding

class FileViewHolder (
    private val binding: ListItemBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    private var clickListener: FileClickListener? = null

    companion object {
        fun from(parent: ViewGroup, context: Context): FileViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListItemBinding.inflate(inflater, parent, false)
            return FileViewHolder(binding, context)
        }
    }

    fun bind(fileName: String) {
        binding.listItemText.text = fileName
        binding.listItem.setOnClickListener {
            clickListener?.invoke(fileName)
        }
    }

    fun setOnFileClickListener(l: FileClickListener) {
        clickListener = l
    }
}