package com.softwareproject.travelguideapp.pages.auth.forgetPass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.softwareproject.travelguideapp.R
import com.softwareproject.travelguideapp.adapter.CustomSpinnerAdapter
import com.softwareproject.travelguideapp.pages.auth.signin.SigninFragment
import com.softwareproject.travelguideapp.util.ActivityUtil
import com.softwareproject.travelguideapp.util.DataSingleton
import com.softwareproject.travelguideapp.util.Validation
import com.tuyenmonkey.mkloader.MKLoader

class ForgetPassFragment : Fragment(), ForgetPassContract.View {
    private var presenter: ForgetPassContract.Presenter? = null

    private lateinit var userEdit: EditText
    private lateinit var passwordEdit: EditText
    private lateinit var secretQuestEdit: EditText
    private lateinit var newPassWord: EditText

    private lateinit var submitButton: Button
    private lateinit var loaderView: MKLoader

    private var secretAnswer:String? = null
    private var secretQuest: String? = null

    companion object{
        const val TAG = "ForgetPassFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        setAttributes()
    }

    private fun init(view: View){
        userEdit = view.findViewById(R.id.frag_forgotPass_et_username)
        newPassWord = view.findViewById(R.id.frag_forgotPass_et_repeatPass)
        passwordEdit = view.findViewById(R.id.frag_forgotPass_et_password)
        secretQuestEdit = view.findViewById(R.id.frag_forgotPass_et_question)
        submitButton = view.findViewById(R.id.frag_forgotPass_btn_update)
        loaderView = view.findViewById(R.id.frag_forget_loader)
        presenter  = ForgetPassPresenter(this)
    }

    private fun setAttributes(){
        submitButton.setOnClickListener {
            if(newPassWord.text.length > 5){
                changePasswordClicked()
            }
            else {
                Toast.makeText(context, "Şifre 6 karakterden küçük olamaz!", Toast.LENGTH_LONG).show()
            }
        }
        secretQuestEdit.setOnClickListener {
            showSecretQuestionDialog()
        }
    }


    private fun showSecretQuestionDialog(){
        context?.let {
            val view =
                LayoutInflater.from(it).inflate(R.layout.content_secret_question_dialog, null, false)
            val builder = AlertDialog.Builder(it).setView(view)
            val dialog: AlertDialog = builder.create()

            val questionSpinner = view.findViewById<Spinner>(R.id.con_questionDialog_sp_question)
            questionSpinner.adapter = CustomSpinnerAdapter(it,"Gizlilik Sorusunu Seçiniz", DataSingleton.createInstance().getQuestionList())

            val answerEditText = view.findViewById<EditText>(R.id.con_questionDialog_et_answer)

            val confirmButton: Button = view.findViewById(R.id.con_questionDialog_btn_confirm)
            confirmButton.setOnClickListener {
                dialog.dismiss()
                secretQuestEdit.setText(questionSpinner.selectedItem.toString())
                secretAnswer = answerEditText.text.toString()
                secretQuest = DataSingleton.createInstance().getBackendQuestionItem(questionSpinner.selectedItemPosition)
            }
            val cancelButton : Button = view.findViewById(R.id.con_questionDialog_btn_cancel)
            cancelButton.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun setPresenter(presenter: ForgetPassContract.Presenter) {
        this.presenter = presenter
    }

    private fun changePasswordClicked(){
        val userName = userEdit.text.toString()
        val password  = newPassWord.text.toString()
        val passwordRepeat = passwordEdit.text.toString()
        // Passwords matched
        if (password == passwordRepeat){
            // Secret question selected and its answer written
            if (secretQuest != null && secretAnswer != null){
                openLoader()
                presenter?.changePassword(userName, password, secretAnswer!!)
            }
            else{ // Secret question to its answer is null
                val mContext = context
                mContext?.let {
                    val title = mContext.getString(R.string.header_error)
                    val message = mContext.getString(R.string.message_secret)
                    ActivityUtil().openAlertDialog(mContext, title , message, false){}
                }
            }
        }
        // Passwords did not matched
        else{
            val mContext = context
            mContext?.let {
                val title = mContext.getString(R.string.header_error)
                val message = mContext.getString(R.string.message_not_matched_pass)
                ActivityUtil().openAlertDialog(mContext, title , message, false){}
            }
        }
    }

    private fun openLoader(){
        loaderView.visibility = View.VISIBLE
    }
    private fun closeLoader(){
        loaderView.visibility = View.GONE
    }

    override fun onRequestError(errorMsg: String) {
        closeLoader()
        val mContext = context
        mContext?.let {
            val title = mContext.getString(R.string.header_error)
            ActivityUtil().openAlertDialog(mContext, title , errorMsg, false){}
        }
    }

    override fun onPasswordChanged() {
        closeLoader()
        val mContext = context
        mContext?.let {
            val title = mContext.getString(R.string.header_attention)
            val message = mContext.getString(R.string.message_change_pass_info)
            ActivityUtil().openAlertDialog(mContext, title , message, false){openLoginFragment()}
        }
    }

    private fun openLoginFragment(){
        val mSupportFragmentManager = activity?.supportFragmentManager
        mSupportFragmentManager?.let {
            ActivityUtil().changeFragmentWithStack(SigninFragment(), SigninFragment.TAG, mSupportFragmentManager,R.id.act_auth_fl_container)
        }

    }
}