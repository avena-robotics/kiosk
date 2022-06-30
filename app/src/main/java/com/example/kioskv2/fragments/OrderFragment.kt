package com.example.kioskv2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kioskv2.MainActivity
import com.example.kioskv2.R
import com.example.kioskv2.adapter.ProductAdapter
import com.example.kioskv2.databinding.FragmentOrderBinding
import com.example.kioskv2.language.LangStorage
import com.example.kioskv2.model.Storage
import com.example.kioskv2.viewmodels.SharedViewModel

class OrderFragment : Fragment() {
    lateinit var binding: FragmentOrderBinding
    lateinit var adapter: ProductAdapter
    lateinit var model: SharedViewModel
    lateinit var products: LiveData<MutableList<Storage>>

    var total: Float = 0.0F

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        products = model.products

        //przenieść do model
        val pizzaList: MutableList<Storage> = mutableListOf()
        val drinkList: MutableList<Storage> = mutableListOf()
        val sosList: MutableList<Storage> = mutableListOf()
        val boxList: MutableList<Storage> = mutableListOf()

        for(i in 0 until products.value!!.size){
            when(products.value!![i].type){
                1 -> { pizzaList.add(products.value!![i]) }
                2 -> { drinkList.add(products.value!![i])}
                3 -> { sosList.add(products.value!![i])}
                4 -> { boxList.add(products.value!![i])}
                else -> {}
            }
        }

        val productList:MutableList<Storage> = mutableListOf()

        buttonChange(1)

        for(i in 0 until pizzaList.size){
            productList.add(pizzaList[i])
        }
        println(productList.size)
        adapter = ProductAdapter(this.requireContext(), productList)
        adapter.setHasStableIds(true)
        binding.list.adapter = adapter

        val layoutManager = object:LinearLayoutManager(context){
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        binding.list.layoutManager = layoutManager
        binding.list.addItemDecoration(DividerItemDecoration(this.requireContext(), layoutManager.orientation))

        adapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                sumProducts()
            }
        })

        binding.buttonPizza.setOnClickListener{
            buttonChange(1)
            for(i in 1..productList.size){
                productList.removeLast()
            }

            for(i in 0 until pizzaList.size){
                productList.add(pizzaList[i])
                print(productList.size)
            }
            adapter.notifyDataSetChanged()
        }

        binding.buttonDrinks.setOnClickListener{
            buttonChange(2)
            for(i in 1..productList.size){
                productList.removeLast()
            }

            for(i in 0 until drinkList.size){
                productList.add(drinkList[i])
            }
            adapter.notifyDataSetChanged()
        }

        binding.buttonSoses.setOnClickListener {
            buttonChange(3)
            for(i in 1..productList.size){
                productList.removeLast()
            }

            for(i in 0 until sosList.size){
                productList.add(sosList[i])
            }
            adapter.notifyDataSetChanged()
        }

        binding.buttonBoxes.setOnClickListener{
            buttonChange(4)
            for(i in 1..productList.size){
                productList.removeLast()
            }

            for(i in 0 until boxList.size){
                productList.add(boxList[i])
            }
            adapter.notifyDataSetChanged()
        }

        binding.cancelButton.setOnClickListener {
            binding.root.findNavController().navigate(OrderFragmentDirections.actionOrderFragmentToStartFragment())
        }

        binding.submitButton.setOnClickListener {
            binding.root.findNavController().navigate(OrderFragmentDirections.actionOrderFragmentToPaymentFragment())
        }

        binding.enFlagButton.setOnClickListener {
            if(LangStorage(requireContext()).getPreferredLocale() != "en"){
                (activity as MainActivity).updateAppLocale("en")
            }
        }

        binding.plFlagButton.setOnClickListener {
            if(LangStorage(requireContext()).getPreferredLocale() != "pl"){
                (activity as MainActivity).updateAppLocale("pl")
            }
        }

        binding.LogoImage.setOnClickListener {

        }
    }

    fun buttonChange(buttonNumber: Int){
        when(buttonNumber){
            1 -> {
                binding.buttonPizza.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.munchies_blue))
                binding.buttonDrinks.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
                binding.buttonSoses.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
                binding.buttonBoxes.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))

                binding.buttonPizza.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                binding.buttonDrinks.setTextColor(ContextCompat.getColor(requireContext(), R.color.munchies_blue))
                binding.buttonSoses.setTextColor(ContextCompat.getColor(requireContext(), R.color.munchies_blue))
                binding.buttonBoxes.setTextColor(ContextCompat.getColor(requireContext(), R.color.munchies_blue))
            }
            2 -> {
                binding.buttonPizza.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
                binding.buttonDrinks.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.munchies_blue))
                binding.buttonSoses.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
                binding.buttonBoxes.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))

                binding.buttonPizza.setTextColor(ContextCompat.getColor(requireContext(), R.color.munchies_blue))
                binding.buttonDrinks.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                binding.buttonSoses.setTextColor(ContextCompat.getColor(requireContext(), R.color.munchies_blue))
                binding.buttonBoxes.setTextColor(ContextCompat.getColor(requireContext(), R.color.munchies_blue))
            }
            3 -> {
                binding.buttonPizza.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
                binding.buttonDrinks.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
                binding.buttonSoses.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.munchies_blue))
                binding.buttonBoxes.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))

                binding.buttonPizza.setTextColor(ContextCompat.getColor(requireContext(), R.color.munchies_blue))
                binding.buttonDrinks.setTextColor(ContextCompat.getColor(requireContext(), R.color.munchies_blue))
                binding.buttonSoses.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                binding.buttonBoxes.setTextColor(ContextCompat.getColor(requireContext(), R.color.munchies_blue))
            }
            4 -> {
                binding.buttonPizza.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
                binding.buttonDrinks.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
                binding.buttonSoses.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
                binding.buttonBoxes.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.munchies_blue))

                binding.buttonPizza.setTextColor(ContextCompat.getColor(requireContext(), R.color.munchies_blue))
                binding.buttonDrinks.setTextColor(ContextCompat.getColor(requireContext(), R.color.munchies_blue))
                binding.buttonSoses.setTextColor(ContextCompat.getColor(requireContext(), R.color.munchies_blue))
                binding.buttonBoxes.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            else -> {}
        }
    }

    fun sumProducts(){
        total = 0.0F
        for(i in 0 until products.value?.size!!){
            total += products.value!![i].price * products.value!![i].number
        }
        binding.productTotal = total
    }
}