package com.seaID.hivet.models

import com.seaID.hivet.`interface`.Message
import com.seaID.hivet.`interface`.MessageType
import java.util.*

data class textMessage(override val text: String,
                       override val time: Date,
                       override val senderId: String,
                       override val type: String = MessageType.TEXT) : Message{
                           constructor() : this("", Date(0), "")

}
