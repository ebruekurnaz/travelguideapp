package com.softwareproject.travelguideapp.pages.auth.signin


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.EditText
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import com.softwareproject.travelguideapp.R
import com.softwareproject.travelguideapp.adapter.CustomSpinnerAdapter
import com.softwareproject.travelguideapp.pages.auth.forgetPass.ForgetPassFragment
import com.softwareproject.travelguideapp.pages.auth.signup.SignupFragment
import com.softwareproject.travelguideapp.pages.home.HomeActivity
import com.softwareproject.travelguideapp.util.ActivityUtil
import com.softwareproject.travelguideapp.util.DataSingleton
import com.tuyenmonkey.mkloader.MKLoader

class SigninFragment : Fragment(), SigninContract.View {
    private var presenter: SigninContract.Presenter? = null
    private lateinit var signupText: TextView
    private lateinit var forgetPassText: TextView

    private lateinit var usrNameEdit: EditText
    private lateinit var passwordEdit: EditText

    private lateinit var loginButton: Button
    private lateinit var loaderView: MKLoader


    companion object{
        const val TAG = "SigninFragment"
    }

    override fun setPresenter(presenter: SigninContract.Presenter) {
        this.presenter = presenter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(view : View){
        signupText = view.findViewById(R.id.frag_signin_tv_signup)
        forgetPassText = view.findViewById(R.id.frag_signin_tv_forgetPass)

        usrNameEdit = view.findViewById(R.id.frag_signin_et_username)
        passwordEdit = view.findViewById(R.id.frag_signin_et_password)

        loginButton = view.findViewById(R.id.frag_signin_bt_login)

        loaderView = view.findViewById(R.id.frag_signin_loader)
        setViews()
        checkLoggedInUser()
    }

    private fun setViews(){
        signupText.setOnClickListener {
            fragmentManager?.let {
                ActivityUtil().changeFragmentWithStack(SignupFragment(), SignupFragment.TAG, it,R.id.act_auth_fl_container)
            }
        }
        forgetPassText.setOnClickListener {
            fragmentManager?.let {
                ActivityUtil().changeFragmentWithStack(ForgetPassFragment(), ForgetPassFragment.TAG, it,R.id.act_auth_fl_container)
            }
        }
        loginButton.setOnClickListener{
            openProgress()
            presenter?.checkAuth(usrNameEdit.text.toString(), passwordEdit.text.toString())
        }
    }

    override fun onUserLoginSuccess(userID: Int) {
        closeProgress()
        val mContext = context
        mContext?.let {
            ActivityUtil().writeUserIDToSharedPref(mContext, userID)
            openHomeActivity()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_signin, container, false)
        presenter = SigninPresenter(this)
        return rootView
    }

    private fun checkLoggedInUser() {
        closeProgress()
        val mContext = context
        mContext?.let {
            val userID = ActivityUtil().getUserIDFromSharedPref(mContext)
            if(userID != -1){
                openHomeActivity()
            }
        }
    }

    private fun openHomeActivity(){
        val intent = Intent(context, HomeActivity::class.java)
        startActivity(intent)
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

    private fun openProgress() {
        loaderView.visibility = View.VISIBLE
    }

    private fun closeProgress(){
        loaderView.visibility = View.GONE
    }


}
