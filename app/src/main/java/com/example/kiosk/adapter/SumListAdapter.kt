package com.example.kiosk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kiosk.databinding.SumRowLayoutBinding
import com.example.kiosk.language.LangStorage
import com.example.kiosk.model.Storage

class SumListAdapter(context: Context, products: MutableList<Storage>): RecyclerView.Adapter<SumListAdapter.ViewHolder>() {

    private val mContext: Context = context
    private val mProducts: MutableList<Storage> = products

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SumListAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(mContext)
        val binding = SumRowLayoutBinding.inflate( layoutInflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(mProducts[position])

    override fun getItemCount(): Int = mProducts.size

    inner class ViewHolder(val binding: SumRowLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Storage){

            if(LangStorage(mContext).getPreferredLocale() == "pl"){
                binding.nameSuma.text = item.name_pl
            }else if(LangStorage(mContext).getPreferredLocale() == "en"){
                binding.nameSuma.text = item.name_en
            }else {
                binding.nameSuma.text = item.name_en
            }

            binding.number = item.number

            binding.executePendingBindings()
        }
    }
}

