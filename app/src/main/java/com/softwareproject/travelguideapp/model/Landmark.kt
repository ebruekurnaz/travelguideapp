package com.softwareproject.travelguideapp.model

class Landmark {
    var landmarkName : String = ""
    var imageUrl : String = ""
    var information : String = ""
    var city: String = ""
    var country : String = ""

    constructor( name : String, imageUrl: String, info: String, city: String, country: String){
        this.landmarkName = name
        this.imageUrl = imageUrl
        this.information = info
        this.city = city
        this.country = country
    }
}