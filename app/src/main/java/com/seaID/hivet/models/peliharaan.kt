package com.seaID.hivet.models

class peliharaan {
    var id : String ?= null
    var pemilik : String?= null
    var nama : String ?= null
    var jenis : String ?= null
    var keterangan : String ?= null

    constructor()
    constructor(id: String, pemilik: String?, Name: String?, jenis: String?, keterangan: String?) {
        this.id = id
        this.pemilik = pemilik
        this.nama = Name
        this.jenis = jenis
        this.keterangan = keterangan
    }


}