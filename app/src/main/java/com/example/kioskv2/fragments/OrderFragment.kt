package com.example.kioskv2.fragments

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.CountDownTimer
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
import kotlinx.coroutines.runBlocking
import java.lang.Exception

class OrderFragment : Fragment() {
    lateinit var binding: FragmentOrderBinding
    lateinit var adapter: ProductAdapter
    lateinit var model: SharedViewModel
    lateinit var products: LiveData<MutableList<Storage>>
    lateinit var robotAnimation: AnimationDrawable

    var total: Float = 0.0F

    var timerCheckFlag = false
    var noOrderFlag = true

    val timer = object: CountDownTimer( 60000, 1000){ //TO FIX !!!!!!!!!!!!
        override fun onFinish() {
            if(!noOrderFlag){
                model.cancelOrder()
            }else{
                model.resetProducts()
            }

            try{
                binding.root.findNavController().navigate(OrderFragmentDirections.actionOrderFragmentToStartFragment())
            }catch (e: Exception){
                e.printStackTrace()
            }

        }

        override fun onTick(p0: Long) {
            println("Order: $p0")
            var flag: Boolean
            runBlocking {
                flag = model.updateStorage()
            }

            if(flag){
                timerCheckFlag = true
                adapter.notifyDataSetChanged()
                timerCheckFlag = false
            }
        }
    }

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

        noOrderFlag = model.currentId.value == 0
        sumProducts()

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

                if(noOrderFlag){
                    model.firstOrder()
                    noOrderFlag = false
                }else{
                    model.orderChange(0)
                }

                timer.cancel()
                timer.start()
            }
        })

        binding.robotImage.apply {
            setBackgroundResource(R.drawable.robot2_animation)
            robotAnimation = background as AnimationDrawable
        }

        binding.buttonPizza.setOnClickListener{
            buttonChange(1)
            for(i in 1..productList.size){
                productList.removeLast()
            }

            for(i in 0 until pizzaList.size){
                productList.add(pizzaList[i])
            }
            adapter.notifyDataSetChanged()

            timer.cancel()
            timer.start()
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

            timer.cancel()
            timer.start()
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

            timer.cancel()
            timer.start()
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

            timer.cancel()
            timer.start()
        }

        binding.cancelButton.setOnClickListener {
            if(!noOrderFlag){
                model.cancelOrder()
            }else{
                model.resetProducts()
            }

            timer.cancel()
            binding.root.findNavController().navigate(OrderFragmentDirections.actionOrderFragmentToStartFragment())
        }

        binding.submitButton.setOnClickListener {
            if(!noOrderFlag && binding.productTotal >0){
                timer.cancel()
                model.total.value = binding.productTotal
                binding.root.findNavController().navigate(OrderFragmentDirections.actionOrderFragmentToPaymentFragment())
            }
        }

        binding.enFlagButton.setOnClickListener {
            if(LangStorage(requireContext()).getPreferredLocale() != "en"){
                timer.cancel()
                (activity as MainActivity).updateAppLocale("en")
            }else{
                timer.cancel()
                timer.start()
            }
        }

        binding.plFlagButton.setOnClickListener {
            if(LangStorage(requireContext()).getPreferredLocale() != "pl"){
                timer.cancel()
                (activity as MainActivity).updateAppLocale("pl")
            }else{
                timer.cancel()
                timer.start()
            }
        }

        binding.LogoImage.setOnClickListener {
            timer.cancel()
            view.findNavController().navigate(OrderFragmentDirections.actionOrderFragmentToAdminFragment())
        }

        timer.start()
        robotAnimation.start()
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