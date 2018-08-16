package company.sukiasyan.happymind.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import company.sukiasyan.happymind.models.Course.AgeGroup.Class.ClassTime
import company.sukiasyan.happymind.utils.calcYears
import java.util.*

data class Child(val id: Long = 0L, val name: String = "", val last_name: String = "", val patronymic: String = "", val date_of_birthday: String = "",
                 val sex: String = "", val phone: String = "", val courses: MutableList<ChildCourse> = mutableListOf()) {

    fun copy() = Child(id, name, last_name, patronymic, date_of_birthday, sex, phone, courses.toMutableList())

    data class ChildCourse(val id: String = "", var name: String = "", var classes: MutableList<ClassTime> = mutableListOf(), var abonement: ChildAbonement = ChildAbonement()) {

        fun copy() = ChildCourse(id, name, classes.toMutableList(), abonement)

        data class ChildAbonement(val countOfClasses: Int = 0, val balance: Int = 0, val price: Int = 0, @ServerTimestamp val timeOfPay: Timestamp = Timestamp.now())
    }

    fun getAge(): Int {
        val (day, month, year) = date_of_birthday.split(".").map { it.toInt() }
        val now = GregorianCalendar()
        return calcYears(GregorianCalendar(year, month, day), now)
    }
}