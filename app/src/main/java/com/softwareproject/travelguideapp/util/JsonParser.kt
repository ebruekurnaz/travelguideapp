package com.softwareproject.travelguideapp.util

import com.softwareproject.travelguideapp.R
import com.softwareproject.travelguideapp.model.ProfileInfo
import com.softwareproject.travelguideapp.model.SearchItem
import com.softwareproject.travelguideapp.model.*
import org.json.JSONArray
import org.json.JSONObject

class JsonParser {

    private fun parseSearchItem(item: JSONObject): SearchItem{
        val itemID = item["id"].toString().toInt()
        val itemName = item["landmarkName"].toString()
        val itemCity = item["city"].toString()
        val itemCountry = item["country"].toString()
        val itemURL = item["imageUrl"].toString()

        return SearchItem(itemID, itemName, itemCity, itemCountry,
            itemURL)
    }

    fun parseSearchItemList(items: JSONArray): MutableList<SearchItem>{
        val searchItems = mutableListOf<SearchItem>()
        for (i in 0 until items.length()) {
            val item = items.getJSONObject(i)
            searchItems.add(i, parseSearchItem(item))
        }
        return searchItems
    }

    fun parseSignUpResult(item: JSONObject): Int {
        return item["id"].toString().toInt()
    }

    fun parseProfileInfo(item: JSONObject) : ProfileInfo {

        val age =
            if (item.isNull("age"))
                null
            else item["age"].toString()

        val email = item["email"].toString()

        val realName =
            if (item.isNull("realName"))
            null
        else item["realName"].toString()

        val userName = item["userName"].toString()
        return ProfileInfo(userName, email, age, realName)
    }

    fun parseLandmarkDetails(item: JSONObject): LandmarkInfoAll {
        val details = item.getJSONObject("landmarkDetails")
        val score = item.getJSONObject("score")
        val comments = item.getJSONObject("comments")
        val allComments = comments.getJSONArray("allComments")
        val commentList = mutableListOf<Comment>()
        for(i in 0 until allComments.length()){
            commentList.add(parseCommentObject(allComments.getJSONObject(i)))
        }
        var userComment : Comment? = null
        if(!comments.isNull("userComment")){
            userComment = parseCommentObject(comments.getJSONObject("userComment"))
        }
        return LandmarkInfoAll(parseLandmarkObject(details), parseScoreObject(score), commentList.toList(), userComment )
    }

    private fun parseLandmarkObject(item: JSONObject) : Landmark {
        val name = item["landmarkName"].toString()
        val imageUrl = item["imageUrl"].toString()
        val country = item["country"].toString()
        val city = item["city"].toString()
        val info = item["information"].toString()

        return Landmark(name,imageUrl, info, city, country)
    }

    private fun parseScoreObject(item: JSONObject): Score {
        var landmarkId = item["landmarkId"].toString().toInt()
        var score : UserScore? = null
        if(!item.isNull("userScore")){
            var scoreItem = item.getJSONObject("userScore")
            score = UserScore(scoreItem["score"].toString().toInt(), scoreItem["scoreId"].toString().toInt())
        }
        val avg = item["avgScore"].toString().toFloat()
        return Score(landmarkId,score,avg)
    }

    private fun parseCommentObject(item: JSONObject): Comment{
        val id = item["id"].toString().toInt()
        val comment = item["userComment"].toString()
        val name = item["userName"].toString()
        val time = item["createdDate"].toString().toLong()
        return Comment(id, name, "", comment, time)
    }

    fun parseError(item : JSONObject) : String{
        return if (item.isNull("message"))
                "Bilinmeyen Hata"
            else item["message"].toString()
    }

}