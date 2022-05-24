package com.example.listviewlayout

import android.content.Context
import android.content.Intent
import android.database.DataSetObserver
import android.os.Bundle
import android.os.CountDownTimer
import android.os.StrictMode
import android.view.*
import androidx.databinding.DataBindingUtil

import com.example.listviewlayout.adapter.ItemAdapter
import com.example.listviewlayout.data.DataBase
import com.example.listviewlayout.databinding.ActivityMainBinding
import com.example.listviewlayout.model.Order
import com.example.listviewlayout.model.Storage
import kotlinx.coroutines.*


class MainActivity : BaseActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var products: MutableList<Storage>
    lateinit var adapter: ItemAdapter
    lateinit var context: Context
    lateinit var dataBase: DataBase

    var total: Float = 0.0F
    var currentId: Int = 0

    var noOrderFlag = true
    var timerCheckFlag = false

    val timer = object: CountDownTimer(60000, 1000){ //60000
        override fun onTick(p0: Long) {
            binding.clock = "${p0/1000}"

            val newProducts = dataBase.updateStorage()

            for(i in 0 until newProducts.size){
                if(products[i].quantity != newProducts[i]){
                    products[i].quantity = newProducts[i] + products[i].number
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

            val switchActivityIntent = Intent(context, StartActivity::class.java)
            startActivity(switchActivityIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.productTotal = 0.0F

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

                if(!timerCheckFlag){
                    GlobalScope.launch(Dispatchers.IO){
                        if(noOrderFlag){
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
                }

                if(!timerCheckFlag){
                    timer.cancel()
                    timer.start()
                }
            }
        })

        binding.payButton.setOnClickListener{
            if(!noOrderFlag && binding.productTotal > 0){
                timer.cancel()
                val switchToPaymentIntent = Intent(this, PaymentActivity::class.java)
                var productsNumbers = ArrayList<Int>(products.size)
                products.forEach{
                    productsNumbers.add(it.number)
                }

                switchToPaymentIntent.putExtra("products", productsNumbers)
                switchToPaymentIntent.putExtra("productsTotal", binding.productTotal)
                switchToPaymentIntent.putExtra("currentId", currentId)

                startActivity(switchToPaymentIntent)
            }
        }

        binding.cancelButton.setOnClickListener {
            if(!noOrderFlag){
                dataBase.updateOrder(Order(0, products[8].number, products[9].number, products[10].number, products[11].number, products[12].number, products[13].number,
                    products[14].number, products[15].number, products[16].number, products[2].number, products[3].number, products[4].number, products[5].number, products[6].number,
                    products[7].number, products[17].number, products[18].number, products[1].number, products[0].number, 5), currentId)
            }

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
}