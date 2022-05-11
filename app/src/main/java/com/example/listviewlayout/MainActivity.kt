package com.example.listviewlayout

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.database.DataSetObserver
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.StrictMode
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil

import com.example.listviewlayout.adapter.ItemAdapter
import com.example.listviewlayout.data.DataBase
import com.example.listviewlayout.databinding.ActivityMainBinding
import com.example.listviewlayout.model.Order
import com.example.listviewlayout.model.Storage
import com.example.listviewlayout.terminal.Payment
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var products: MutableList<Storage>
    lateinit var adapter: ItemAdapter
    lateinit var context: Context
    lateinit var dataBase: DataBase

    var total: Float = 0.0F
    var currentId: Int = 0

    var emergencyFlag = false
    var noOrderFlag = true
    var timerCheckFlag = false

    val timer = object: CountDownTimer(60000, 1000){ //60000
        override fun onTick(p0: Long) {
            binding.clock = "${p0/1000}"


            val new_products = dataBase.updateStorage()

            for(i in 0 until new_products.size){
                if(products[i].quantity != new_products[i]){
                    products[i].quantity = new_products[i] + products[i].number
                }
            }

            timerCheckFlag = true
            adapter.notifyDataSetChanged()
            timerCheckFlag = false
        }

        override fun onFinish() {
            if(!noOrderFlag){
                dataBase.updateOrder(Order(0, products[8].number, products[9].number, products[10].number, products[11].number, products[12].number, products[13].number,
                    products[14].number, products[15].number, products[16].number, products[2].number, products[3].number, products[4].number, products[5].number, products[6].number,
                    products[7].number, products[17].number, products[18].number, products[1].number, products[0].number, 5), currentId)
            }

            println("time switch")
            val switchActivityIntent = Intent(context, StartActivity::class.java)
            startActivity(switchActivityIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.productTotal = 0.0F
        binding.payFeedback = "Start"

        window.insetsController?.let {
            it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            window.navigationBarColor = getColor(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)
            it.hide(WindowInsets.Type.systemBars())
        }

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)


        dataBase = DataBase()
        currentId = 0

        runBlocking {
            launch(Dispatchers.IO){
                products = dataBase.storageImport()
            }
        }

        noOrderFlag = true

        adapter = ItemAdapter(this, products)
        binding.list.adapter = adapter
        binding.list.adapter.registerDataSetObserver(object: DataSetObserver() {
            override fun onChanged() {
                super.onChanged()
                sumProducts()

                if(!emergencyFlag && !timerCheckFlag){
                    GlobalScope.launch(Dispatchers.IO){
                        if(noOrderFlag){
                            println("Timer falg $timerCheckFlag")
                            currentId = dataBase.firstOrder()
                            dataBase.updateOrder(Order(0, products[8].number, products[9].number, products[10].number, products[11].number, products[12].number, products[13].number,
                                products[14].number, products[15].number, products[16].number, products[2].number, products[3].number, products[4].number, products[5].number, products[6].number,
                                products[7].number, products[17].number, products[18].number, products[1].number, products[0].number, 0), currentId)
                            noOrderFlag = false
                        }else{
                            dataBase.updateOrder(Order(0, products[8].number, products[9].number, products[10].number, products[11].number, products[12].number, products[13].number,
                                products[14].number, products[15].number, products[16].number, products[2].number, products[3].number, products[4].number, products[5].number, products[6].number,
                                products[7].number, products[17].number, products[18].number, products[1].number, products[0].number, 0), currentId)
                        }
                    }
                }else{
                    emergencyFlag = false
                }

                if(!timerCheckFlag){
                    timer.cancel()
                    timer.start()
                }
            }
        })

        val payment = Payment()
        binding.payButton.setOnClickListener{
            if(!noOrderFlag && binding.productTotal > 0){

                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                window.insetsController?.let {
                    it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    window.navigationBarColor = getColor(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)
                    it.hide(WindowInsets.Type.systemBars())
                }


                GlobalScope.launch(Dispatchers.IO) {
                    timer.cancel()

                    binding.payFeedback = "Payment Start"

                    println("payment start")
                    dataBase.updateOrder(Order(0, products[8].number, products[9].number, products[10].number, products[11].number, products[12].number, products[13].number,
                        products[14].number, products[15].number, products[16].number, products[2].number, products[3].number, products[4].number, products[5].number, products[6].number,
                        products[7].number, products[17].number, products[18].number, products[1].number, products[0].number, 1), currentId)

                    val response = payment.doTransaction(binding.productTotal)

                    if( response == 7){ //Cancelled
                        println("payment Cancelled")
                        dataBase.updateOrder(Order(0, products[8].number, products[9].number, products[10].number, products[11].number, products[12].number, products[13].number,
                            products[14].number, products[15].number, products[16].number, products[2].number, products[3].number, products[4].number, products[5].number, products[6].number,
                            products[7].number, products[17].number, products[18].number, products[1].number, products[0].number, 5), currentId)

                        binding.payFeedback = "Payment Cancelled"
                        noOrderFlag = true

                        withContext(Dispatchers.Main){
                            launch{
                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                window.insetsController?.let {
                                    it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                                    window.navigationBarColor = getColor(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)
                                    it.hide(WindowInsets.Type.systemBars())
                                }
                            }
                        }
                    }else if( response == 0){ //Authorised
                        println("payment good")
                        dataBase.updateOrder(Order(0, products[8].number, products[9].number, products[10].number, products[11].number, products[12].number, products[13].number,
                            products[14].number, products[15].number, products[16].number, products[2].number, products[3].number, products[4].number, products[5].number, products[6].number,
                            products[7].number, products[17].number, products[18].number, products[1].number, products[0].number, 2), currentId)

                        binding.payFeedback = "Payment Processed"
                        noOrderFlag = true

                        withContext(Dispatchers.Main){
                            launch{
                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                window.insetsController?.let {
                                    it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                                    window.navigationBarColor = getColor(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)
                                    it.hide(WindowInsets.Type.systemBars())
                                }
                            }
                        }

                    }

                    withContext(Dispatchers.Main){
                        launch{
                            emergencyFlag = true
                            resetProducts()
                            //emergencyFlag = false
                        }
                    }
                }
            }

        }

        binding.cancelButton.setOnClickListener {
            //resetProducts()
            if(!noOrderFlag){
                println("canele button")
                dataBase.updateOrder(Order(0, products[8].number, products[9].number, products[10].number, products[11].number, products[12].number, products[13].number,
                    products[14].number, products[15].number, products[16].number, products[2].number, products[3].number, products[4].number, products[5].number, products[6].number,
                    products[7].number, products[17].number, products[18].number, products[1].number, products[0].number, 5), currentId)
            }

            println("cancel switch")
            timer.cancel()
            val switchActivityIntent = Intent(context, StartActivity::class.java)
            startActivity(switchActivityIntent)
        }

        context = this
        timer.start()
    }

    fun sumProducts(){
        total = 0.0F
        for(i in 0 until products.size){
            total += products[i].price * products[i].number
        }
        binding.productTotal = total
    }

    fun resetProducts(){
        for(i in 0 until products.size){
            products[i].number = 0
        }
        println("ha ha ")
        adapter.notifyDataSetChanged()
    }
}