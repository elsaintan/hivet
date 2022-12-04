package com.seaID.hivet.models


class peliharaan {
    var id : String ?= null
    var pemilik : String?= null
    var nama : String ?= null
    var jenis : String ?= null
    var keterangan : String ?= null

    constructor(){}

    constructor(id: String, jenis: String?, keterangan: String?, name: String?, pemilik: String?) {
        this.id = id
        this.pemilik = pemilik
        this.nama = name
        this.jenis = jenis
        this.keterangan = keterangan
    }

}