package com.softwareproject.travelguideapp.pages.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.softwareproject.travelguideapp.R
import com.softwareproject.travelguideapp.pages.auth.camera.CameraFragment
import com.softwareproject.travelguideapp.pages.profile.ProfileFragment
import com.softwareproject.travelguideapp.pages.homepage.HomePageFragment
import com.softwareproject.travelguideapp.pages.landmark.LandmarkFragment
import com.softwareproject.travelguideapp.pages.search.SearchFragment
import com.softwareproject.travelguideapp.util.ActivityUtil

class HomeActivity : AppCompatActivity() {


    private lateinit var bottomNavigationView : BottomNavigationView
    var util = ActivityUtil()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        init()
        setAttributes()
    }

    private fun init(){
        util.changeFragmentWithoutStack(HomePageFragment(), HomePageFragment.TAG, supportFragmentManager, R.id.act_home_fl_container)
        bottomNavigationView = findViewById(R.id.act_home_bottomMenu)
    }

    private fun setAttributes(){
        bottomNavigationView.setOnNavigationItemSelectedListener {
            disableButton(it.itemId)
            changeFragment(it.itemId)
        }
    }

    private fun disableButton(itemId: Int){
        for (i in 0 until bottomNavigationView.menu.size()){
            bottomNavigationView.menu.getItem(i).isEnabled = true
        }
        bottomNavigationView.menu.findItem(itemId).isEnabled = false
    }

    private fun changeFragment(item: Int): Boolean{
        when(item){
            R.id.menu_home_homepage -> {
               util.changeFragmentWithoutStack(HomePageFragment(), HomePageFragment.TAG, supportFragmentManager,R.id.act_home_fl_container)
            }
            R.id.menu_home_camera -> {
                util.changeFragmentWithoutStack(CameraFragment(), CameraFragment.TAG, supportFragmentManager,R.id.act_home_fl_container)
            }

            R.id.menu_home_search -> {
                util.changeFragmentWithoutStack(SearchFragment(""), SearchFragment.TAG, supportFragmentManager,R.id.act_home_fl_container)
            }

            R.id.menu_home_profile -> {
                util.changeFragmentWithoutStack(ProfileFragment(), ProfileFragment.TAG, supportFragmentManager,R.id.act_home_fl_container)
            }
        }
        return true
    }

    // Used functions for opening Search and Camera Fragment inside HomePage fragment

    fun openSearchFragmentWithCityName(cityName: String){
        disableButton(R.id.menu_home_search)
        util.changeFragmentWithoutStack(SearchFragment(cityName), SearchFragment.TAG, supportFragmentManager,R.id.act_home_fl_container)
        bottomNavigationView.menu.getItem(2).isChecked = true

    }

    fun openCameraFragment(){
        bottomNavigationView.selectedItemId = R.id.menu_home_camera
    }
}
