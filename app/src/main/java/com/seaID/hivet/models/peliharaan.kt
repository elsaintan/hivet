package com.seaID.hivet.models

class peliharaan {
    var pemilik : String?= null
    var nama : String ?= null
    var jenis : String ?= null
    var keterangan : String ?= null

    constructor()
    constructor(pemilik: String?, Name: String?, jenis: String?, keterangan: String?) {
        this.pemilik = pemilik
        this.nama = Name
        this.jenis = jenis
        this.keterangan = keterangan
    }


}