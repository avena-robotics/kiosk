package com.example.kiosk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kiosk.data.DataBase
import com.example.kiosk.databinding.RowLayoutBinding
import com.example.kiosk.language.LangStorage
import com.example.kiosk.model.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.properties.Delegates

class ProductAdapter(val context: Context, val products: MutableList<Storage>): RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    val database = DataBase()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val binding = RowLayoutBinding.inflate(layoutInflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(products[position], position)

    override fun getItemCount(): Int = products.size

    inner class ViewHolder(val binding: RowLayoutBinding ): RecyclerView.ViewHolder(binding.root) {
        var pos = -1
        init {
            binding.upButton.setOnClickListener {
                println("click")
                runBlocking {
                    launch(Dispatchers.IO) {
                        val newProduct = database.updateStorage()
                        products[pos].quantity = newProduct[pos] + products[pos].number
                    }
                }

                if (products[pos].number < products[pos].quantity && products[pos].number < 9) {
                    products[pos].number++
                    binding.productNumber = products[pos].number
                    binding.productSum = binding.productPrice * binding.productNumber
                    binding.downButton.visibility = View.VISIBLE

                    println("position: $pos")
                    notifyDataSetChanged()
                }
            }

            binding.upButton.setOnLongClickListener {
                println("long")
                binding.upButton.performClick()
            }

            binding.downButton.setOnClickListener {
                if (products[pos].number > 0) {
                    products[pos].number--
                    binding.productNumber = products[pos].number
                    binding.productSum = binding.productPrice * binding.productNumber
                    binding.upButton.visibility = View.VISIBLE

                    notifyDataSetChanged()
                }
            }

            binding.downButton.setOnLongClickListener {
                binding.downButton.performClick()
            }
        }


        fun bind(item: Storage, position: Int){
            pos = position
            if(LangStorage(context).getPreferredLocale() == "pl"){
                binding.productName = products[position].name_pl
            }else if(LangStorage(context).getPreferredLocale() == "en"){
                binding.productName = products[position].name_en
            }else{
                binding.productName = products[position].name_en
            }

            binding.productPrice = products[position].price
            binding.productNumber = products[position].number
            binding.productSum = binding.productPrice * binding.productNumber
            binding.image.setImageBitmap(products[position].image)

            if(products[position].number <= 0){
                binding.downButton.visibility = View.INVISIBLE
            }

            if(products[position].number >= products[position].quantity || products[position].number >= 9 || products[position].quantity <=0){
                binding.upButton.visibility = View.INVISIBLE
            }else {
                binding.upButton.visibility = View.VISIBLE
            }

            if(products[position].number > 0){
                binding.downButton.visibility = View.VISIBLE
            }else{
                binding.downButton.visibility = View.INVISIBLE
            }
        }

        /*


         */
    }


}