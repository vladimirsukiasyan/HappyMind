package company.sukiasyan.happymind.utils

import android.content.Context

const val PROFILE_PREFENECES = "profile"
const val CHILD_PREFENECES = "child"

fun Context.saveUserBranch(branch: String) {
    val edit = getSharedPreferences(PROFILE_PREFENECES, Context.MODE_PRIVATE).edit()
    edit.putString("branch", branch)
    edit.apply()
}

fun Context.getUserBranch() = getSharedPreferences(PROFILE_PREFENECES, Context.MODE_PRIVATE).getString("branch", "")!!

fun Context.saveUserRole(role: String) {
    val edit = getSharedPreferences(PROFILE_PREFENECES, Context.MODE_PRIVATE).edit()
    edit.putString("role", role)
    edit.apply()
}

fun Context.getUserRole() = getSharedPreferences(PROFILE_PREFENECES, Context.MODE_PRIVATE).getString("role", "")!!

fun Context.saveActiveChildId(childId: Long) {
    val edit = getSharedPreferences(CHILD_PREFENECES, Context.MODE_PRIVATE).edit()
    edit.putLong("id", childId)
    edit.apply()
}

fun Context.getActiveChildId() = getSharedPreferences(CHILD_PREFENECES, Context.MODE_PRIVATE).getLong("id", 0L)

fun Context.isFirstChild()=!getSharedPreferences(CHILD_PREFENECES,Context.MODE_PRIVATE).contains("id")

//fun Context.saveActiveChild(child: Child) {
//    val edit = getSharedPreferences(CHILD_PREFENECES, Context.MODE_PRIVATE).edit()
//    edit.putLong("id", child.id)
//    edit.putString("name", child.name)
//    edit.putString("last_name", child.last_name)
//    edit.putString("patronymic", child.patronymic)
//    edit.putString("photo_uri", child.photo_uri)
//    edit.putString("date_of_birthday", child.date_of_birthday)
//    edit.putString("phone", child.phone)
//    edit.putString("sex", child.sex)
//    edit.apply()
//}
//
//fun Context.getActiveChild(): Child {
//    with(getSharedPreferences(CHILD_PREFENECES, Context.MODE_PRIVATE)) {
//        val child = Child(
//                getLong("id", 0L),
//                getString("name", ""),
//                getString("last_name", ""),
//                getString("patronymic", ""),
//                getString("date_of_birthday", ""),
//                getString("sex", ""),
//                getString("phone", ""),
//                getString("photo_uri", "")
//        )
//        Log.d(TAG, "Utils: getActiveChild() $child")
//        return child
//    }
//}

