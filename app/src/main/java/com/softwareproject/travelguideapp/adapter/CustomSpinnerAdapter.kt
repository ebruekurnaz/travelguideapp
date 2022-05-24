package com.softwareproject.travelguideapp.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.softwareproject.travelguideapp.R

class CustomSpinnerAdapter<T> : ArrayAdapter<T> {
    private val layoutInflater: LayoutInflater
    private val title: String
    private val dataList: List<T?>

    constructor(context: Context, title: String, dataList: List<T?>) : super(context, 0) {
        layoutInflater = LayoutInflater.from(context)
        this.title = title
        this.dataList = convertListWithTitle(dataList)
    }

    companion object {
        private val TITLE_COLOR = Color.parseColor("#bdbcbc")
    }

    override fun getCount(): Int {
        return dataList.count()
    }

    override fun getItem(position: Int): T? {
        return dataList[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
       return createView(position, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createView(position, parent)
    }

    private fun convertListWithTitle(dataList: List<T?>): List<T?> {
        val mutatedList = dataList.toMutableList()
        mutatedList.add(0, null)

        return mutatedList.toList()
    }

    private fun createView(position: Int, parent: ViewGroup?): View {
        val view =  layoutInflater.inflate(R.layout.row_spinner, parent, false)

        val tvText = view.findViewById<TextView>(R.id.row_spinner_tv_itemText)

        val item = dataList[position]

        if (item == null) {
            tvText.setTextColor(TITLE_COLOR)
        }

        tvText.text = item?.toString() ?: title

        return view
    }

    override fun isEnabled(position: Int): Boolean {
        return dataList[position] != null
    }
}