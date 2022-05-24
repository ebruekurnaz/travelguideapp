package com.softwareproject.travelguideapp.adapter

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwareproject.travelguideapp.R
import com.softwareproject.travelguideapp.`interface`.SearchItemClicked
import com.softwareproject.travelguideapp.model.SearchItem

class SearchRecyclerAdapter(private val searchItems: MutableList<SearchItem>,
                            private val searchItemListener: SearchItemClicked,
                            private val context: Context) :
    DynamicSearchAdapter<SearchItem>(searchItems) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.search_list_item, parent,false))
    }

    override fun getItemCount(): Int = searchItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val landmarkItem = searchItems[position]
        // Make score disappear. Search view has access to scores of the landmark.
        holder.landmarkScore.visibility = View.GONE

        holder.landmarkName.text = landmarkItem.getPlaceInfo()
        holder.landmarkImage.setOnClickListener { searchItemListener.onSearchItemClicked(landmarkItem.itemID) }

        Glide.with(context).load(landmarkItem.itemURL).into(holder.landmarkImage)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var landmarkImage: ImageView = view.findViewById(R.id.card_landmarkPic_iv_picture)
        var landmarkName: TextView = view.findViewById(R.id.card_landmarkPic_tv_name)
        var landmarkScore: LinearLayout = view.findViewById(R.id.card_landmarkPic_ll_score)

    }
}