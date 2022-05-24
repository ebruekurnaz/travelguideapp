package com.softwareproject.travelguideapp.pages.auth.signin

import com.softwareproject.travelguideapp.core.BasePresenter
import com.softwareproject.travelguideapp.core.BaseView

interface SigninContract {

    interface View: BaseView<Presenter> {

        fun onUserLoginSuccess(userID: Int)
        fun onRequestError(errorMsg: String)

    }

    interface Presenter: BasePresenter {

        fun checkAuth(username: String, password:String)
    }
}