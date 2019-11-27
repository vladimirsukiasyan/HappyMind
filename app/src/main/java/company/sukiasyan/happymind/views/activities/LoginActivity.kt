package company.sukiasyan.happymind.views.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.utils.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var progressDialog:ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (getAuth().currentUser != null) {
            startActivity(Intent(this, ScheduleActivity::class.java))
        }
        login_btn.setOnClickListener(this)
        create_account_text.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.login_btn -> {
                Log.d(TAG, "onClick: SignIn")
                val error=validateEditView(email_input,password_input)

                if (!error) {
                    progressDialog = ProgressDialog(this).apply {
                        setProgressStyle(ProgressDialog.STYLE_SPINNER)
                        setMessage("Пожалуйста, подождите...")
                        setCancelable(false)
                    }
                    getAuth().signInWithEmailAndPassword(email_input.text.toString(), password_input.text.toString())
                            .addOnSuccessListener {
                                authorization(it.user.uid)
                            }
                            .addOnFailureListener { exception ->
                                progressDialog.dismiss()
                                when (exception) {
                                    is FirebaseNetworkException -> {
                                        email_input.error="Отсутствует интеренет-соединение."
                                        password_input.error="Отсутствует интеренет-соединение."
                                    }
                                    is FirebaseAuthUserCollisionException -> {
                                        email_input.error="Ваш аккаунт уже используется на другом устройстве!"
                                    }
                                    is FirebaseAuthInvalidCredentialsException -> {
                                        when (exception.errorCode) {
                                            "ERROR_INVALID_EMAIL" -> {
                                                email_input.error="Введён неправильный формат email!"
                                                email_input.requestFocus()
                                            }
                                            "ERROR_WRONG_PASSWORD" -> {
                                                password_input.error="Введен неправильный пароль!"
                                                password_input.requestFocus()
                                            }
                                        }
                                    }
                                    is FirebaseAuthInvalidUserException -> {
                                        email_input.error="Пользователя с такой почтой не существует!"
                                        email_input.requestFocus()
                                    }
                                }
                            }
                }
            }
            R.id.create_account_text -> {
                Log.d(TAG, "onClick: SignUp")
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        }
    }

    private fun authorization(uid: String) {
        getDatabase().collection("accounts").document(uid)
                .get()
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    val role = it.getString("role")!!
                    val branch = it.getString("branch")
                    saveUserRole(role)
                    //Teacher doesn't have a branch( equal null)
                    branch?.let {
                        saveUserBranch(it)
                    }
                    startActivity(Intent(this, ScheduleActivity::class.java))
                }
    }
}
