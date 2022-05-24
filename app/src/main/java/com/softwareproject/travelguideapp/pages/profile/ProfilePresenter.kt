package com.softwareproject.travelguideapp.pages.profile

import android.util.Log
import com.softwareproject.travelguideapp.`interface`.RequestCallBackListener
import com.softwareproject.travelguideapp.util.Constants
import com.softwareproject.travelguideapp.util.HttpRequest
import com.softwareproject.travelguideapp.util.JsonParser
import org.json.JSONObject

class ProfilePresenter : ProfileContract.Presenter{
    private var view: ProfileContract.View? = null

    constructor(view: ProfileContract.View){
        this.view = view
        view.setPresenter(this)
    }

    override fun updateProfileInfo(userID: Int, realName: String, age: String, email: String) {
        val body = JSONObject()
        body.put("age", age)
        body.put("email", email)
        body.put("realName", realName)
        HttpRequest("users/$userID", Constants.HTTP_POST, body = body, listenerRequest = object :  RequestCallBackListener {
            override fun onRequestCompleted(result: JSONObject?, error: JSONObject?) {
                result?.let {
                    val profileInfo = JsonParser().parseProfileInfo(result)
                    view?.setUserInfoFields(profileInfo)
                }
                if(result == null && error != null){
                    view?.onRequestError(JsonParser().parseError(error))
                }
            }
        }).execute()
    }

    override fun onStart() {

    }

    override fun onDestroy() {

    }

    override fun getProfileInfo(userID: Int) {
        HttpRequest("users/$userID", "GET", listenerRequest = object : RequestCallBackListener{
            override fun onRequestCompleted(result: JSONObject?, error: JSONObject?) {
                result?.let {
                    val profileInfo = JsonParser().parseProfileInfo(result)
                    view?.setUserInfoFields(profileInfo)
                }
                if(result == null && error != null){
                    view?.onRequestError(JsonParser().parseError(error))
                }
            }
        }).execute()
    }

    override fun deleteUser(userID: Int) {
        HttpRequest("users/$userID", Constants.HTTP_DELETE, listenerRequest = object : RequestCallBackListener{
            override fun onRequestCompleted(result: JSONObject?, error: JSONObject?) {
                if(result == null && error != null){
                    view?.onRequestError(JsonParser().parseError(error))
                }
                else if(result == null && error == null){
                    view?.onUserDeleted()
                }
            }
        }).execute()
    }

}