package company.sukiasyan.happymind.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Course(var id: String = "", var photo_uri: String = "", var name: String = "", var description: String = "", var rating: Float = 0f) : Parcelable {

    fun copy() = Course(id, photo_uri, name, description, rating)

    @Parcelize
    data class Class(var id: String = "", var name:String="", var minAge: Int = 0, var maxAge: Int = 0, var classTime: ClassTime = ClassTime(), var teacher_uid: String = "", var children: MutableList<String> = mutableListOf(), var places: Int = 0, var occupied_places: Int = 0) : Parcelable {

        fun copy() = Class(id, name, minAge, maxAge, classTime.copy(), teacher_uid, children.toMutableList(), places, occupied_places)

        @Parcelize
        data class ClassTime(var dayOfWeek: String = "", var startClassHour: Int = 0, var startClassMinute: Int = 0, var endClassHour: Int = 0, var endClassMinute: Int = 0) : Parcelable
    }

    @Parcelize
    data class Abonement(var price: Int = 0, var countOfClasses: Int = 0) : Parcelable
}