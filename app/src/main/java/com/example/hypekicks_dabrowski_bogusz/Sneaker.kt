package com.example.hypekicks_dabrowski_bogusz

import java.io.Serializable

data class Sneaker(
    var id: String = "",
    val brand: String = "",
    val modelName: String = "",
    val releaseYear: Int = 0,
    val resellPrice: Double = 0.0,
    val imageUrl: String = ""
) : Serializable