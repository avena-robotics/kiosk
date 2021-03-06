package com.example.kioskv2.adapter

import android.content.Context
import android.content.res.Resources
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.kioskv2.R
import com.example.kioskv2.data.DataBase
import com.example.kioskv2.databinding.OrderRowBinding
import com.example.kioskv2.language.LangStorage
import com.example.kioskv2.model.Order
import com.example.kioskv2.model.Storage
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ProductAdapter(val context: Context, val products: MutableList<Storage>): RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    val database = DataBase()

    inner class ViewHolder(val binding: OrderRowBinding): RecyclerView.ViewHolder(binding.root){
        var pos = -1
        init {
            binding.upButton.setOnClickListener {
                runBlocking {
                    launch(Dispatchers.IO){
                        val newProduct = database.updateStorage(products[0].type)
                        products[pos].quantity = newProduct[pos] + products[pos].number
                    }

                    if (products[pos].number < products[pos].quantity && products[pos].number < 9) {
                        products[pos].number++
                        binding.productNumber = products[pos].number
                        binding.productSum = binding.productPrice * binding.productNumber
                        binding.downButton.visibility = View.VISIBLE

                        notifyDataSetChanged()
                    }
                }
            }

            binding.upButton.setOnLongClickListener {
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

            when(LangStorage(context).getPreferredLocale()) {
                "pl" -> {
                    binding.name.text = products[position].name_pl
                    binding.sideText.text = products[position].ingredients_pl
                }
                "en" -> {
                    binding.name.text = products[position].name_en
                    binding.sideText.text = products[position].ingredients_en
                }
                else -> {
                    binding.name.text = products[position].name_en
                    binding.sideText.text = products[position].ingredients_en
                }
            }

            binding.Image.setImageBitmap(products[position].image)

            when (products[position].type) {
                1 -> {
                    binding.Image.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    binding.Image.setContentPadding(0, 0, 0, 0)
                }
                2 -> {
                    binding.Image.scaleType = ImageView.ScaleType.FIT_CENTER
                    binding.Image.setContentPadding(0,5,0,5)
                }
                4 -> {
                    binding.Image.scaleType = ImageView.ScaleType.FIT_CENTER
                    binding.Image.setContentPadding(0,5,0,5)
                }
                else -> {
                    binding.Image.scaleType = ImageView.ScaleType.FIT_CENTER
                    binding.Image.setContentPadding(0, 0, 0, 0)
                }
            }

            binding.productNumber = products[position].number
            binding.productPrice = products[position].price
            binding.productSum = binding.productNumber * binding.productPrice

            if(binding.productSum > 0){
                binding.sum.visibility = View.VISIBLE
            }else{
                binding.sum.visibility = View.INVISIBLE
            }

            //println("number ${products[position].number}, quantity ${products[position].quantity}, position $position, type ${products[position].type}")
            if(products[position].number <= 0){
                binding.downButton.visibility = View.INVISIBLE
            }

            if(products[position].number >= products[position].quantity || products[position].number >= 9 || products[position].quantity <=0){
                binding.upButton.visibility = View.INVISIBLE
            }else{
                binding.upButton.visibility = View.VISIBLE
            }

            if(products[position].number > 0){
                binding.downButton.visibility = View.VISIBLE
            }else{
                binding.downButton.visibility = View.INVISIBLE
            }

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