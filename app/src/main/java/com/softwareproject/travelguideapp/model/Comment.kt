package com.softwareproject.travelguideapp.model

class Comment {
    var id: Int
    var username: String = ""
    var icon : String = ""
    var comment : String = ""
    var time : Long


    constructor(id: Int, name : String, icon: String, comment: String, time: Long){
        this.id = id
        this.username = name
        this.icon = icon
        this.comment = comment
        this.time = time
    }
}