package com.softwareproject.travelguideapp.util

import java.util.*

class StringUtil {

    fun toLower(string: String): String{
        return string.toLowerCase(Locale("tr", "TR"))
    }
}