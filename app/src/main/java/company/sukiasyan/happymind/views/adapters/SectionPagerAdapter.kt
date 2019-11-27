package company.sukiasyan.happymind.views.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.util.Log
import company.sukiasyan.happymind.fragments.PlaceholderFragment
import company.sukiasyan.happymind.models.Course.Class
import company.sukiasyan.happymind.utils.TAG

class SectionsPagerAdapter(fm: FragmentManager, val scheduleMap: Map<String, MutableList<Class>>) : FragmentPagerAdapter(fm) {

    val daysOfWeek = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье")

    override fun getItem(position: Int): Fragment {
        Log.d(TAG, "SectionsPagerAdapter: getItem() $position")
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return PlaceholderFragment.newInstance(position, scheduleMap[daysOfWeek[position]]
                ?: mutableListOf())
    }

    override fun getItemPosition(fragment: Any): Int {
        Log.d(TAG, "SectionsPagerAdapter: getItemPosition()")
        return PagerAdapter.POSITION_NONE
    }

    override fun getCount(): Int {
        // Show 7 total pages.
        return 7
    }
//    private fun getScheduleOnDay(position: Int){
//
//        getDatabase().collection("parents").document(getAuth().uid!!)
//                .collection("children").document(activeChild.name)
//                .collection("classes")
//                .whereEqualTo("class_time.dayOfWeek",daysOfWeek[position])
//                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//                    firebaseFirestoreException?.let {
//                        Log.d(TAG, "SectionsPagerAdapter: getScheduleOnDay() SnapshotListener failed")
//                        return@addSnapshotListener
//                    }
//                    querySnapshot?.let {
//
//                    }
//                }
//    }
}
