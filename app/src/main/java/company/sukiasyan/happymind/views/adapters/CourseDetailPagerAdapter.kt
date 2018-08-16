package company.sukiasyan.happymind.views.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import company.sukiasyan.happymind.fragments.DetailCourseFragments

class CourseDetailPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        //TODO создать для каждого пейджа свой фрагмент
        return DetailCourseFragments.newInstance(position + 1)
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 2
    }
}
