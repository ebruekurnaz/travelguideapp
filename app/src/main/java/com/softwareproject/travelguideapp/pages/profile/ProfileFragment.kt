package com.softwareproject.travelguideapp.pages.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.softwareproject.travelguideapp.R
import com.softwareproject.travelguideapp.model.ProfileInfo
import com.softwareproject.travelguideapp.util.ActivityUtil
import com.tuyenmonkey.mkloader.MKLoader

class ProfileFragment() : Fragment(), ProfileContract.View{

    private lateinit var usernameText: TextView
    private lateinit var ageEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var realNameEdit: EditText
    private lateinit var logoutImage: ImageView
    private lateinit var updateButton: Button
    private lateinit var editButton: Button
    private lateinit var loaderView: MKLoader
    private lateinit var deleteImage: Button

    private var presenter: ProfileContract.Presenter? = null


    override fun setPresenter(presenter: ProfileContract.Presenter) {
        this.presenter = presenter
        val mContext = context
        mContext?.let {
            val userID = ActivityUtil().getUserIDFromSharedPref(mContext)
            this.presenter?.getProfileInfo(userID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Open progress to wait while fetching profile info
        init(view)
        openProgress()
        setAttributes()
        presenter = ProfilePresenter(this)
    }

    private fun init(view : View){
        usernameText = view.findViewById(R.id.frag_profile_tv_username)
        ageEdit = view.findViewById(R.id.frag_profile_et_age)
        emailEdit = view.findViewById(R.id.frag_profile_et_email)
        realNameEdit = view.findViewById(R.id.frag_profile_et_realname)
        logoutImage = view.findViewById(R.id.frag_profile_iv_logout)
        updateButton = view.findViewById(R.id.frag_profile_bt_update)
        editButton = view.findViewById(R.id.frag_profile_bt_edit)
        loaderView = view.findViewById(R.id.frag_profile_loader)
        deleteImage = view.findViewById(R.id.frag_profile_iv_delete)
    }

    private fun setAttributes(){
        editButton.setOnClickListener {  switchToEditView() }
        updateButton.setOnClickListener { updateUserInfo() }
        logoutImage.setOnClickListener { showAlertDialogToLogOut() }
        deleteImage.setOnClickListener { deleteUser() }
    }

    private fun deleteUser(){
        context?.let {
            val userId = ActivityUtil().getUserIDFromSharedPref(it)
            val title = it.getString(R.string.header_attention)
            val message = it.getString(R.string.message_delete_user)
            ActivityUtil().openAlertDialog(it,title, message, true){ presenter?.deleteUser(userId)}
        }
    }

    // Get input from EditTexts and send to presenter
    private fun updateUserInfo(){
        val mContext = context
        mContext?.let {

            openProgress()

            val userID = ActivityUtil().getUserIDFromSharedPref(mContext)
            val realName = realNameEdit.text.toString()
            val age= ageEdit.text.toString()
            val email = emailEdit.text.toString()
            presenter?.updateProfileInfo(userID, realName, age, email)
        }
    }

    override fun onUserDeleted() {
        context?.let {
            ActivityUtil().deleteUserIDFromSharedPref(it)
            activity?.finish()
        }
    }

    override fun setUserInfoFields(user: ProfileInfo) {
        closeProgress()
        realNameEdit.setText(user.realName)
        usernameText.text  = user.username
        ageEdit.setText(user.age)
        emailEdit.setText(user.email)
        switchToMonitorView()
    }

    private fun switchToEditView(){
        ageEdit.isEnabled = true
        realNameEdit.isEnabled = true
        emailEdit.isEnabled = true
        updateButton.visibility = View.VISIBLE
        editButton.visibility = View.GONE
        deleteImage.visibility = View.VISIBLE
    }

    private fun switchToMonitorView(){
        ageEdit.isEnabled = false
        realNameEdit.isEnabled = false
        emailEdit.isEnabled = false
        editButton.visibility = View.VISIBLE
        updateButton.visibility = View.GONE
        deleteImage.visibility = View.GONE
    }

    private fun showAlertDialogToLogOut(){
        val mContext = context
        mContext?.let {
            val title = mContext.getString(R.string.header_attention)
            val message = mContext.getString(R.string.message_logout)
            ActivityUtil().openAlertDialog(mContext, title, message, true) {logoutUser()}
        }
    }


    override fun onRequestError(errorMsg: String) {
        closeProgress()
        showAlertDialogOnError(errorMsg)
    }

    private fun showAlertDialogOnError(errorMsg: String){
        val mContext = context
        mContext?.let {
            val title = mContext.getString(R.string.header_error)
            ActivityUtil().openAlertDialog(mContext, title, errorMsg, false) {}
        }
    }

    private fun logoutUser(){
        val mContext = context
        mContext?.let {
            ActivityUtil().deleteUserIDFromSharedPref(mContext)
        }
        activity?.finish()
    }

    private fun openProgress() {
        loaderView.visibility = View.VISIBLE
    }

    private fun closeProgress(){
        loaderView.visibility = View.GONE
    }


    companion object{
        const val TAG = "ProfileFragment"
    }
}
