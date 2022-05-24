package com.softwareproject.travelguideapp.pages.search

import android.util.Log
import com.softwareproject.travelguideapp.`interface`.RequestCallBackListener
import com.softwareproject.travelguideapp.util.HttpRequest
import com.softwareproject.travelguideapp.util.JsonParser
import org.json.JSONObject

class SearchPresenter: SearchContract.Presenter, RequestCallBackListener{

    private var view: SearchContract.View? = null

    constructor(view: SearchContract.View){
        this.view = view
        view.setPresenter(this)
    }

    override fun getLandmarkList(){
        HttpRequest("landmarks", "GET",listenerRequest = this).execute()
    }

    override fun onStart() {}

    override fun onDestroy() {}

    override fun onRequestCompleted(result: JSONObject?, error: JSONObject?) {
        super.onRequestCompleted(result, error)
        if (result != null && result.has("data")){
            val searchItems = JsonParser().parseSearchItemList(result.getJSONArray("data"))
            view?.onSearchItemsFetched(searchItems)
        }
        else if(error != null){
            view?.onRequestError(JsonParser().parseError(error))
        }
    }
}