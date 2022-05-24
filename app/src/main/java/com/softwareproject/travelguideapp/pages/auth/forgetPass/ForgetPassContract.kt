package com.softwareproject.travelguideapp.pages.auth.forgetPass

import com.softwareproject.travelguideapp.core.BasePresenter
import com.softwareproject.travelguideapp.core.BaseView

interface ForgetPassContract {

    interface View : BaseView<Presenter> {
        fun onRequestError(errorMsg: String)
        fun onPasswordChanged()
    }

    interface Presenter : BasePresenter {
        fun changePassword(username: String, newPassword: String, secretAnswer: String)
    }

}