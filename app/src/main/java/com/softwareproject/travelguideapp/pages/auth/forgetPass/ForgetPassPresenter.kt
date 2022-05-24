package com.softwareproject.travelguideapp.pages.auth.forgetPass

import com.softwareproject.travelguideapp.`interface`.RequestCallBackListener
import com.softwareproject.travelguideapp.util.Constants
import com.softwareproject.travelguideapp.util.HttpRequest
import com.softwareproject.travelguideapp.util.JsonParser
import org.json.JSONObject

class ForgetPassPresenter : ForgetPassContract.Presenter {

    private var view: ForgetPassContract.View? = null

    constructor(view: ForgetPassContract.View){
        this.view = view
        view.setPresenter(this)
    }

    override fun onStart() {

    }

    override fun onDestroy() {

    }
    override fun changePassword(username: String, newPassword: String, secretAnswer: String) {
        val body = JSONObject()
        body.put("newPassword", newPassword)
        body.put("userName", username)
        body.put("secretAnswer", secretAnswer)
        HttpRequest("users/change/password", Constants.HTTP_POST, body, listenerRequest = object : RequestCallBackListener{
            override fun onRequestCompleted(result: JSONObject?, error: JSONObject?) {
                if (result != null){
                    view?.onPasswordChanged()
                }
                else if(error != null){
                    view?.onRequestError(JsonParser().parseError(error))
                }
            }
        }).execute()
    }
}