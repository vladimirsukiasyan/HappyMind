package company.sukiasyan.happymind.views.activities

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
                val email = email_input.text.toString()
                val password = password_input.text.toString()

                if (validate(email, password)) {
                    getAuth().signInWithEmailAndPassword(email_input.text.toString(), password_input.text.toString())
                            .addOnSuccessListener {
                                authorization(it.user.uid)
                            }
                            .addOnFailureListener { exception ->
                                when (exception) {
                                    is FirebaseNetworkException -> {
                                        showToast("Отсутствует интеренет соединение.")
                                    }
                                    is FirebaseAuthUserCollisionException -> {
                                        showToast("Ваш аккаунт уже используется на другом устройстве!")
                                    }
                                    is FirebaseAuthInvalidCredentialsException -> {
                                        when (exception.errorCode) {
                                            "ERROR_INVALID_EMAIL" -> showToast("Введён неправильный формат email!")
                                            "ERROR_WRONG_PASSWORD" -> showToast("Введен неправильный пароль!")
                                        }
                                    }
                                    is FirebaseAuthInvalidUserException -> {
                                        showToast("Пользователя с такой почтой не существует!")
                                    }
                                }
                            }
                } else {
                    Log.d(TAG, "onClick: SignIn Validate Failure")
                    showToast("Пожалуйста, заполните все поля")
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

    private fun validate(email: String, password: String) =
            email.isNotEmpty() && password.isNotEmpty()
}
