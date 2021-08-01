package com.raveendran.helpdevs.models

import java.io.Serializable

data class Dev(
    val id: Int = 0,
    val image: String = "",
    val title: String = "",
    val url: String = "",
    val desc: String = "",
) : Serializable