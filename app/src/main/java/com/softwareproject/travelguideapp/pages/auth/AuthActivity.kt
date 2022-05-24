package com.softwareproject.travelguideapp.pages.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.softwareproject.travelguideapp.R
import com.softwareproject.travelguideapp.pages.auth.signin.SigninFragment
import com.softwareproject.travelguideapp.util.ActivityUtil

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        startFragment()
    }

    private fun startFragment(){
        ActivityUtil().changeFragmentWithStack(SigninFragment(), SigninFragment.TAG, supportFragmentManager,R.id.act_auth_fl_container)
    }
}
