package com.seaID.hivet.`interface`

import java.util.*

object MessageType{
    const val TEXT = "TEXT"
    const val IMAGE = "IMAGE"
}

interface Message {
    val text: String
    val time: Date
    val senderId: String
    val type: String
}