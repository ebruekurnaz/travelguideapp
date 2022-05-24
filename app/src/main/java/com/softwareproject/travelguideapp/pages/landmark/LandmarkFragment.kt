package com.softwareproject.travelguideapp.pages.landmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.clans.fab.FloatingActionButton
import com.ramotion.foldingcell.FoldingCell
import com.softwareproject.travelguideapp.R
import com.softwareproject.travelguideapp.adapter.CommentItemRecyclerAdapter
import com.softwareproject.travelguideapp.model.Comment
import com.softwareproject.travelguideapp.model.Landmark
import com.softwareproject.travelguideapp.model.UserScore
import com.softwareproject.travelguideapp.util.ActivityUtil
import com.softwareproject.travelguideapp.util.DateUtil
import com.tuyenmonkey.mkloader.MKLoader
import java.util.*

class LandmarkFragment : Fragment(), LandmarkContract.View {

    private var presenter: LandmarkContract.Presenter? = null
    private lateinit var foldingCell: FoldingCell
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var feedRecyclerView: RecyclerView

    //landmark information card
    private lateinit var infoLandmarkNameTextView : TextView
    private lateinit var infoLandmarkLocationTextView: TextView
    private lateinit var infoAboutLandmarkTextView: TextView

    //landmark image card
    private lateinit var imgLandmarkImageView : ImageView
    private lateinit var imgLandmarkNameTextView: TextView
    private lateinit var imgLandmarkScoreTextView: TextView

    //user comment layout
    private lateinit var userCommentLayout: LinearLayout
    private lateinit var userCommentTextView: TextView
    private lateinit var userCommentNameTextView: TextView
    private lateinit var userCommentDate: TextView

    private var userComment : Comment? = null
    private var userScore : UserScore? = null

    private lateinit var loaderView: MKLoader
    private var landmarkId : Int? = null
    companion object{
        const val TAG = "LandmarkFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_landmark, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            landmarkId = it.getInt("landmarkId")
        }
        setPresenter(LandmarkPresenter(this))
        init(view)
        openProgress()
        setAttributes()
        getLandmarkInfo()
    }

    override fun setPresenter(presenter: LandmarkContract.Presenter) {
        this.presenter = presenter
    }

    private fun init(view: View){
        foldingCell =  view.findViewById(R.id.frag_landmark_fc_landmark)
        floatingActionButton = view.findViewById(R.id.frag_landmark_fab)

        infoLandmarkNameTextView = view.findViewById(R.id.card_landmarkInfo_tv_name)
        infoAboutLandmarkTextView = view.findViewById(R.id.card_landmarkInfo_tv_info)
        infoLandmarkLocationTextView = view.findViewById(R.id.card_landmarkInfo_tv_location)

        imgLandmarkImageView = view.findViewById(R.id.card_landmarkPic_iv_picture)
        imgLandmarkNameTextView = view.findViewById(R.id.card_landmarkPic_tv_name)
        imgLandmarkScoreTextView = view.findViewById(R.id.card_landmarkPic_tv_score)

        userCommentLayout = view.findViewById(R.id.frag_landmark_ll_self_comment)
        userCommentTextView = view.findViewById(R.id.item_feed_tv_comment)
        userCommentNameTextView = view.findViewById(R.id.item_feed_username)
        userCommentDate = view.findViewById(R.id.item_feed_tv_time)

        feedRecyclerView = view.findViewById(R.id.frag_landmark_rv_feed)
        feedRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        loaderView = view.findViewById(R.id.frag_landmark_loader)
    }

    private fun setAttributes(){
        foldingCell.setOnClickListener {
            foldingCell.toggle((false))
        }

        floatingActionButton.setOnClickListener {
            userCommentDialog()
        }
    }

    override fun getLandmarkInfo() {
        landmarkId?.let {
            presenter?.getLandmarkInfo(it, ActivityUtil().getUserIDFromSharedPref(context!!))
        }
    }

    private fun userCommentDialog() {
        context?.let {
            val view =
                LayoutInflater.from(it).inflate(R.layout.content_comment_dialog, null, false)
            val builder = AlertDialog.Builder(it).setView(view)
            val dialog: AlertDialog = builder.create()

            val comment = view.findViewById<EditText>(R.id.con_commentDialog_et_comment)
            val ratingBar = view.findViewById<RatingBar>(R.id.con_commentDialog_ratingbar)

            if(userComment != null){
                comment.setText(userComment!!.comment)
            }
            if(userScore != null){
                ratingBar.rating = userScore!!.score.toFloat()
            }

            val shareButton: Button = view.findViewById(R.id.con_commentDialog_btn_share)
            shareButton.setOnClickListener {
                if(getUserandSendComment(comment.text.toString(),ratingBar.rating))
                    dialog.dismiss()
                else{
                    Toast.makeText(context,"Lütfen yorum yapın ya da puan verin", Toast.LENGTH_LONG).show()
                }
            }
            val cancelButton : Button = view.findViewById(R.id.con_commentDialog_btn_cancel)
            cancelButton.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    override fun showErrorDialog(error : String) {

    }

    override fun updateLandmarkInfo(landmark: Landmark, score: Float) {
        closeProgress()
        infoLandmarkNameTextView.text = landmark.landmarkName
        infoAboutLandmarkTextView.text = landmark.information
        infoLandmarkLocationTextView.text = "${landmark.city}/${landmark.country}"

        imgLandmarkNameTextView.text = landmark.landmarkName
        imgLandmarkScoreTextView.text = score.toString()

        context?.let {
            Glide.with(it)
                .load(landmark.imageUrl)
                .placeholder(R.drawable.basic_ripple_effect)
                .into(imgLandmarkImageView)
        }
    }

    override fun updateFeed(comments: List<Comment>) {
        context?.let { feedRecyclerView.adapter = CommentItemRecyclerAdapter(it, comments ) }
    }

    override fun updateUserComment(comment: Comment, userScore: UserScore?) {
        userComment = comment
        userCommentLayout.visibility = View.VISIBLE
        userCommentTextView.text = comment.comment
        userCommentNameTextView.text = comment.username

        userCommentDate.text = DateUtil().calculateDayDifference(comment.time)

        this.userScore = userScore
    }

    override fun getUserandSendComment(comment: String?, rating: Float) : Boolean{
        var userId = ActivityUtil().getUserIDFromSharedPref(context!!)
        if(comment.isNullOrEmpty()){
            if(rating == 0f) return false
        }
        presenter?.postUserComment(comment!!, landmarkId!!,userComment,userId)
        if(rating != 0f){
            presenter?.postUserScore(rating,landmarkId!!, userScore,userId)
        }
        return true
    }

    private fun closeProgress(){
        loaderView.visibility = View.GONE
    }

    private fun openProgress(){
        loaderView.visibility = View.VISIBLE
    }
}
