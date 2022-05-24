package com.softwareproject.travelguideapp.pages.auth.signup

import com.softwareproject.travelguideapp.core.BasePresenter
import com.softwareproject.travelguideapp.core.BaseView

interface SignupContract {

    interface View: BaseView<Presenter> {
        fun onUserSaved(userID: Int)
        fun onUserSaveFailed(errorMsg: String)
    }

    interface Presenter: BasePresenter {
        fun saveUser(email: String, username: String, password: String, quest: String, answer: String)
    }
}