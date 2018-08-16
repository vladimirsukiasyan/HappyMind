package company.sukiasyan.happymind.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Course(var id: String = "", var name: String = "", var description: String = "", var teacher_uid: String = "", var ageGroups: MutableList<AgeGroup> = mutableListOf(), var rating: Float = 0f) : Parcelable {

    fun copy() = Course(id, name, description, teacher_uid, ageGroups.toMutableList(), rating)

    @Parcelize
    data class AgeGroup(var minAge: Int = 0, var maxAge: Int = 18, var duration: Int = 0, val abonements: MutableList<Abonement> = mutableListOf(), val classes: MutableList<Class> = mutableListOf()) : Parcelable {

        fun copy() = AgeGroup(minAge, maxAge, duration, abonements.toMutableList(), classes.toMutableList())

        @Parcelize
        data class Class(var id: Int = -1, var classTime: ClassTime = ClassTime(), var children: MutableList<String> = mutableListOf(), var places: Int = 0, var occupied_places: Int = 0, var reserved_places: Int = 0) : Parcelable {

            fun copy() = Class(id, classTime.copy(), children.toMutableList(), places, occupied_places, reserved_places)

            @Parcelize
            data class ClassTime(var dayOfWeek: String = "", var startClassHour: Int = 0, var startClassMinute: Int = 0, var endClassHour: Int = 0, var endClassMinute: Int = 0) : Parcelable
        }

        @Parcelize
        data class Abonement(var price: Int = 0, var countOfClasses: Int = 0) : Parcelable
    }
}