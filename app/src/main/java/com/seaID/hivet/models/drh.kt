package com.seaID.hivet.models

import java.util.jar.Attributes

class drh{
    var id: String? = null
    var Name: String? = null
    var Contact: String? = null
    var uid: String? = null
    var photoProfile : String? = null
    var email : String? = null
    var SIP : String? = null
    var STR : String? = null
    var konsultasi : String?=null
    var booking : String?=null
    var tempat : String?=null
    var WorkExp : String ?= null
    var alamat : String ?= null
    var harga: String ?= null
    var status: String ?=null
    var tanggal: String ?=null

    constructor()

    constructor(id: String?, Name: String?, Contact: String?, uid: String?, photoProfile: String?, email: String?, SIP: String?, STR: String?, konsultasi: String?, booking: String?, tempat:String?, alamat:String?, WorkExp: String?, harga:String?, status: String?) {
        this.id = id
        this.Name = Name
        this.Contact = Contact
        this.uid = uid
        this.photoProfile = photoProfile
        this.email = email
        this.SIP = SIP
        this.STR = STR
        this.konsultasi = konsultasi
        this.booking = booking
        this.tempat = tempat
        this.WorkExp = WorkExp
        this.harga = harga
        this.alamat = alamat
        this.status = status
    }


}
