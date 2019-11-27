package company.sukiasyan.happymind.views.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.models.Child
import company.sukiasyan.happymind.utils.*
import kotlinx.android.synthetic.main.activity_add_child.*
import java.io.ByteArrayOutputStream


class AddChildActivity : AppCompatActivity() {

    private val GALLERY_PICKER_CODE = 1
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_child)

        child_image.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_GET_CONTENT)
            photoPicker.type = "image/*"
            startActivityForResult(photoPicker, GALLERY_PICKER_CODE)
        }

        register_btn.setOnClickListener {

            val errorEdit = validateEditView(name_input, last_name_input, patronymic_input, date_of_birthday_input)
            val errorRadioGroup = validateRadioGgroup(sex_input)

            val errorDateValidate = validateDateOfBirthday(date_of_birthday_input.text.toString())
            if(errorDateValidate){
                date_of_birthday_input.error="Неверный формат даты!"
            }
            if (errorRadioGroup) {
                showToast("Выберите пол, пожалуйста", Toast.LENGTH_LONG)
            }
            val error = errorEdit || errorRadioGroup || errorDateValidate

            if (!error) {
                uploadPhotoWithChild()
            }
        }
    }

    private fun uploadPhotoWithChild() {
        val bitmap = (child_image.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        progressDialog = ProgressDialog(this).apply {
            setProgressStyle(ProgressDialog.STYLE_SPINNER)
            setMessage("Пожалуйста, подождите...")
            setCancelable(false)
        }
        progressDialog.show()

        val reference = getStorage().reference.child("users/parents/${getAuth().uid}/${name_input.text}/${name_input.text}")
        reference.putBytes(data)
                .addOnSuccessListener {
                    Log.d(TAG, "downloadPhoto: successful!")
                    val child = Child(
//                            id = generateUniqueId(),
                            name = name_input.text.toString(),
                            last_name = last_name_input.text.toString(),
                            patronymic = patronymic_input.text.toString(),
                            date_of_birthday = dateFormatter.parse(date_of_birthday_input.text.toString()),
                            sex = findViewById<RadioButton>(sex_input.checkedRadioButtonId).text.toString(),
                            phone = phone_input.text.toString()
                    )
                    uploadChild(child)
                }
                .addOnFailureListener {
                    progressDialog.dismiss()
                    showToast("Не удалось добавить вашего ребенка! Проверьте интернет-соединение и попробуйте еще раз.")
                    Log.d(TAG, "downloadPhoto: something wrong happened! , ${it.message} ")
                }
    }

//    private fun generateUniqueId() = BasicActivity.children.size.toLong()

    private fun uploadChild(child: Child) {
        getDatabase().collection("parents").document(getAuth().uid!!)
                .collection("children").document(child.name)
                .set(child)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    showToast("Ребенок добавлен :)")
                    saveActiveChildId(child.id)
//                    BasicActivity.activeChild = child

//                    BasicActivity.children.clear()
                    startActivity(Intent(this, CoursesActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    progressDialog.dismiss()
                    showToast("Не удалось добавить вашего ребенка! Проверьте интернет-соединение и попробуйте еще раз.")
                    Log.d(TAG, "downloadPhoto: something wrong happened! , $it ")
                }
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
                    child_image.setImageBitmap(bitmap)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (isFirstChild()) {
            showToast("Добавьте ребенка. Это обязательно!")
        } else super.onBackPressed()
    }
}
