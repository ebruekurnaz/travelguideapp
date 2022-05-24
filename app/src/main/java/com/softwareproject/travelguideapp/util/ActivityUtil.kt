package com.softwareproject.travelguideapp.util

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.softwareproject.travelguideapp.R

class ActivityUtil {

    val APP_PREF_KEY = "TRAVEL_GUIDE_SHARED_PREF"
    val USERID_PREF_KEY = "SHARE_PREF_USERID"

    fun changeFragmentWithStack(fragment: Fragment, tag: String? = null, manager: FragmentManager,container: Int) {
        manager.apply {
            beginTransaction()
                    .replace(container, fragment, tag)
                    .addToBackStack(tag)
                    .commit()
        }
    }

    fun changeFragmentWithoutStack(fragment: Fragment, tag: String? = null, manager: FragmentManager,container: Int) {
        manager.apply {
            beginTransaction()
                    .replace(container, fragment, tag)
                    .commit()
        }
    }

    fun writeUserIDToSharedPref(context: Context, userID: Int){
        val prefsEditor = context.getSharedPreferences(APP_PREF_KEY, MODE_PRIVATE).edit()
        prefsEditor.putInt(USERID_PREF_KEY, userID)
        prefsEditor.apply()
    }

    fun getUserIDFromSharedPref(context: Context): Int{
        val prefs = context.getSharedPreferences(APP_PREF_KEY, MODE_PRIVATE)
        return prefs.getInt(USERID_PREF_KEY, -1)
    }

    fun deleteUserIDFromSharedPref(context: Context){
        val prefsEditor = context.getSharedPreferences(APP_PREF_KEY, MODE_PRIVATE).edit()
        prefsEditor.remove(USERID_PREF_KEY)
        prefsEditor.apply()
    }

    fun openAlertDialog(context: Context, title: String, message: String,
                        showNegButton: Boolean, posFunc : () -> Unit ){
        val alertDialogBuild = AlertDialog.Builder(context)
            .setTitle(title)
            .setIcon(R.drawable.warning)
            .setMessage(message)
            .setPositiveButton(R.string.okay
            ) { _, _ -> (posFunc())}
        if (showNegButton){
            alertDialogBuild.setNegativeButton(R.string.cancel, null)
        }
        alertDialogBuild.show()
    }
}