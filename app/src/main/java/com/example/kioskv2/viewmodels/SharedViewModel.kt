package com.example.kioskv2.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kioskv2.data.DataBase
import com.example.kioskv2.model.Order
import com.example.kioskv2.model.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class SharedViewModel: ViewModel() {
    val products: MutableLiveData<MutableList<Storage>> = MutableLiveData()
    val clientName: MutableLiveData<String> = MutableLiveData()
    val currentId: MutableLiveData<Int> = MutableLiveData(0)
    val order: MutableLiveData<Order> = MutableLiveData(Order(0, 0, 0,
        0, 0, 0, 0 ,0 ,0 ,0 ,
        0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,
        0 ,0))
    val total: MutableLiveData<Float> = MutableLiveData()
    val nameGenFlag: MutableLiveData<Boolean> = MutableLiveData()
    val changeFlag: MutableLiveData<Boolean> = MutableLiveData(false)

    var flag = 0
    val database = DataBase()

    fun productsInit(){
        if(flag == 0){
            viewModelScope.launch(Dispatchers.IO){
                val temp = database.storageImport()
                flag =1
                withContext(Dispatchers.Main){
                    products.value = temp
                }
            }
            clientName.value = ""
        }
    }

    fun cancelOrder(){
        runBlocking {
            launch(Dispatchers.IO) {
                updateOrder()
                order.value?.status = 5
                println("in current id ${currentId.value}")
                database.updateOrder(order.value!! ,currentId.value!!)
            }
        }
        resetProducts()
    }

    fun orderChange(status: Int){
        viewModelScope.launch(Dispatchers.IO) {
            updateOrder()
            order.value?.status = status
            database.updateOrder(order.value!! ,currentId.value!!)
        }
    }

    fun firstOrder(){
        viewModelScope.launch(Dispatchers.IO) {
            val temp = database.firstOrder()
            withContext(Dispatchers.Main){
                currentId.value = temp
            }
            orderChange(0)
        }
    }

    fun updateOrder(){
        products.value?.forEach{
            when(it.name_en){
                "product_1" -> order.value?.product_1 = it.number;
                "product_2" -> order.value?.product_2 = it.number
                "product_3" -> order.value?.product_3 = it.number
                "product_4" -> order.value?.product_4 = it.number
                "product_5" -> order.value?.product_5 = it.number
                "product_6" -> order.value?.product_6 = it.number
                "product_7" -> order.value?.product_7 = it.number
                "product_8" -> order.value?.product_8 = it.number
                "product_9" -> order.value?.product_9 = it.number
                "drink_1" -> order.value?.drink_1 = it.number
                "drink_2" -> order.value?.drink_2 = it.number
                "drink_3" -> order.value?.drink_3 = it.number
                "drink_4" -> order.value?.drink_4 = it.number
                "drink_5" -> order.value?.drink_5 = it.number
                "drink_6" -> order.value?.drink_6 = it.number
                "sauce_1" -> order.value?.sos_1 = it.number
                "sauce_2" -> order.value?.sos_2 = it.number
                "box" -> order.value?.box = it.number
                "bag" -> order.value?.bag = it.number
            }
        }
    }

    fun resetProducts(){
        println("pre res current id ${currentId.value}")
        products.value?.forEach{
            it.number = 0
        }
        currentId.value = 0
        clientName.value = ""
        println("post res current id ${currentId.value}")
    }

    fun updateStorage(){
        viewModelScope.launch(Dispatchers.IO){
            val temp = database.updateStorage()
            var flagStorChange = false
            withContext(Dispatchers.Main){
                for(i in 0 until temp.size){
                    if(products.value!![i].quantity != temp[i]){
                        products.value!![i].quantity  = temp[i] + products.value!![i].number
                        flagStorChange = true
                    }
                }
                changeFlag.value = flagStorChange
            }
        }
    }

    fun setClientName(){
        viewModelScope.launch(Dispatchers.IO) {
            database.setClientName(clientName.value!!, currentId.value!!)
        }

    }
}