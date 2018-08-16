package company.sukiasyan.happymind.views.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import company.sukiasyan.happymind.fragments.PlaceholderFragment
import company.sukiasyan.happymind.utils.TAG

class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    //TODO: Если FragmentPagerAdapter, тогда можно использовать static поля и в методе onCreate обращаться к static полям напрямую для обновления расписания. +: не нужно тратить память на передачу списков
    //TODO: Если FragmentStatePagerAdapter, тогда можно использовать не static поля для передачи аргументов, но придется обновлять данные через метод getItemPosition()

    override fun getItem(position: Int): Fragment {
        Log.d(TAG, "SectionsPagerAdapter: getItem() $position")
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return PlaceholderFragment.newInstance(position)
    }

    override fun getItemPosition(fragment: Any): Int {
        Log.d(TAG, "SectionsPagerAdapter: getItemPosition()")
        if (fragment is PlaceholderFragment) {
            fragment.notifyUpdate()
        }
        return super.getItemPosition(fragment)
    }

    override fun getCount(): Int {
        // Show 7 total pages.
        return 7
    }
}
