package company.sukiasyan.happymind.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import company.sukiasyan.happymind.models.Course.Class.ClassTime
import company.sukiasyan.happymind.utils.calcYears
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Child(val id: Long = 0L, val name: String = "", val last_name: String = "", val patronymic: String = "", val date_of_birthday: Date = Date(),
                 val sex: String = "", val phone: String = "", val courses: MutableList<ChildCourse> = mutableListOf()) : Parcelable {

    fun copy() = Child(id, name, last_name, patronymic, date_of_birthday, sex, phone, courses.toMutableList())

    @Parcelize
    data class ChildCourse(val id: String = "", var name: String = "", var classes: MutableList<ClassTime> = mutableListOf(), var abonement: ChildAbonement = ChildAbonement()) : Parcelable {

        fun copy() = ChildCourse(id, name, classes.toMutableList(), abonement)

        @Parcelize
        data class ChildAbonement(val countOfClasses: Int = 0, val balance: Int = 0, val price: Int = 0, @ServerTimestamp val timeOfPay: Timestamp = Timestamp.now()) : Parcelable
    }

    fun getAge(): Int {
        val now = GregorianCalendar()
        val birthday = GregorianCalendar().apply {
            time = date_of_birthday
        }
        return calcYears(birthday, now)
    }
}