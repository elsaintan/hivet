package com.seaID.hivet.models

class konsultasi {
        var id : String?=null
        var id_drh : String?=null
        var id_user : String?=null
        var id_pet : String?=null
        var tanggal : String?=null
        var status : String?=null

    constructor()
    constructor(
        id: String?,
        id_drh: String?,
        id_user: String?,
        id_pet: String?,
        tanggal: String?,
        status: String?
    ) {
        this.id = id
        this.id_drh = id_drh
        this.id_user = id_user
        this.id_pet = id_pet
        this.tanggal = tanggal
        this.status = status
    }




}