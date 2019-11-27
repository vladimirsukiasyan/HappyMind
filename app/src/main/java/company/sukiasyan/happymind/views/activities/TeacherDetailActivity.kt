package company.sukiasyan.happymind.views.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.StringSignature
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.storage.UploadTask
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.models.Teacher
import company.sukiasyan.happymind.utils.*
import kotlinx.android.synthetic.main.activity_teacher_detail.*
import kotlinx.android.synthetic.main.content_teacher_detail.*
import java.io.ByteArrayOutputStream

class TeacherDetailActivity : AppCompatActivity() {
    private var mTeacher = Teacher()
    private var mPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_detail)

        setSupportActionBar(toolbar_collapse)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fab.setOnClickListener { saveTeacher() }

        initProperty()
        authSpecialUI()
    }

    private fun authSpecialUI() {
        when (getUserRole()) {
            "admin" -> {
                if (!isAdding()) {
                    fillEditViews()
                    downloadTeacherPhoto()
                    email_input.editText?.isEnabled = false
                    password_input.editText?.isEnabled = false
                }
                item_image.setOnClickListener {
                    val photoPicker = Intent(Intent.ACTION_GET_CONTENT)
                    photoPicker.type = "image/*"
                    startActivityForResult(photoPicker, GALLERY_PICKER_CODE)
                }
            }
            "parent" -> {
                password_input.visibility = View.GONE
                phone_input.visibility=View.GONE
                experience_input.visibility=View.GONE
                email_input.visibility=View.GONE
                fab.visibility = View.GONE
                fillEditViews()
                disableEditViews()
                downloadTeacherPhoto()
            }
        }
    }


    private fun downloadTeacherPhoto() {
        val reference = getStorage().reference.child("users/teachers/${mTeacher.uid}.jpg")
        Glide.with(this)
                .using(FirebaseImageLoader())
                .load(reference)
                .asBitmap()
                .centerCrop()
                .signature(StringSignature(mTeacher.photo_uri))
                .error(R.drawable.profile_placeholder)
                .placeholder(R.drawable.profile_placeholder)
                .into(item_image)

    }

    private fun initProperty() {
        mTeacher = intent.getParcelableExtra(EXTRA_TEACHER) ?: Teacher()
        mPosition = intent.getIntExtra(ARG_ITEM_POSITION, -1)
    }

    private fun disableEditViews() {
        val editList = listOf<TextInputLayout>(email_input, password_input, name_input, last_name_input, patronymic_input,
                date_of_birthday_input, education_input, experience_input, phone_input)
        editList.forEach {
            it.editText?.isEnabled = false
        }
    }

    private fun fillEditViews() {
        with(mTeacher) {
            email_input.editText?.setText(email)
            password_input.editText?.setText(password)
            name_input.editText?.setText(name)
            last_name_input.editText?.setText(last_name)
            patronymic_input.editText?.setText(patronymic)
            date_of_birthday_input.editText?.setText(dateFormatter.format(date_of_birthday))
            education_input.editText?.setText(education)
            experience_input.editText?.setText(experience.toString())
            phone_input.editText?.setText(phone_number)
        }
    }

    private fun isAdding() = mPosition == -1

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                android.R.id.home -> {
                    finish()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GALLERY_PICKER_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "onActivityResult: OnActivityResult is ok")
                    val bitmap: Bitmap
                    val selectedImage = data?.data
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
                    item_image.setImageBitmap(bitmap)
                }
            }
        }
    }

    private fun saveTeacher() {
        val error1 = validateTextInputLayout(email_input, password_input, name_input, last_name_input, patronymic_input,
                date_of_birthday_input, education_input, experience_input, phone_input)
        val error2 = validateDateOfBirthday(date_of_birthday_input.editText?.text.toString())
        if(error2){
            date_of_birthday_input.isErrorEnabled=true
            date_of_birthday_input.error="Неверный формат даты!"
        }
        if (!error1 && !error2) {
            if (isAdding()) {
                getAuth().createUserWithEmailAndPassword(email_input.editText!!.text.toString(), password_input.editText!!.text.toString())
                        .addOnSuccessListener {
                            Log.d(TAG, "TeacherDetailActivity: saveTeacher() teacher's account has been successfully created")
                            uploadTeacher(getPojoTeacher(it.user.uid))
                            finish()
                        }
                        .addOnFailureListener { exception ->
                            when (exception) {
                                is FirebaseNetworkException -> {
                                    email_input.error = "Отсутствует интеренет-соединение!"
                                    password_input.error = "Отсутствует интеренет-соединение!"
                                }
                                is FirebaseAuthInvalidCredentialsException -> {
                                    when (exception.errorCode) {
                                        "ERROR_INVALID_EMAIL" -> {
                                            email_input.error = "Введён неправильный формат email!"
                                            email_input.requestFocus()
                                        }
                                        "ERROR_WEAK_PASSWORD" -> {
                                            password_input.error = "Пароль должен содержать не менее 6 символов!"
                                            password_input.requestFocus()
                                        }
                                    }
                                }
                                is FirebaseAuthUserCollisionException -> {
                                    email_input.error = "Пользователь с такой почтой уже существует!"
                                    email_input.requestFocus()
                                }
                            }
                        }
            } else {
                uploadTeacher(getPojoTeacher())
                finish()
            }
        }
    }

    private fun uploadTeacher(teacher: Teacher) {
        val bitmap = (item_image.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        val data = baos.toByteArray()

        //First we upload photo, then we get a url of photo and upload the teacher
        val reference = getStorage().reference.child("users").child("teachers").child("${teacher.uid}.jpg")
        reference.putBytes(data)
                .continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                    if (task.isSuccessful) {
                        reference.downloadUrl
                    } else {
                        throw task.exception ?: Exception()
                    }
                }
                .addOnSuccessListener {
                    teacher.photo_uri = it.toString()
                    getDatabase().collection("teachers").document(teacher.uid)
                            .set(teacher)
                            .addOnSuccessListener {
                                Log.d(TAG, "TeacherDetailActivity: saveTeacher() teacher's doc has been uploaded. Teacher=$teacher")
                            }
                }
    }

    private fun getPojoTeacher(uid: String = mTeacher.uid) = Teacher(uid,
            email_input.editText!!.text.toString(),
            password_input.editText!!.text.toString(),
            name_input.editText!!.text.toString(),
            last_name_input.editText!!.text.toString(),
            patronymic_input.editText!!.text.toString(),
            dateFormatter.parse(date_of_birthday_input.editText?.text.toString()),
            education_input.editText!!.text.toString(),
            experience_input.editText!!.text.toString().toInt(),
            phone_input.editText!!.text.toString()
    )

    //TODO СДЕЛАТЬ ПРОВЕРКУ ЧЕРЕЗ SIMPLEDATAFORMAT. REFACTORING!!!!!!



//            if (date_of_birthday_input.editText!!.text.isNotEmpty()) {
//                val dateList = date_of_birthday_input.editText?.text.toString().split(".")
//                if (dateList.size == 3) {
//                    val (day, month, year) = date_of_birthday_input.editText?.text.toString().split(".")
//                    val error = !((day.isNotEmpty() && day in "0".."31") && (month.isNotEmpty() && month in "0".."12") && (year.length == 4))
//                    if (error) {
//                        date_of_birthday_input.isErrorEnabled = true
//                        date_of_birthday_input.error = "Неверный формат"
//                    }
//                    error
//                } else {
//                    date_of_birthday_input.isErrorEnabled = true
//                    date_of_birthday_input.error = "Неверный формат"
//                    true
//                }
//            } else true
}