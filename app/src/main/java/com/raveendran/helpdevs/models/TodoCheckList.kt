package com.raveendran.helpdevs.models

class TodoCheckList(
    val title: String = "",
    val checked: Boolean = false,
    val id: String = "",
    val percentage: Int = 0,
    val timeStamp: Long = System.currentTimeMillis(),
    val createdTime: String = "",
)