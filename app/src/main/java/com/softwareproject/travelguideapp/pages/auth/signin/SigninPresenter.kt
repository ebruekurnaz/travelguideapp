package com.softwareproject.travelguideapp.pages.auth.signin

import com.softwareproject.travelguideapp.`interface`.RequestCallBackListener
import com.softwareproject.travelguideapp.util.HttpRequest
import com.softwareproject.travelguideapp.util.JsonParser
import org.json.JSONObject

class SigninPresenter : SigninContract.Presenter, RequestCallBackListener{
    private var view: SigninContract.View? = null

    constructor(view: SigninContract.View){
        this.view = view
        view.setPresenter(this)
    }
    override fun onStart() {
    }

    override fun onDestroy() {
    }

    override fun checkAuth(username: String, password: String){
        val body = JSONObject()
        body.put("userName", username)
        body.put("password", password)
        HttpRequest("users/login", "POST", body, listenerRequest = this).execute()
        //HttpRequest("post", "POST", body, listenerRequest = this).execute()
    }

    override fun onRequestCompleted(result: JSONObject?, error: JSONObject?) {
        super.onRequestCompleted(result, error)
        result?.let {
            val userID = JsonParser().parseSignUpResult(result)
            view?.onUserLoginSuccess(userID)
        }
        if (result == null && error != null){
            view?.onRequestError(JsonParser().parseError(error))
        }
    }
}