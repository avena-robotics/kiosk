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
import com.example.kioskv2.R
import com.example.kioskv2.databinding.FragmentTransactionBinding
import com.example.kioskv2.terminal.Payment
import com.example.kioskv2.viewmodels.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class TransactionFragment: Fragment() {
    lateinit var binding: FragmentTransactionBinding
    lateinit var model: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_transaction, container, false)
        return binding.root
    }

    val timer = object: CountDownTimer(240000, 1000){ //60000
        override fun onTick(p0: Long) {
            println("Transaction $p0")
        }

        override fun onFinish() {
            model.cancelOrder()
            println("Nav: Payment to start timer")
            try {
                binding.root.findNavController()
                    .navigate(TransactionFragmentDirections.actionTransactionFragmentToStartFragment())
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        binding.productTotal = model.total.value!!
        binding.clientName.text = model.clientName.value!!

        binding.backButton.visibility = View.INVISIBLE
        binding.redoButton.visibility = View.INVISIBLE

        pay()

        binding.backButton.setOnClickListener {
            timer.cancel()
            model.resetProducts()
            binding.root.findNavController().navigate(TransactionFragmentDirections.actionTransactionFragmentToStartFragment())
        }

        binding.redoButton.setOnClickListener {
            timer.cancel()
            binding.redoButton.visibility = View.INVISIBLE
            binding.backButton.visibility = View.INVISIBLE
            binding.supportText.visibility = View.VISIBLE
            binding.mainText.text = getString(R.string.payment_started)

            pay()
        }

    }

    fun pay(){
        GlobalScope.launch {
            timer.cancel()
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

            if( response == 0){ //Authorised 0
                println("payment good")
                model.orderChange(2)

                endString = getString(R.string.payment_accepted)

                withContext(Dispatchers.Main){
                    binding.backButton.visibility = View.VISIBLE
                    binding.supportText.visibility = View.VISIBLE
                    binding.supportText.text = getString(R.string.end_goal)
                    binding.mainText.text = endString
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
                    binding.backButton.visibility = View.VISIBLE
                    binding.supportText.visibility = View.INVISIBLE
                    binding.mainText.text = endString
                    binding.redoButton.visibility = View.VISIBLE
                    //timer.start()
                }

                println(endString)
                timer.start()
            }
        }
    }
}