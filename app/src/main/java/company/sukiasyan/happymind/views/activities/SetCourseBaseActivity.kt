package company.sukiasyan.happymind.views.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import company.sukiasyan.happymind.models.Child
import company.sukiasyan.happymind.models.Course
import company.sukiasyan.happymind.utils.*

abstract class SetCourseBaseActivity : AppCompatActivity() {
    protected var mPosition = -1
    protected lateinit var course: Course
    protected lateinit var oldCourse: Course

    protected lateinit var bundle: Bundle


    protected fun uploadCourse() {
        val collectionReference = getDatabase().collection("filials").document(getUserBranch()).collection("courses")
        if (isAdding()) {
            collectionReference
                    .add(course)
                    .addOnSuccessListener {
                        collectionReference.document(it.id)
                                .update("id", it.id)
                        Log.d(TAG, "SetCourseActivitySecond: uploadCourse() course has been added")
                        updateCoursePhoto(it.id)
                    }
        } else {
            collectionReference
                    .document(course.id)
                    .set(course)
                    .addOnSuccessListener {
                        Log.d(TAG, "SetCourseActivitySecond: uploadCourse() course has been updated")
                        updateCoursePhoto()
                        applyChangesForChildren()
                    }

        }
    }

    private fun applyChangesForChildren() {
        fun updateChildren(child: Child, reference: String) {
            getDatabase().document(reference)
                    .set(child)
                    .addOnSuccessListener {
                        Log.d(TAG, "SetCourseActivitySecond: updateChildren() child is updated")
                    }
        }

        fun downloadChildrenForUpdateClass(reference: String) {
            //TODO это же заюзать когда учитель загружает детишек
            getDatabase().document(reference)
                    .get()
                    .addOnSuccessListener {
                        it.toObject(Child::class.java)?.let { child ->

                            val predicate: (Course.AgeGroup) -> Boolean = { it.minAge <= child.getAge() && it.maxAge >= child.getAge() }
                            val ageGroupOld = oldCourse.ageGroups.find(predicate)!!
                            val ageGroupNew = course.ageGroups.find(predicate)!!
                            val newClasses = ageGroupNew.classes.filter { it.children.any { it == reference } }

                            val childCourse = child.courses.find { it.id == course.id }!!
                            with(childCourse) {
                                name = course.name

                                newClasses.forEach { clazz ->
                                    val sourceClass = ageGroupOld.classes.find { it.id == clazz.id }!!
                                    if (sourceClass.classTime != clazz.classTime) {
                                        val sourceClassOfChild = classes.find { it == sourceClass.classTime }
                                        classes[classes.indexOf(sourceClassOfChild)] = clazz.classTime
                                    }
                                }
                            }
                            updateChildren(child, reference)
                        }
                    }
        }
        course.ageGroups.forEach { ageGroup ->
            val children = ageGroup.classes.flatMap { it.children }.toSet()
            children.forEach {
                downloadChildrenForUpdateClass(it)
            }
        }
    }


    private fun updateCoursePhoto(id: String = course.id) {
        getStorage().reference.child("courses").child(getUserBranch()).child(id).child("$id.jpg")
                .putBytes(bundle.getByteArray(EXTRA_PHOTO))
                .addOnSuccessListener {
                    Log.d(TAG, "SetCourseActivityFirst: updateCoursePhoto() course's photo has been updated")
                }
    }

    protected fun isAdding() = mPosition == -1
}