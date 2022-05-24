package com.softwareproject.travelguideapp.pages.profile

import com.softwareproject.travelguideapp.core.BasePresenter
import com.softwareproject.travelguideapp.core.BaseView
import com.softwareproject.travelguideapp.model.ProfileInfo

interface ProfileContract {

    interface View:  BaseView<Presenter>{
        fun setUserInfoFields(user: ProfileInfo)
        fun onRequestError(errorMsg: String)
        fun onUserDeleted()
    }

    interface Presenter: BasePresenter{

        fun updateProfileInfo(userID: Int, realName: String, age: String, email: String)
        fun getProfileInfo(userID: Int)
        fun deleteUser(userID: Int)
    }
}