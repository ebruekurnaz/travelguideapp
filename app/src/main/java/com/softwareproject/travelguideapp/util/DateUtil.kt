package com.softwareproject.travelguideapp.util

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DateUtil {
    fun convertTimeStampToStringDate(timeStamp: Long){
        val dateString = SimpleDateFormat("dd/MM/yyyy",Locale("tr", "TR")).format(Date(timeStamp))
    }
    fun calculateDayDifference(timeStamp: Long): String{
        val msDiff = Calendar.getInstance().timeInMillis - timeStamp
        val daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff)
        if (daysDiff < 1){
            return "Birkaç saat önce"
        }
        else if(daysDiff < 7 ){
            return "$daysDiff gün önce"
        }
        else if(daysDiff < 30 ){
            return "${daysDiff/7} hafta önce"
        }
        else if(daysDiff < 365 ){
            return "${daysDiff/30} ay önce"
        }
        else{
            return "${daysDiff/365} yıl önce"
        }
    }
}