package com.seaID.hivet.models

class User {
    var name: String? = null
    var email: String? = null
    var uid: String? = null
    var photoProfile : String? = null

    constructor(){}

    constructor(email: String?, name: String?, photoProfile: String?, uid: String?) {
        this.name = name
        this.email = email
        this.uid = uid
        this.photoProfile = photoProfile
    }

}