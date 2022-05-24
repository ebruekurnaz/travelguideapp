package com.softwareproject.travelguideapp.model

class Score {
    var landmarkId : Int
    var userScore: UserScore?
    var avgScore : Float
    constructor(landmarkId: Int, userScore:UserScore?, avgScore: Float){
        this.landmarkId = landmarkId
        this.userScore = userScore
        this.avgScore = avgScore
    }
}