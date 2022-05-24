package com.softwareproject.travelguideapp.pages.auth.signup

import android.util.Log
import com.softwareproject.travelguideapp.`interface`.RequestCallBackListener
import com.softwareproject.travelguideapp.util.HttpRequest
import com.softwareproject.travelguideapp.util.JsonParser
import org.json.JSONObject

class SignupPresenter: SignupContract.Presenter, RequestCallBackListener {
    override fun saveUser(
        email: String,
        username: String,
        password: String,
        quest: String,
        answer: String
    ) {
        val body = JSONObject()
        body.put("email", email)
        body.put("password", password)
        body.put("userName", username)
        body.put("secretQuestion", quest)
        body.put("secretAnswer", answer)
        HttpRequest("users/register", "POST", body = body, listenerRequest = this).execute()
    }

    override fun onRequestCompleted(result: JSONObject?, error: JSONObject?) {
        super.onRequestCompleted(result, error)
        if(result != null){
            val recordedUserId = JsonParser().parseSignUpResult(result)
            view?.onUserSaved(recordedUserId)
        }
        else if(error != null){
            view?.onUserSaveFailed(JsonParser().parseError(error))
        }
    }

    private var view: SignupContract.View? = null

    constructor(view: SignupContract.View){
        this.view = view
        view.setPresenter(this)
    }


    override fun onStart() {

    }

    override fun onDestroy() {

    }
}