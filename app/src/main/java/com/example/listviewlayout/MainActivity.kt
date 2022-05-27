package com.example.listviewlayout

import android.content.Context
import android.content.Intent
import android.database.DataSetObserver
import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.example.listviewlayout.adapter.ItemAdapter
import com.example.listviewlayout.data.DataBase
import com.example.listviewlayout.databinding.ActivityMainBinding
import com.example.listviewlayout.databinding.CardViewBinding
import com.example.listviewlayout.model.Order
import com.example.listviewlayout.model.Storage
import kotlinx.coroutines.*
import kotlin.random.Random



class MainActivity : BaseActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var products: MutableList<Storage>
    lateinit var adapter: ItemAdapter
    lateinit var context: Context
    lateinit var dataBase: DataBase

    val order = Order(0 , 0, 0, 0, 0,0 ,0 ,0, 0, 0, 0, 0,0 ,0 ,0, 0, 0, 0,0, 0, 0)

    var total: Float = 0.0F
    var currentId: Int = 0

    var noOrderFlag = true
    var timerCheckFlag = false

    val timer = object: CountDownTimer(60000, 1000){ //60000
        override fun onTick(p0: Long) {
            binding.clock = "${p0/1000}"

            var newProducts: MutableList<Int> = mutableListOf()
            runBlocking {
                launch(Dispatchers.IO){
                    newProducts = dataBase.updateStorage()
                }

            }


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
                updateOrder()
                order.status = 5
                GlobalScope.launch(Dispatchers.IO){
                    dataBase.updateOrder(order, currentId)
                }

            }

            val switchActivityIntent = Intent(context, StartActivity::class.java)
            startActivity(switchActivityIntent)
        }
    }

    lateinit var popupWindow: PopupWindow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("Main create 1")

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.productTotal = 0.0F

        window.insetsController?.let {
            it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            window.navigationBarColor = getColor(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)
            it.hide(WindowInsets.Type.systemBars())
        }


        dataBase = DataBase()
        currentId = 0

        println("Main create 2")

        runBlocking {
            launch(Dispatchers.IO){
                products = dataBase.storageImport()
            }
        }

        println("Main create 3")


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
                            updateOrder()
                            order.status = 0
                            dataBase.updateOrder(order, currentId)
                            noOrderFlag = false
                        }else{
                            order.status = 0
                            updateOrder()
                            dataBase.updateOrder(order, currentId)
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
                updateOrder()

                productsNumbers.add(order.product_1)
                productsNumbers.add(order.product_2)
                productsNumbers.add(order.product_3)
                productsNumbers.add(order.product_4)
                productsNumbers.add(order.product_5)
                productsNumbers.add(order.product_6)
                productsNumbers.add(order.product_7)
                productsNumbers.add(order.product_8)
                productsNumbers.add(order.product_9)
                productsNumbers.add(order.drink_1)
                productsNumbers.add(order.drink_2)
                productsNumbers.add(order.drink_3)
                productsNumbers.add(order.drink_4)
                productsNumbers.add(order.drink_5)
                productsNumbers.add(order.drink_6)
                productsNumbers.add(order.sos_1)
                productsNumbers.add(order.sos_2)
                productsNumbers.add(order.box)
                productsNumbers.add(order.bag)

                runBlocking {
                    launch(Dispatchers.IO){
                        dataBase.setClientName(binding.popupButton.text.toString(), currentId)
                    }
                }

                switchToPaymentIntent.putExtra("products", productsNumbers)
                switchToPaymentIntent.putExtra("productsTotal", binding.productTotal)
                switchToPaymentIntent.putExtra("currentId", currentId)
                println(binding.popupButton.text)
                switchToPaymentIntent.putExtra("clientName", binding.popupButton.text.toString())

                startActivity(switchToPaymentIntent)
            }
        }

        binding.cancelButton.setOnClickListener {
            if(!noOrderFlag){
                order.status = 5
                updateOrder()
                GlobalScope.launch(Dispatchers.IO) {
                    dataBase.updateOrder(order, currentId)
                }
            }

            timer.cancel()
            val switchActivityIntent = Intent(context, StartActivity::class.java)
            startActivity(switchActivityIntent)
        }

        binding.popupButton.setOnClickListener {
            timer.cancel()
            timer.start()
            val inflater: LayoutInflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popUpBinding = CardViewBinding.inflate(inflater)

            popupWindow = PopupWindow(popUpBinding.root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

            popupWindow.isFocusable = true
            popupWindow.update()

            popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)

            popUpBinding.confirmButton.setOnClickListener {
                timer.cancel()
                timer.start()
                if(popUpBinding.editText.text.isNotEmpty()){
                    binding.popupButton.text = popUpBinding.editText.text
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
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

                    if(imm.showSoftInput(popUpBinding.editText, InputMethodManager.SHOW_IMPLICIT)){
                        println("break")
                        break
                    }else{
                        println("delay")
                        delay(10)
                    }
                }

            }
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
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
                        binding.popupButton.text = popUpBinding.editText.text
                    }
                    popupWindow.dismiss()

                    return@OnEditorActionListener true
                }
                false
            })

        }


        //binding.popupButton.text = getString(R.string.name_first_10) + " " + getString(R.string.name_seccond_10)
        binding.popupButton.text = generateFirstName()

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

    fun updateOrder(){
        products.forEach{
            when(it.name_en){
                "product_1" -> order.product_1 = it.number
                "product_2" -> order.product_2 = it.number
                "product_3" -> order.product_3 = it.number
                "product_4" -> order.product_4 = it.number
                "product_5" -> order.product_5 = it.number
                "product_6" -> order.product_6 = it.number
                "product_7" -> order.product_7 = it.number
                "product_8" -> order.product_8 = it.number
                "product_9" -> order.product_9 = it.number
                "drink_1" -> order.drink_1 = it.number
                "drink_2" -> order.drink_2 = it.number
                "drink_3" -> order.drink_3 = it.number
                "drink_4" -> order.drink_4 = it.number
                "drink_5" -> order.drink_5 = it.number
                "drink_6" -> order.drink_6 = it.number
                "sauce_1" -> order.sos_1 = it.number
                "sauce_2" -> order.sos_2 = it.number
                "box" -> order.box = it.number
                "bag" -> order.bag = it.number
            }
        }
    }

    fun generateFirstName(): String{
        return getString(resources.getIdentifier("name_first_${Random.nextInt(1,10)}", "string", packageName)) + " " + getString(resources.getIdentifier("name_seccond_${Random.nextInt(1,10)}", "string", packageName))
    }
}