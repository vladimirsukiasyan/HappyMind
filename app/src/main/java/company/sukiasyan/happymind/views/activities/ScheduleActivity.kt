package company.sukiasyan.happymind.views.activities

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.models.Course
import company.sukiasyan.happymind.utils.TAG
import company.sukiasyan.happymind.utils.getDatabase
import company.sukiasyan.happymind.utils.getUserBranch
import company.sukiasyan.happymind.utils.getUserRole
import company.sukiasyan.happymind.views.adapters.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_schedule.*
import kotlinx.android.synthetic.main.toolbar.*

class ScheduleActivity : BasicActivity(0) {

    private lateinit var mSectionsPagerAdapter: SectionsPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ScheduleActivity: onCreate()")

        //setting standard views
        setContentView(R.layout.activity_schedule)
        setSupportActionBar(toolbar)

        //setting special view for type of user
        authenticationSpecialUI(savedInstanceState)
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
    }

    private fun authenticationSpecialUI(savedInstanceState: Bundle?) {
        val role = getUserRole()
        when (role) {
            "parent" -> {
                downloadChildren()
            }
            "admin" -> {
                downloadCourses()
            }
            "teacher" -> {
//                downloadTeacherCourses()
            }
        }
        setUpNavigationDrawer(role, savedInstanceState)
        setUpBottomNavigationView(role)
    }

    private fun downloadCourses() {
        getDatabase().collection("filials").document(getUserBranch())
                .collection("courses")
                .get()
                .addOnSuccessListener {
                    courses = it.toObjects(Course::class.java)
                    Log.d(TAG, "ScheduleActivity: downloadCourses() $courses")
                    initContentUI()
                }
    }

    override fun initContentUI() {
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter
    }

    private fun reInitContentUI() {
        mSectionsPagerAdapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onRestart() {
        Log.d(TAG, "ScheduleActivity: onRestart()")
        reInitContentUI()
        super.onRestart()
    }

    override fun onStart() {
        Log.d(TAG, "ScheduleActivity: onStart()")
        super.onStart()
    }
}
