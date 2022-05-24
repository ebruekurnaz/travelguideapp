package com.softwareproject.travelguideapp.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softwareproject.travelguideapp.R
import com.softwareproject.travelguideapp.model.Comment
import com.softwareproject.travelguideapp.util.DateUtil
import java.util.*

class CommentItemRecyclerAdapter: RecyclerView.Adapter<CommentItemRecyclerAdapter.ViewHolder>{

    var commentList : List<Comment>
    var context : Context
    constructor(context: Context, commentList: List<Comment>){
        this.commentList = commentList
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.feed_list_item, parent, false))
    }

    companion object {
        var blue = Color.parseColor("#51EBD7")
        var red = Color.parseColor("#B76BFF")
        var orange = Color.parseColor("#ffb741")
        var green = Color.parseColor("#95EF43")

        var iconList = listOf<Int>(R.drawable.girl, R.drawable.boy, R.drawable.boy1,R.drawable.profile_placeholder, R.drawable.man, R.drawable.man1,R.drawable.girl,R.drawable.man2,
            R.drawable.man3,R.drawable.girl,R.drawable.man4)
    }


    override fun getItemCount(): Int {
        return commentList.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var feed = commentList[position]
        holder.viewIndicator.setBackgroundColor(setViewIndicator(position))
        holder.usernameTextView.text = feed.username
        holder.commentTextView.text = feed.comment
        holder.timeTextView.text = DateUtil().calculateDayDifference(feed.time)
        Glide.with(context)
            .load(randomNumber())
            .placeholder(R.drawable.profile_placeholder)
            .into(holder.iconImageView)

    }

    private fun setViewIndicator(position: Int): Int{
        return when(position % 4){
            0 -> {
                blue
            }
            1 -> {
                red
            }
            2 -> {
                orange
            }
            else -> {
                green
            }
        }
    }

    private fun randomNumber(): Int{
        var r = Random()
        var no = r.nextInt(10)
        return iconList[no]
    }


    class ViewHolder: RecyclerView.ViewHolder{
        var viewIndicator: View
        var iconImageView: ImageView
        var usernameTextView : TextView
        var commentTextView : TextView
        var timeTextView: TextView

        constructor(view: View): super(view){
            viewIndicator = view.findViewById(R.id.item_feed_view)
            iconImageView = view.findViewById(R.id.item_feed_iv_usericon)
            usernameTextView = view.findViewById(R.id.item_feed_username)
            commentTextView = view.findViewById(R.id.item_feed_tv_comment)
            timeTextView = view.findViewById(R.id.item_feed_tv_time)
        }
    }
}