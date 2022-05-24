package com.softwareproject.travelguideapp.core


interface BaseView<T: BasePresenter> {
    fun setPresenter(presenter: T)
}