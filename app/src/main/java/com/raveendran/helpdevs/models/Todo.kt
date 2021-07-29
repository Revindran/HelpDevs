package com.raveendran.helpdevs.models

import java.io.Serializable


data class Todo(
    val todo: String = "",
    val time: String = "",
    val timeStamp: Long = 0L,
    val priority: String = "",
    val notes: String = "",
    val id: String = "",
    val progress: Int = 0
) : Serializable
