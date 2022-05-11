package com.example.listviewlayout.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import com.example.listviewlayout.data.DataBase
import com.example.listviewlayout.databinding.RowLayoutBinding
import com.example.listviewlayout.model.Storage

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


        binding?.productName = mProducts[position].name
        binding?.productPrice = mProducts[position].price
        binding?.productNumber = mProducts[position].number
        binding?.productSum = binding?.productPrice!! * binding.productNumber

        if(mProducts[position].number <= 0){
            binding.downButton.visibility = View.INVISIBLE
        }
        if(mProducts[position].number >= mProducts[position].quantity || mProducts[position].number >= 9 || mProducts[position].quantity <=0){
            binding.upButton.visibility = View.INVISIBLE
        }


        binding?.upButton?.setOnClickListener {
            val newProduct = database.updateStorage()
            mProducts[position].quantity = newProduct[position] + mProducts[position].number

            if (mProducts[position].number < mProducts[position].quantity && mProducts[position].number < 9) {
                mProducts[position].number++
                binding.productNumber = mProducts[position].number
                binding.productSum = binding?.productPrice!! * binding.productNumber
                binding.downButton.visibility = View.VISIBLE

                notifyDataSetChanged()
            }
        }


        binding?.downButton?.setOnClickListener {
            if (mProducts[position].number > 0) {
                mProducts[position].number--
                binding.productNumber = mProducts[position].number
                binding.productSum = binding?.productPrice!! * binding.productNumber
                binding.upButton.visibility = View.VISIBLE

                notifyDataSetChanged()
            }
        }

        return binding.root
    }
}