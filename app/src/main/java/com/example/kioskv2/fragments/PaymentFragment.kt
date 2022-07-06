package com.example.kioskv2.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kioskv2.BuildConfig
import com.example.kioskv2.MainActivity
import com.example.kioskv2.R
import com.example.kioskv2.adapter.SumListAdapter
import com.example.kioskv2.data.DataBase
import com.example.kioskv2.databinding.FragmentPaymentBinding
import com.example.kioskv2.language.LangStorage
import com.example.kioskv2.model.Storage
import com.example.kioskv2.terminal.Payment
import com.example.kioskv2.viewmodels.SharedViewModel
import kotlinx.coroutines.*
import java.lang.Exception

class PaymentFragment : Fragment() {
    lateinit var binding: FragmentPaymentBinding
    lateinit var model: SharedViewModel
    val dataBase = DataBase()

    val timer = object: CountDownTimer(240000, 1000){ //60000
        override fun onTick(p0: Long) {
            //println("Payment $p0")
        }

        override fun onFinish() {
            model.cancelOrder()
            println("Nav: Payment to start timer")
            try {
                binding.root.findNavController()
                    .navigate(PaymentFragmentDirections.actionPaymentFragmentToStartFragment())
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        binding.productTotal = model.total.value!!

        if(model.clientName.value == ""){
            model.clientName.value = generateNewName()
            binding.clientName.text = model.clientName.value
            model.nameGenFlag.value = true
        }else{
            binding.clientName.text = model.clientName.value
        }

        val products = mutableListOf<Storage>()

        for(i in 0 until model.products.value!!.size){
            if(model.products.value!![i].number != 0){
                products.add(model.products.value!![i])
            }
        }

        val layoutManager = LinearLayoutManager(context)
        binding.list.layoutManager = layoutManager
        binding.list.addItemDecoration(DividerItemDecoration(this.requireContext(), layoutManager.orientation))
        binding.list.adapter = SumListAdapter(requireContext(), products)

        binding.paymentButton.setOnClickListener {
            timer.cancel()
            model.setClientName()

            binding.cancelOrderButton.visibility = View.INVISIBLE
            binding.editOrderButton.visibility = View.INVISIBLE
            //pay()

            binding.root.findNavController().navigate(PaymentFragmentDirections.actionPaymentFragmentToTransactionFragment())
        }

        binding.cancelOrderButton.setOnClickListener {
            timer.cancel()
            model.cancelOrder()
            binding.root.findNavController().navigate(PaymentFragmentDirections.actionPaymentFragmentToStartFragment())
        }

        binding.editOrderButton.setOnClickListener {
            timer.cancel()
            binding.root.findNavController().navigate(PaymentFragmentDirections.actionPaymentFragmentToOrderFragment())
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

        binding.enFlagButton.setOnClickListener {
            if(LangStorage(requireContext()).getPreferredLocale() != "en"){
                timer.cancel()
                (activity as MainActivity).updateAppLocale("en")
            }else{
                timer.cancel()
                timer.start()
            }
        }

        timer.start()
    }

    fun generateNewName(): String{
        var last_name = ""
        var number = 0
        runBlocking {
            launch(Dispatchers.IO){
                last_name = dataBase.getLastName()
            }
        }

        for(i in 1..50){
            if(getString(resources.getIdentifier("imie_$i", "string", BuildConfig.APPLICATION_ID)) == last_name){
                if(i == 50){
                    number = 1
                }else{
                    number = i+1
                }
                break
            }else{
                number = 1
            }
        }
        val name = getString(resources.getIdentifier("imie_$number", "string", BuildConfig.APPLICATION_ID))

        GlobalScope.launch(Dispatchers.IO) {
            dataBase.setClientName(name, model.currentId.value!!)
        }
        return name
    }
}