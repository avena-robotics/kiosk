package com.example.kioskv2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kioskv2.databinding.OrderRowBinding
import com.example.kioskv2.model.Order
import com.example.kioskv2.model.Storage

class ProductAdapter(val context: Context, val products: MutableList<Storage>): RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: OrderRowBinding): RecyclerView.ViewHolder(binding.root){
        init {

        }

        fun bind(item: Storage, position: Int){
            binding.Image.setImageBitmap(products[position].image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val binding = OrderRowBinding.inflate(layoutInflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(products[position], position)

    override fun getItemCount(): Int = products.size
}