package company.sukiasyan.happymind.utils

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import company.sukiasyan.happymind.views.activities.BasicActivity
import company.sukiasyan.happymind.views.activities.BasicActivity.Companion.activeChild
import company.sukiasyan.happymind.views.activities.CoursesDetailActivity
import company.sukiasyan.happymind.views.activities.CoursesDetailActivity.Companion.childCourse
import company.sukiasyan.happymind.views.activities.CoursesDetailActivity.Companion.course
import java.util.*

const val TAG = "Activities"
const val EXTRA_COURSE = "course"
const val EXTRA_BUNDLE = "bundle"
const val EXTRA_PHOTO = "photo"
const val ARG_ITEM_POSITION = "position"
const val EXTRA_AGE_GROUP = "age_group"

//get static Firebase tools
fun getAuth() = FirebaseAuth.getInstance()

fun getStorage() = FirebaseStorage.getInstance()
fun getDatabase() = FirebaseFirestore.getInstance()

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, duration).show()
}


fun validateEditView(vararg fieldsEdit: EditText): Boolean {
    var error = false
    fieldsEdit.forEach {
        if (it.text.toString().isEmpty()) {
            it.error = "Обязательное поле"
            error = true
        }
    }
    return error
}

fun Context.validateRadioGgroup(vararg fieldsRadioGroup: RadioGroup): Boolean {
    var error = false
    fieldsRadioGroup.forEach {
        if (it.checkedRadioButtonId == -1) {
            showToast("Выберите пол, пожалуйста", Toast.LENGTH_LONG)
            error = true
        }
    }
    return error
}

fun calcYears(birthDay: GregorianCalendar, checkDay: GregorianCalendar): Int {
    var years = checkDay.get(GregorianCalendar.YEAR) - birthDay.get(GregorianCalendar.YEAR)
    // корректируем, если текущий месяц в дате проверки меньше месяца даты рождения
    val checkMonth = checkDay.get(GregorianCalendar.MONTH)
    val birthMonth = birthDay.get(GregorianCalendar.MONTH)
    if (checkMonth < birthMonth) {
        years--
    } else if (checkMonth == birthMonth && checkDay.get(GregorianCalendar.DAY_OF_MONTH) < birthDay.get(GregorianCalendar.DAY_OF_MONTH)) {
        // отдельный случай - в случае равенства месяцев,
        // но меньшего дня месяца в дате проверки - корректируем
        years--
    }
    return years
}

fun getCourseAgeGroup() =
        CoursesDetailActivity.course.ageGroups.find {
            val age = BasicActivity.activeChild.getAge()
            it.minAge <= age && it.maxAge >= age
        }!!

fun Context.updateChildCourse(index: Int) {
    //replace or add item
    if (index == -1) { //in case adding course
        activeChild.courses.add(childCourse)
    } else { //in case replacing course
        activeChild.courses[index] = childCourse
    }

    getDatabase().collection("parents").document(getAuth().uid!!)
            .collection("children").document(activeChild.name)
            .set(activeChild)
            .addOnSuccessListener {
                Log.d(TAG, "updateChildCourse: данные о курсе ${course.name} у ${activeChild.name} успешно обновлены")
            }
    getDatabase().collection("filials").document(getUserBranch())
            .collection("courses").document(course.id)
            .set(course)
}

fun getChildReference() = "parents/${getAuth().uid}/children/${activeChild.name}"

fun Context.getColorById(id: Int) = ContextCompat.getColor(this, id)