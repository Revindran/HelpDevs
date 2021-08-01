package com.raveendran.helpdevs.models

import java.io.Serializable

data class ChatGroup(
    val groupName: String = "",
    val createdBy: String = "",
    val createdTime: String = "",
    val timeStamp: Long = 0L,
    val lastChat: String = "",
    val lastChatMemberName: String = "",
    val lastChatTime: String = "",
) : Serializable