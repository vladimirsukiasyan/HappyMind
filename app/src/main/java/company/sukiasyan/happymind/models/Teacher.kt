package company.sukiasyan.happymind.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Teacher(val uid: String = "", val email: String = "", val password: String = "", val name: String = "",
                   val last_name: String = "", val patronymic: String = "", val date_of_birthday: Date = Date(),
                   val education: String = "", val experience: Int = 0, val phone_number:String="",var photo_uri: String = "") : Parcelable