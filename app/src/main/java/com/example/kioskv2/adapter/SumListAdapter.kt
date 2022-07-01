package com.example.kioskv2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kioskv2.databinding.SumRowBinding
import com.example.kioskv2.language.LangStorage
import com.example.kioskv2.model.Storage

class SumListAdapter(val context: Context, val products: MutableList<Storage>): RecyclerView.Adapter<SumListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: SumRowBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: Storage){
            when(LangStorage(context).getPreferredLocale()) {
                "pl" -> {
                    binding.mainText.text = item.name_pl
                }
                "en" -> {
                    binding.mainText.text = item.name_en
                }
                else -> {
                    binding.mainText.text = item.name_en
                }
            }

            binding.number = item.number
            binding.price = item.price
            binding.sum = item.number*item.price
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SumListAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val binding = SumRowBinding.inflate(layoutInflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(products[position])

    override fun getItemCount(): Int = products.size
}