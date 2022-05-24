package com.softwareproject.travelguideapp.util

import android.content.Context
import android.widget.Toast
import java.util.regex.Matcher
import java.util.regex.Pattern

class Validation {
    companion object {
        val PHONE = "^[0-9]{10}$"
        val EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$"
        private var pattern : Pattern = Pattern.compile(EMAIL_PATTERN)
        private var matcher : Matcher? = null

        fun validate(mail: String, password: String, context: Context) :  Boolean{
            pattern = Pattern.compile(EMAIL_PATTERN)
            matcher = pattern.matcher(mail)
            if( matcher!!.matches()) {
                if(password.length > 5){
                    return true
                }
                else {
                    Toast.makeText(context, "Şifre 6 karakterden küçük olamaz!", Toast.LENGTH_LONG).show()
                    return false
                }
            }
            else{
                Toast.makeText(context, "Geçersiz Email adresi", Toast.LENGTH_LONG).show()
                return false
            }
        }
    }
}