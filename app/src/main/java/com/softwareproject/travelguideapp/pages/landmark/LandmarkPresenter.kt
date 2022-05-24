package com.softwareproject.travelguideapp.pages.landmark

import android.util.Log
import com.softwareproject.travelguideapp.`interface`.RequestCallBackListener
import com.softwareproject.travelguideapp.model.Comment
import com.softwareproject.travelguideapp.model.UserScore
import com.softwareproject.travelguideapp.util.Constants
import com.softwareproject.travelguideapp.util.HttpRequest
import com.softwareproject.travelguideapp.util.JsonParser
import org.json.JSONObject

class LandmarkPresenter : LandmarkContract.Presenter {

    private var view : LandmarkContract.View? = null
    private var parser = JsonParser()
    constructor(view: LandmarkContract.View){
        this.view = view
    }

    override fun onStart() {

    }

    override fun onDestroy() {
    }

    override fun getLandmarkInfo(landmarkId : Int, userId: Int) {
        val url = "landmarks/detail?landmarkId=$landmarkId&userId=$userId"
        val method = Constants.HTTP_GET
        val ls = object : RequestCallBackListener {
            override fun onRequestCompleted(result: JSONObject?, error : JSONObject?) {
                try {
                    if(result != null){
                        val landmarkDetails = parser.parseLandmarkDetails(result)
                        view?.updateLandmarkInfo(landmarkDetails.landmark, landmarkDetails.score.avgScore)
                        view?.updateFeed(landmarkDetails.comments)
                        landmarkDetails.userComment?.let {
                            view?.updateUserComment(it, landmarkDetails.score.userScore)
                        }
                    }
                    else if(error != null) {
                        view?.showErrorDialog(JsonParser().parseError(error))
                    }
                } catch (e: Exception) {
                    Log.e("exception:: ", e.toString())
                }
            }
        }
        val request = HttpRequest(url, method, null, listenerRequest = ls)
        request.execute()
    }

    override fun postUserComment(comment: String,landmarkId: Int, commentExists: Comment?, userId: Int) {
        if(commentExists != null){
            updateComment(comment,commentExists)
            return
        }
        val url = "comments"
        val method = Constants.HTTP_POST
        val body = JSONObject().put("landmarkId",landmarkId).put("userComment",comment).put("userId",userId)
        val ls = object : RequestCallBackListener {
            override fun onRequestCompleted(result: JSONObject?, error : JSONObject?) {
                try {
                    if(result != null){
                        view?.getLandmarkInfo()
                    }
                    else if(error != null) {
                        view?.showErrorDialog(JsonParser().parseError(error))
                    }
                } catch (e: Exception) {

                }
            }
        }
        val request = HttpRequest(url, method, body, listenerRequest = ls)
        request.execute()
    }

    private fun updateComment(comment: String, exists: Comment){
        val url = "comments/${exists.id}"
        val method = Constants.HTTP_POST
        val body = JSONObject().put("userComment",comment)
        val ls = object : RequestCallBackListener {
            override fun onRequestCompleted(result: JSONObject?, error : JSONObject?) {
                try {
                    if(result != null){
                        view?.getLandmarkInfo()
                    }
                    else if(error != null) {
                        view?.showErrorDialog(JsonParser().parseError(error))
                    }
                } catch (e: Exception) {

                }
            }
        }
        val request = HttpRequest(url, method, body, listenerRequest = ls)
        request.execute()
    }

    override fun postUserScore(rating: Float,landmarkId: Int,scoreExists: UserScore? ,userId: Int) {
        if(scoreExists != null){
            updateScore(rating, scoreExists)
            return
        }
        val url = "scores"
        val method = Constants.HTTP_POST
        val body = JSONObject().put("landmarkId",landmarkId).put("userScore",rating.toInt()).put("userId",userId)
        val ls = object : RequestCallBackListener {
            override fun onRequestCompleted(result: JSONObject?, error : JSONObject?) {
                try {
                    if(result != null){
                        view?.getLandmarkInfo()
                    }
                    else if(error != null) {
                        view?.showErrorDialog(JsonParser().parseError(error))
                    }
                } catch (e: Exception) {

                }
            }
        }
        val request = HttpRequest(url, method, body, listenerRequest = ls)
        request.execute()
    }

    private fun updateScore(rating: Float, userScore: UserScore){
        val url = "scores/${userScore.scoreId}"
        val method = Constants.HTTP_POST
        val body = JSONObject().put("score",rating.toInt())
        val ls = object : RequestCallBackListener {
            override fun onRequestCompleted(result: JSONObject?, error : JSONObject?) {
                try {
                    if(result != null){
                        view?.getLandmarkInfo()
                    }
                    else if(error != null) {
                        view?.showErrorDialog(JsonParser().parseError(error))
                    }
                } catch (e: Exception) {
                }
            }
        }
        val request = HttpRequest(url, method, body, listenerRequest = ls)
        request.execute()
    }
}