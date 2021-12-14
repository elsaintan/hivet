package com.seaID.hivet.models

class User {
    var name: String? = null
    var email: String? = null
    var uid: String? = null
    var photoProfile : String? = null

    constructor(){}

    constructor(name: String?, email: String?, uid: String?, photoProfile: String?) {
        this.name = name
        this.email = email
        this.uid = uid
        this.photoProfile = photoProfile
    }

}