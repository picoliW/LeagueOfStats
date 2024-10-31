package com.example.lol.data.models

data class ItemsModel(
    val name: String,
    val description: String,
    val price: Price,
    val purchasable: Boolean,
    val iconUrl: String
)

data class Price(
    val base: Int,
    val total: Int,
    val sell: Int
)

