package com.example.listviewlayout

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.databinding.DataBindingUtil
import com.example.listviewlayout.data.DataBase
import com.example.listviewlayout.databinding.ActivityPaymentBinding
import com.example.listviewlayout.model.Order
import com.example.listviewlayout.terminal.Payment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

class PaymentActivity : AppCompatActivity() {
    lateinit var binding: ActivityPaymentBinding
    lateinit var context: Context
    lateinit var products: ArrayList<Int>

    val payment = Payment()
    val dataBase = DataBase()

    var endString = ""
    var total = 0.0F
    var currentId = 0

    val timer = object: CountDownTimer(60000, 1000){ //60000
        override fun onTick(p0: Long) {
        }

        override fun onFinish() {
            val switchActivityIntent = Intent(context, StartActivity::class.java)
            startActivity(switchActivityIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment)
        context = this

        window.insetsController?.let {
            it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            window.navigationBarColor = getColor(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)
            it.hide(WindowInsets.Type.systemBars())
        }

        products = intent.getIntegerArrayListExtra("products")!!
        currentId = intent.getIntExtra("currentId", 0)
        total = intent.getFloatExtra("productsTotal", 0.0F)

        binding.productTotal = total

        binding.backButton.setOnClickListener {
            timer.cancel()
            val switchActivityIntent = Intent(this, StartActivity::class.java)
            startActivity(switchActivityIntent)
        }

        binding.redoButton.setOnClickListener {
            timer.cancel()
            binding.redoButton.visibility = View.INVISIBLE
            binding.backButton.visibility = View.INVISIBLE
            binding.supportText.visibility = View.VISIBLE
            binding.mainText.text = "Payment Started"
            pay()
        }

        pay()
    }

    fun pay(){
        GlobalScope.launch(Dispatchers.IO) {
            dataBase.updateOrder(Order(0, products[8], products[9], products[10], products[11], products[12], products[13],
                products[14], products[15], products[16], products[2], products[3], products[4], products[5], products[6],
                products[7], products[17], products[18], products[1], products[0], 1), currentId)

            val response = payment.startTransaction(total)

            if( response == 0){ //Authorised
                println("payment good")
                dataBase.updateOrder(Order(0, products[8], products[9], products[10], products[11], products[12], products[13],
                    products[14], products[15], products[16], products[2], products[3], products[4], products[5], products[6],
                    products[7], products[17], products[18], products[1], products[0], 2), currentId)

                endString = "Payment Processed"

                withContext(Dispatchers.Main){
                    launch{
                        binding.backButton.visibility = View.VISIBLE
                        binding.supportText.visibility = View.INVISIBLE
                        binding.mainText.text = endString
                        timer.start()
                    }
                }
            } else { //Cancelled
                println("payment Cancelled")
                dataBase.updateOrder(Order(0, products[8], products[9], products[10], products[11], products[12], products[13],
                    products[14], products[15], products[16], products[2], products[3], products[4], products[5], products[6],
                    products[7], products[17], products[18], products[1], products[0], 5), currentId)

                when(response){
                    1  -> endString = "Payment Not Authorised"
                    2  -> endString = "Payment Not Processed"
                    3  -> endString = "Payment Unable to Authorise"
                    4  -> endString = "Payment Unable to Process"
                    5  -> endString = "Payment To Connect"
                    6  -> endString = "Void"
                    7  -> endString = "Payment Cancelled"
                    8  -> endString = "Invalid Password"
                    9  -> endString = "Amount Exceed Maximum Limit"
                    10 -> endString = "Connection Failure"
                    11 -> endString = "Timeout Reached"
                    12 -> endString = "Invoice Not Found"
                    13 -> endString = "CashBack Exceed Maximum Limit"
                    14 -> endString = "CashBack Not Allowed"
                    15 -> endString = "Incomplete"
                    else -> {}
                }

                withContext(Dispatchers.Main){
                    launch{
                        binding.backButton.visibility = View.VISIBLE
                        binding.supportText.visibility = View.INVISIBLE
                        binding.mainText.text = endString
                        binding.redoButton.visibility = View.VISIBLE
                        timer.start()
                    }
                }
            }
        }
    }
}



