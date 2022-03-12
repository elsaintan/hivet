package com.seaID.hivet.models

class Rating {
    var id_konsul : String ?= null
    var id_drh : String?= null
    var id_user : String ?= null
    var rating : Float ?= null

    constructor()
    constructor(id_konsul: String?, id_drh: String?, id_user: String?, rating: Float?) {
        this.id_konsul = id_konsul
        this.id_drh = id_drh
        this.id_user = id_user
        this.rating = rating
    }


}