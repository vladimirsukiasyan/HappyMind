package company.sukiasyan.happymind.views.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.google.android.gms.tasks.Task
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.models.Parent
import company.sukiasyan.happymind.utils.*
import kotlinx.android.synthetic.main.fragment_register_email.*
import kotlinx.android.synthetic.main.fragment_register_namepass.*

class RegisterActivity : AppCompatActivity(), EmailFragment.Listener, NamePassFragment.Listener {


    private lateinit var mEmail: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.frame_layout, EmailFragment())
                    .commit()

        }

    }

    override fun onNext(email: String) {
        mEmail = email
        getAuth().fetchSignInMethodsForEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result.signInMethods?.isEmpty() == true) {
                    supportFragmentManager.beginTransaction().replace(R.id.frame_layout, NamePassFragment())
                            .addToBackStack(null)
                            .commit()
                } else {
                    showToast("This email is already created")
                }
            }
        }
    }

    override fun onRegister(name: String, lastName: String, patronymic: String, pass: String, phone: String, phone_other_parent: String, adress: String, branch: String) {
        getAuth().createUserWithEmailAndPassword(mEmail, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                val parent = Parent(name, lastName, patronymic, phone, phone_other_parent, adress, branch)
                getDatabase().collection("parents")
                        .document(it.result.user.uid)
                        .set(parent)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d(TAG, "onRegister: user has been created")
                                getAuth().signInWithEmailAndPassword(mEmail, pass)
                                        .addOnSuccessListener {
                                            Log.d(TAG, "onRegister: signIn")
                                            saveUserBranch(parent.branch)
                                            startActivity(Intent(this, AddChildActivity::class.java))
                                            finish()
                                        }
                            } else {
                                sentErrorToast(it)
                            }
                        }
                getDatabase().collection("accounts").document(it.result.user.uid)
                        .set(mapOf("role" to "parent", "branch" to branch))
            } else {
                sentErrorToast(it)
            }
        }
    }

    private fun sentErrorToast(task: Task<out Any>) {
        Log.d(TAG, "onRegister: failure to create user", task.exception)
        showToast("Something wrong happened")
    }

}

class EmailFragment : Fragment() {

    private lateinit var mListener: Listener

    interface Listener {
        fun onNext(email: String)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        next_btn.setOnClickListener {
            val error = validateEditView(email_input)
            if (!error) {
                mListener.onNext(email_input.text.toString())
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mListener = context as Listener
    }
}

class NamePassFragment : Fragment() {
    private val TAG = "NamePassFragment"

    private lateinit var mListener: Listener

    interface Listener {
        fun onRegister(name: String, lastName: String, patronymic: String, pass: String, phone: String, phone_other_parent: String, adress: String, branch: String)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register_namepass, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        register_btn.setOnClickListener {
            Log.d(TAG, "button ic clicked: ")

            val errorEdit = validateEditView(name_input, last_name_input, password_input, phone_input, adress_input)
            val errorRadioGroup = this@NamePassFragment.context!!.validateRadioGgroup(branch_input)
            val error = errorEdit && errorRadioGroup

            if (!error) {
                val name = name_input.text.toString()
                val lastName = last_name_input.text.toString()
                val patronymic = patronymic_input.text.toString()
                val pass = password_input.text.toString()
                val phone = phone_input.text.toString()
                val phone_other_parent = phone_other_input.text.toString()
                val adress = adress_input.text.toString()
                val branch = branch_input.findViewById<RadioButton>(branch_input.checkedRadioButtonId).text.toString()
                mListener.onRegister(name, lastName, patronymic, pass, phone, phone_other_parent, adress, branch)
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mListener = context as Listener
    }
}
