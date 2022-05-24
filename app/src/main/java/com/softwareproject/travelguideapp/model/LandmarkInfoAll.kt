package com.softwareproject.travelguideapp.model

class LandmarkInfoAll {
    var landmark : Landmark
    var score: Score
    var comments : List<Comment>
    var userComment: Comment? = null
    constructor(landmark: Landmark,score:Score, comments: List<Comment>, userComment: Comment?){
        this.landmark = landmark
        this.score = score
        this.comments = comments
        this.userComment = userComment
    }
}