package com.seaID.hivet.models

class Saldo {
    var id : String ?= null
    var id_drh : String ?= null
    var jumlah : String ?= null
    var tarikKe : String ?= null
    var nama : String ?= null
    var nomer : String ?= null
    var tanggal : String ?= null
    var status : String?= null
    var jenis : String?= null

    constructor()

    constructor(
        id: String?,
        id_drh: String?,
        jumlah: String?,
        tarikKe: String?,
        nama: String?,
        nomer: String?,
        tanggal: String?,
        status: String?,
        jenis: String
    ) {
        this.id = id
        this.id_drh = id_drh
        this.jumlah = jumlah
        this.tarikKe = tarikKe
        this.nama = nama
        this.nomer = nomer
        this.tanggal = tanggal
        this.status = status
        this.jenis = jenis
    }
}