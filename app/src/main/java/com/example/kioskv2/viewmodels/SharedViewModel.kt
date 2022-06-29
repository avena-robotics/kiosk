package com.example.kioskv2.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kioskv2.data.DataBase
import com.example.kioskv2.model.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SharedViewModel: ViewModel() {
    val products: MutableLiveData<MutableList<Storage>> = MutableLiveData()
    val clientName: MutableLiveData<String> = MutableLiveData()

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
}