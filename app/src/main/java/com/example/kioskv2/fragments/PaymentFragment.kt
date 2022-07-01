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

class PaymentFragment : Fragment() {
    lateinit var binding: FragmentPaymentBinding
    lateinit var model: SharedViewModel
    val dataBase = DataBase()
    var timerFlag = 0

    val timer = object: CountDownTimer(60000, 1000){ //60000
        override fun onTick(p0: Long) {
        }

        override fun onFinish() {
            model.cancelOrder()
            timerFlag = 1

            timerFlag = 0
            println("Nav: Payment to start timer")
            binding.root.findNavController().navigate(PaymentFragmentDirections.actionPaymentFragmentToStartFragment())
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
            model.setClientName()

            binding.cancelOrderButton.visibility = View.INVISIBLE
            binding.editOrderButton.visibility = View.INVISIBLE
            pay()
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
                (activity as MainActivity).updateAppLocale("pl")
            }
        }

        binding.enFlagButton.setOnClickListener {
            if(LangStorage(requireContext()).getPreferredLocale() != "en"){
                (activity as MainActivity).updateAppLocale("en")
            }
        }
    }

    fun generateNewName(): String{
        var last_name = ""
        var number = 0
        runBlocking {
            launch(Dispatchers.IO){
                last_name = dataBase.getLastName()
                println(last_name)
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

    fun pay(){
        GlobalScope.launch {
            var endString = ""
            val payment = Payment()
            var response: Int = -1

            model.orderChange(1)
            //timer.cancel()

            withContext(Dispatchers.IO){
                launch{
                    response = payment.startTransaction(model.total.value!!)
                }
            }

            if( response == 0){ //Authorised
                println("payment good")
                model.orderChange(2)

                endString = getString(R.string.payment_accepted)

                withContext(Dispatchers.Main){
                    //binding.backButton.visibility = View.VISIBLE
                    //binding.supportText.visibility = View.VISIBLE
                    //binding.supportText.text = getString(R.string.end_goal)
                    //binding.mainText.text = endString
                    //timer.start()
                }

            } else { //Cancelled
                println("payment Cancelled")
                model.orderChange(5)

                when (response) {
                    1 -> endString = getString(R.string.payment_not_authorised)
                    2 -> endString = getString(R.string.payment_not_processed)
                    3 -> endString = getString(R.string.payment_unable_to_authorise)
                    4 -> endString = getString(R.string.payment_unable_to_process)
                    5 -> endString = getString(R.string.payment_unable_to_connect)
                    6 -> endString = getString(R.string.payment_void)
                    7 -> endString = getString(R.string.payment_cancelled)
                    8 -> endString = getString(R.string.payment_invalid_password)
                    9 -> endString = getString(R.string.payment_amount_maximum_limit)
                    10 -> endString = getString(R.string.payment_connection_failure)
                    11 -> endString = getString(R.string.payment_timeout_reached)
                    12 -> endString = getString(R.string.payment_invoice_not_found)
                    13 -> endString = getString(R.string.payment_cashback_exceed_max)
                    14 -> endString = getString(R.string.payment_cashback_not_allowed)
                    15 -> endString = getString(R.string.payment_incomplete)
                    else -> {}
                }

                withContext(Dispatchers.Main){
                    //binding.backButton.visibility = View.VISIBLE
                    //binding.supportText.visibility = View.INVISIBLE
                    //binding.mainText.text = endString
                    //binding.redoButton.visibility = View.VISIBLE
                    //binding.potw.visibility = View.INVISIBLE
                    //timer.start()
                }

                println(endString)
            }
        }
    }
}