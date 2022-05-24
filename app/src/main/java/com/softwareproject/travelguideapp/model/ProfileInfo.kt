package com.softwareproject.travelguideapp.model

class ProfileInfo {
    var userID: Int = 0
    var username: String = ""
    var email:String = ""
    var age:String? = ""
    var realName: String? = ""

    constructor(username:String , email: String, age: String?, realName: String?) {
        this.username = username
        this.email = email
        this.age = age
        this.realName = realName
    }
}