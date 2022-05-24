package com.softwareproject.travelguideapp.pages.landmark

import com.softwareproject.travelguideapp.core.BasePresenter
import com.softwareproject.travelguideapp.core.BaseView
import com.softwareproject.travelguideapp.model.Comment
import com.softwareproject.travelguideapp.model.Landmark
import com.softwareproject.travelguideapp.model.UserScore

interface LandmarkContract {

    interface View : BaseView<Presenter> {
        fun updateLandmarkInfo(landmark : Landmark, score: Float)
        fun updateFeed(comments: List<Comment>)
        fun updateUserComment(comment: Comment, userScore: UserScore?)
        fun showErrorDialog(error: String)
        fun getUserandSendComment(comment: String?, rating: Float): Boolean
        fun getLandmarkInfo()
    }
    interface Presenter : BasePresenter {
        fun getLandmarkInfo(landmarkId: Int, userId: Int)
        fun postUserComment(comment: String,landmarkId: Int, commentExists: Comment? , userId: Int)
        fun postUserScore(rating: Float, landmarkId: Int,scoreExists: UserScore?,userId: Int)
    }
}