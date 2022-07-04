package com.example.kioskv2.model

import android.graphics.Bitmap

data class Storage(
    val name_en: String,
    val name_pl: String,
    val ingredients_en: String,
    val ingredients_pl: String,
    val price: Float,
    var quantity: Int,
    val type: Int,
    val image: Bitmap
){
    var number: Int = 0
}