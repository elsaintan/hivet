package com.seaID.hivet.models

class Chat {
    private var sender: String? = null
    private var receiver: String? = null
    private var message: String? = null
    private var isseen = false
    private var idkonsul: String? = null

    fun Chat(sender: String?, receiver: String?, message: String?, isseen: Boolean, idkonsul: String?) {
        this.sender = sender
        this.receiver = receiver
        this.message = message
        this.isseen = isseen
        this.idkonsul = idkonsul
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

    fun getIdkonsul(): String? {
        return idkonsul
    }

    fun setIdkonsul(idkonsul: String?) {
        this.idkonsul = idkonsul
    }

    fun isIsseen(): Boolean {
        return isseen
    }

    fun setIsseen(isseen: Boolean) {
        this.isseen = isseen
    }
}