package com.example.listviewlayout

import android.content.Context
import android.content.Intent
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

class PaymentActivity : BaseActivity() {
    lateinit var binding: ActivityPaymentBinding
    lateinit var context: Context
    lateinit var products: ArrayList<Int>
    lateinit var order: Order

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

        order = Order(0, products[0], products[1], products[2], products[3], products[4],
            products[5], products[6], products[7], products[8], products[9], products[10], products[11],
            products[12], products[13], products[14], products[15], products[16], products[17], products[18], 0)
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
            binding.mainText.text = getString(R.string.payment_started)
            binding.potw.visibility = View.VISIBLE
            pay()
        }

        pay()
    }

    fun pay(){
        GlobalScope.launch(Dispatchers.IO) {
            order.status = 1
            dataBase.updateOrder(order, currentId)

            val response = payment.startTransaction(total)

            if( response == 0){ //Authorised
                println("payment good")
                order.status = 2
                dataBase.updateOrder(order, currentId)

                endString = getString(R.string.payment_started)
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
                order.status = 5
                dataBase.updateOrder(order, currentId)

                when(response){
                    1  -> endString = getString(R.string.payment_not_authorised)
                    2  -> endString = getString(R.string.payment_not_processed)
                    3  -> endString = getString(R.string.payment_unable_to_authorise)
                    4  -> endString = getString(R.string.payment_unable_to_process)
                    5  -> endString = getString(R.string.payment_unable_to_connect)
                    6  -> endString = getString(R.string.payment_void)
                    7  -> endString = getString(R.string.payment_cancelled)
                    8  -> endString = getString(R.string.payment_invalid_password)
                    9  -> endString = getString(R.string.payment_amount_maximum_limit)
                    10 -> endString = getString(R.string.payment_connection_failure)
                    11 -> endString = getString(R.string.payment_timeout_reached)
                    12 -> endString = getString(R.string.payment_invoice_not_found)
                    13 -> endString = getString(R.string.payment_cashback_exceed_max)
                    14 -> endString = getString(R.string.payment_cashback_not_allowed)
                    15 -> endString = getString(R.string.payment_incomplete)
                    else -> {}
                }

                withContext(Dispatchers.Main){
                    launch{
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
    }
}



