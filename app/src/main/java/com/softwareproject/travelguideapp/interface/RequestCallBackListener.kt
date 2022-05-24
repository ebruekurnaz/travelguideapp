package com.softwareproject.travelguideapp.`interface`
import org.json.JSONObject

interface RequestCallBackListener {
    fun onRequestCompleted(result : JSONObject?, error : JSONObject?){
    }
}