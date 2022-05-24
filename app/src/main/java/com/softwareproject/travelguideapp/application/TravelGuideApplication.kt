package com.softwareproject.travelguideapp.application

import android.app.Application
import android.os.AsyncTask
import org.json.JSONObject

class TravelGuideApplication : Application() {

    companion object {
        val profilePageTaskList:ArrayList<AsyncTask<String, Void, JSONObject?>> = ArrayList()
        val PROFILE_PAGE = 1

        fun clearRequests(queue: Int){
            if(queue == PROFILE_PAGE){
                if(profilePageTaskList.isNotEmpty()){
                    for (task in profilePageTaskList){
                        task.cancel(true)
                    }
                }
            }
        }
    }
}