package com.softwareproject.travelguideapp.pages.homepage

class HomePagePresenter: HomePageContract.Presenter{

    private var view : HomePageContract.View? = null


    constructor(view: HomePageContract.View){
        this.view = view
    }

    override fun onStart() {

    }

    override fun onDestroy() {

    }

}