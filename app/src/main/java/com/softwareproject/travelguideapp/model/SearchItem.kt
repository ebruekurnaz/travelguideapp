package com.softwareproject.travelguideapp.model

import com.softwareproject.travelguideapp.R
import com.softwareproject.travelguideapp.adapter.DynamicSearchAdapter
import com.softwareproject.travelguideapp.util.StringUtil
import org.json.JSONObject

class SearchItem() : DynamicSearchAdapter.Searchable{

    constructor(id:Int, name: String, city: String, country: String, imageURL: String) : this() {
        this.itemID = id
        this.itemName = name
        this.itemCity = city
        this.itemCountry = country
        this.itemURL = imageURL
    }

    override fun getSearchCriteria(): String {
        return StringUtil().toLower( itemName + itemCity + itemCountry)
    }

    var itemID: Int= -1
    var itemName: String = ""
    var itemCity: String = ""
    var itemCountry: String = ""
    var itemURL: String = ""


    // Get combined string of city and country
    fun getPlaceInfo() = "$itemName $itemCity/$itemCountry"
}