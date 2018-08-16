package company.sukiasyan.happymind.views.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.models.Course
import company.sukiasyan.happymind.models.Teacher
import company.sukiasyan.happymind.utils.*
import kotlinx.android.synthetic.main.content_set_course_first.*
import kotlinx.android.synthetic.main.toolbar.*
import java.io.ByteArrayOutputStream


class SetCourseActivityFirst : SetCourseBaseActivity() {
    private var teachers: List<Teacher> = listOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_course_first)
        Log.d(TAG, "SetCourseActivityFirst: onCreate()")

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        downloadTeacherList()
        if (intent?.extras?.containsKey(EXTRA_COURSE) == true) {
            //edit course
            course = intent.getParcelableExtra(EXTRA_COURSE)
            oldCourse = course.copy()
            toolbar.title = course.name
        } else {
            //add new course
            course = Course()
            oldCourse = course.copy()
            toolbar.title = "Добавление курса"
        }
        mPosition = intent!!.getIntExtra(ARG_ITEM_POSITION, -1)

        if (savedInstanceState == null && !isAdding()) {
            downloadCoursePhoto()
            course_name.setText(course.name)
            course_description.setText(course.description)
        }


        next_btn.setOnClickListener {
            //if teachers list is empty (download is not completed) => ignore user
            if (teachers.isNotEmpty()) {
                val error = validateEditView(course_name, course_description)
                if (!error) {
                    setAllPropertyInCourse()

                    val intent = Intent(this, SetCourseActivitySecond::class.java)
                    intent.putExtra(EXTRA_BUNDLE, bundle)
                    intent.putExtra(EXTRA_COURSE, course)
                    intent.putExtra(ARG_ITEM_POSITION, mPosition)
                    startActivityForResult(intent, REQUEST_SECOND_ACTIVITY)
                }
            }
        }
        course_image.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_GET_CONTENT)
            photoPicker.type = "image/*"
            startActivityForResult(photoPicker, GALLERY_PICKER_CODE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (!isAdding()) {
            //adding course
            menuInflater.inflate(R.menu.menu_set_course_first, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            //updating course
            val error = validateEditView(course_name, course_description)
            if (!error) {
                setAllPropertyInCourse()

                uploadCourse()
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setAllPropertyInCourse() {
        course.name = course_name.text.toString()
        course.description = course_description.text.toString()
        course.teacher_uid = teachers[course_teacher_spinner.selectedItemPosition].uid
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
                    course_image.setImageBitmap(bitmap)
                    bundle = getBundleForTransmit()
                }
            }
            REQUEST_SECOND_ACTIVITY -> {
                if (resultCode == Activity.RESULT_OK) {
                    course = data!!.getParcelableExtra(EXTRA_COURSE)
                }
            }
        }
    }

    private fun getBundleForTransmit() = Bundle().apply {
        putByteArray(EXTRA_PHOTO, compressPhoto())
    }

    private fun compressPhoto(): ByteArray {
        val bitmap = (course_image.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        return baos.toByteArray()
    }

    private fun downloadCoursePhoto() {
        val reference = getStorage().reference.child("courses/${getUserBranch()}/${course.id}/${course.id}.jpg")
        Glide.with(this)
                .using(FirebaseImageLoader())
                .load(reference)
                .asBitmap()
                .placeholder(R.drawable.item_placeholder)
                .error(R.drawable.item_placeholder)
                .into(course_image)
        bundle = getBundleForTransmit()
    }

    private fun downloadTeacherList() {
//        val progressDialog = ProgressDialog(this).apply {
//            setProgressStyle(ProgressDialog.STYLE_SPINNER)
//            setMessage("Загрузка данных. Пожалуйста, подождите...")
//            setCancelable(false)
//        }
//        progressDialog.show()


        getDatabase().collection("teachers")
                .get()
                .addOnSuccessListener {
                    teachers = it.toObjects(Teacher::class.java)
                    val teacherName = teachers.map { it.name + " " + it.last_name }
                    course_teacher_spinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, teacherName).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                    if (!isAdding()) {
                        val teacher = teachers.find { it.uid == course.teacher_uid }
                        teacher?.let {
                            course_teacher_spinner.setSelection(teachers.indexOf(it))
                        }
                    }
//                    progressDialog.dismiss()
                }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        Log.d(TAG, "SetCourseActivityFirst: onSaveInstanceState()")
        outState?.putByteArray(EXTRA_PHOTO, compressPhoto())
        outState?.putInt(ARG_ITEM_POSITION, course_teacher_spinner.selectedItemPosition)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        //TODO не возвращает положение spinner( проверить, не сохраняет ли он его состояние, и переприсваивает ли он его после вызова super())
        Log.d(TAG, "SetCourseActivityFirst: onRestoreInstanceState()")
        savedInstanceState?.getByteArray(EXTRA_PHOTO)?.let {
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            course_image.setImageBitmap(bmp)
        }
        savedInstanceState?.getInt(ARG_ITEM_POSITION)?.let {
            course_teacher_spinner.setSelection(it)
        }

        super.onRestoreInstanceState(savedInstanceState)
    }

    companion object {
        const val GALLERY_PICKER_CODE = 1
        const val REQUEST_SECOND_ACTIVITY = 100
    }
}
