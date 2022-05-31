package com.example.kiosk

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.kiosk.data.DataBase
import com.example.kiosk.databinding.CardViewBinding
import com.example.kiosk.databinding.FragmentPaymentBinding
import com.example.kiosk.terminal.Payment
import kotlinx.coroutines.*
import kotlin.random.Random

class PaymentFragment : Fragment() {
    lateinit var binding: FragmentPaymentBinding
    lateinit var popupWindow: PopupWindow
    lateinit var model: SharedViewModel

    val dataBase = DataBase()


    val timer = object: CountDownTimer(60000, 1000){ //60000
        override fun onTick(p0: Long) {
        }

        override fun onFinish() {
            model.resetProducts()
            binding.root.findNavController().navigate(PaymentFragmentDirections.actionPaymentFragmentToStartFragment())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        binding.clientName = generateFirstName()

        binding.backButton.setOnClickListener {
            timer.cancel()
            model.resetProducts()
            binding.root.findNavController().navigate(PaymentFragmentDirections.actionPaymentFragmentToStartFragment())
        }

        binding.redoButton.setOnClickListener {
            timer.cancel()
            binding.redoButton.visibility = View.INVISIBLE
            binding.backButton.visibility = View.INVISIBLE
            binding.supportText.visibility = View.VISIBLE
            binding.mainText.text = getString(R.string.payment_started)
            binding.potw.visibility = View.VISIBLE
            pay()
        }

        binding.changeName.setOnClickListener {
            val inflater: LayoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popUpBinding = CardViewBinding.inflate(inflater)

            popupWindow = PopupWindow(popUpBinding.root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

            popupWindow.isFocusable = true
            popupWindow.update()

            popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)

            popUpBinding.confirmButton.setOnClickListener {
                timer.cancel()
                timer.start()
                if(popUpBinding.editText.text.isNotEmpty()){
                    binding.clientName = popUpBinding.editText.text.toString()
                }
                popupWindow.dismiss()
            }

            popUpBinding.declineButton.setOnClickListener {
                timer.cancel()
                timer.start()
                popupWindow.dismiss()
            }

            popUpBinding.editText.requestFocus()

            GlobalScope.launch {
                while(true){
                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                    if(imm.showSoftInput(popUpBinding.editText, InputMethodManager.SHOW_IMPLICIT)){
                        println("break")
                        break
                    }else{
                        println("delay")
                        delay(10)
                    }
                }

            }
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(popUpBinding.editText, InputMethodManager.SHOW_IMPLICIT)


            popUpBinding.editText.addTextChangedListener {
                timer.cancel()
                timer.start()
            }

            popUpBinding.editText.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
                if(id == EditorInfo.IME_ACTION_DONE) {
                    timer.cancel()
                    timer.start()
                    if(popUpBinding.editText.text.isNotEmpty()){
                        binding.clientName = popUpBinding.editText.text.toString()
                    }
                    popupWindow.dismiss()

                    return@OnEditorActionListener true
                }
                false
            })
        }

        binding.startButton.setOnClickListener {
            runBlocking {
                launch(Dispatchers.IO){
                    dataBase.setClientName(binding.clientName.toString(), model.currentId.value!!)
                }
            }

            binding.startButton.visibility = View.INVISIBLE
            binding.changeName.visibility = View.INVISIBLE
            binding.mainText.visibility = View.VISIBLE
            binding.supportText.visibility = View.VISIBLE

            pay()
        }

    }

    fun pay(){
        GlobalScope.launch {
            var endString = ""
            val payment = Payment()
            var response: Int = -1

            model.orderChange(1)

            withContext(Dispatchers.IO){
                launch{
                    response = payment.startTransaction(model.total.value!!)
                }
            }

            if( response == 0){ //Authorised
                println("payment good")
                model.orderChange(2)

            endString = getString(R.string.payment_started)
            binding.backButton.visibility = View.VISIBLE
            binding.supportText.visibility = View.INVISIBLE
            binding.mainText.text = endString
            timer.start()

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
                    binding.potw.visibility = View.INVISIBLE
                    timer.start()
                }

            }
        }
    }


    fun generateFirstName(): String{
        return getString(resources.getIdentifier("name_first_${Random.nextInt(1,10)}", "string", BuildConfig.APPLICATION_ID )) + " " + getString(resources.getIdentifier("name_seccond_${Random.nextInt(1,10)}", "string", BuildConfig.APPLICATION_ID ))
    }
}