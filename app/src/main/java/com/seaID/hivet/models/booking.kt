package com.seaID.hivet.models

class booking {
    var kode_booking : String ?= null
    var transaction_id : String ?= null
    var user_id : String ?= null
    var pet_id : String ?= null
    var drh_id : String ?= null
    var waktu : String ?= null
    var tanggal : String ?= null
    var status : String ?= null


    constructor()

    constructor(
        kode_booking: String?,
        transaction_id : String?,
        user_id: String?,
        pet_id: String?,
        drh_id: String?,
        waktu: String?,
        tanggal: String?,
        status: String?
    ) {
        this.kode_booking = kode_booking
        this.transaction_id = transaction_id
        this.user_id = user_id
        this.pet_id = pet_id
        this.drh_id = drh_id
        this.waktu = waktu
        this.tanggal = tanggal
        this.status = status
    }


}