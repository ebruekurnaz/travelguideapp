package com.softwareproject.travelguideapp.pages.auth.signup


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.softwareproject.travelguideapp.R
import com.softwareproject.travelguideapp.adapter.CustomSpinnerAdapter
import com.softwareproject.travelguideapp.pages.home.HomeActivity
import com.softwareproject.travelguideapp.util.ActivityUtil
import com.softwareproject.travelguideapp.util.DataSingleton
import com.softwareproject.travelguideapp.util.Validation
import com.tuyenmonkey.mkloader.MKLoader

class SignupFragment : Fragment(), SignupContract.View {
    private var presenter: SignupContract.Presenter? = null
    private lateinit var emailEdit : EditText
    private lateinit var userEdit: EditText
    private lateinit var passwordEdit: EditText
    private lateinit var secretQuestEdit: EditText

    private lateinit var submitButton: Button
    private lateinit var loaderView: MKLoader
    private var secretAnswer:String? = null
    private var secretQuest: String? = null

    companion object{
        const val TAG = "SignupFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = SignupPresenter(this)
        init(view)
        setAttributes()
    }

    private fun init(view: View){
        emailEdit = view.findViewById(R.id.signup_email_edit)
        userEdit = view.findViewById(R.id.signup_username_edit)
        passwordEdit = view.findViewById(R.id.signup_password_edit)
        secretQuestEdit = view.findViewById(R.id.signup_secretQuestion_edit)
        submitButton = view.findViewById(R.id.signup_submit_button)
        loaderView = view.findViewById(R.id.frag_signup_loader)
    }

    private fun setAttributes(){
        submitButton.setOnClickListener {
            if(Validation.validate(emailEdit.text.toString(), passwordEdit.text.toString(),context!!))
                recordUser()
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

    private fun recordUser(){
        // Check if user selected the secret question and write its answer
        if(secretQuest != null && secretAnswer != null){
            openProgress()
            presenter?.saveUser(emailEdit.text.toString(),
                userEdit.text.toString(),
                passwordEdit.text.toString(),
                secretQuest!!,
                secretAnswer!!)
        } // If not show error dialog
        else{
            val mContext = context
            mContext?.let {
                val title = mContext.getString(R.string.header_error)
                val message = mContext.getString(R.string.secret_question)
                ActivityUtil().openAlertDialog(mContext, title, message ,false) {}
            }
        }
    }

    override fun setPresenter(presenter: SignupContract.Presenter) {
        this.presenter = presenter

    }

    override fun onUserSaved(userID: Int) {
        closeProgress()
        val mContext = context
        mContext?.let {
            ActivityUtil().writeUserIDToSharedPref(mContext, userID)
            val intent = Intent(mContext, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onUserSaveFailed(errorMsg: String) {
        closeProgress()
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
