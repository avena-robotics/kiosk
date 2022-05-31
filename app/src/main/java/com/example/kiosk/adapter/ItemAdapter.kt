package com.example.kiosk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import com.example.kiosk.data.DataBase
import com.example.kiosk.databinding.RowLayoutBinding
import com.example.kiosk.language.LangStorage
import com.example.kiosk.model.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ItemAdapter(context: Context, products: MutableList<Storage>): BaseAdapter() {
    private val mContext: Context = context
    private val mProducts: MutableList<Storage> = products

    val database = DataBase()

    //How many rows are in list
    override fun getCount(): Int {
        return mProducts.size
    }

    override fun getItem(position: Int): Any {
        return mProducts[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    //rendering each row
    override fun getView(position: Int, covertView: View?, viewGroup: ViewGroup?): View {

        val layoutInflater = LayoutInflater.from(mContext)
        val binding =
            if (covertView != null) DataBindingUtil.getBinding(covertView) else RowLayoutBinding.inflate(
                layoutInflater,
                viewGroup,
                false)

        if(LangStorage(mContext).getPreferredLocale() == "pl"){
            binding?.productName = mProducts[position].name_pl
        }else if(LangStorage(mContext).getPreferredLocale() == "en"){
            binding?.productName = mProducts[position].name_en
        }else{
            binding?.productName = mProducts[position].name_en
        }


        binding?.productPrice = mProducts[position].price
        binding?.productNumber = mProducts[position].number
        binding?.productSum = binding?.productPrice!! * binding.productNumber
        binding.image.setImageBitmap(mProducts[position].image)

        if(mProducts[position].number <= 0){
            binding.downButton.visibility = View.INVISIBLE
        }

        if(mProducts[position].number >= mProducts[position].quantity || mProducts[position].number >= 9 || mProducts[position].quantity <=0){
            binding.upButton.visibility = View.INVISIBLE
        }else {
            binding.upButton.visibility = View.VISIBLE
        }


        binding.upButton.setOnClickListener {
            runBlocking {
                launch(Dispatchers.IO) {
                    val newProduct = database.updateStorage()
                    mProducts[position].quantity = newProduct[position] + mProducts[position].number
                }
            }

            if (mProducts[position].number < mProducts[position].quantity && mProducts[position].number < 9) {
                mProducts[position].number++
                binding.productNumber = mProducts[position].number
                binding.productSum = binding.productPrice * binding.productNumber
                binding.downButton.visibility = View.VISIBLE

                notifyDataSetChanged()
            }
        }



        binding.downButton.setOnClickListener {
            if (mProducts[position].number > 0) {
                mProducts[position].number--
                binding.productNumber = mProducts[position].number
                binding.productSum = binding.productPrice * binding.productNumber
                binding.upButton.visibility = View.VISIBLE

                notifyDataSetChanged()
            }
        }

        return binding.root
    }
}