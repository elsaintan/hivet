package com.seaID.hivet.models

class jadwal {
    var id : String?=null
    var tanggal : String?=null
    var start : String?= null
    var end : String?= null
    var duration : String ?= null
    var slot : String?= null

    constructor(id: String?, tanggal: String?, start: String?, end: String?, duration: String?, slot: String?) {
        this.id = id
        this.tanggal = tanggal
        this.start = start
        this.end = end
        this.duration = duration
        this.slot = slot
    }

    constructor()
}