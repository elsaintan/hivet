package com.seaID.hivet.models

class Chat {
    private var sender: String? = null
    private var receiver: String? = null
    private var message: String? = null
    private var id_konsul: String? = null
    private var isseen = false

    fun Chat(sender: String?, receiver: String?, message: String?, id_konsul: String?, isseen: Boolean) {
        this.sender = sender
        this.receiver = receiver
        this.message = message
        this.id_konsul = id_konsul
        this.isseen = isseen
    }
    constructor()

    fun getSender(): String? {
        return sender
    }

    fun setSender(sender: String?) {
        this.sender = sender
    }

    fun getReceiver(): String? {
        return receiver
    }

    fun setReceiver(receiver: String?) {
        this.receiver = receiver
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getIdKonsul(): String? {
        return id_konsul
    }

    fun setIdKonsul(id_konsul: String?) {
        this.id_konsul = id_konsul
    }

    fun isIsseen(): Boolean {
        return isseen
    }

    fun setIsseen(isseen: Boolean) {
        this.isseen = isseen
    }
}