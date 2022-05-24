package com.softwareproject.travelguideapp.pages.search

import com.softwareproject.travelguideapp.core.BasePresenter
import com.softwareproject.travelguideapp.core.BaseView
import com.softwareproject.travelguideapp.model.SearchItem

interface SearchContract {
    interface View : BaseView<Presenter> {
        fun onSearchItemsFetched(searchItems: MutableList<SearchItem>)
        fun onRequestError(errorMsg: String)
    }
    interface Presenter : BasePresenter {
        fun getLandmarkList()

    }
}