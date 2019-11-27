package company.sukiasyan.happymind.views.activities

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.models.Course.Class
import company.sukiasyan.happymind.utils.*
import company.sukiasyan.happymind.views.adapters.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_schedule.*
import kotlinx.android.synthetic.main.toolbar.*

class ScheduleActivity : BasicActivity(0) {

    private lateinit var mSectionsPagerAdapter: SectionsPagerAdapter
    private val scheduleMap: Map<String, MutableList<Class>> = mapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ScheduleActivity: onCreate()")

        //setting standard views
        setContentView(R.layout.activity_schedule)
        setSupportActionBar(toolbar)
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager, scheduleMap)
        container.adapter = mSectionsPagerAdapter
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        //setting special view for type of user
        authenticationSpecialUI(savedInstanceState)
    }

    private fun authenticationSpecialUI(savedInstanceState: Bundle?) {
        val role = getUserRole()
        when (role) {
            "parent" -> {
                downloadChildren()
            }
            "admin" -> {
                downloadAdminClassReferences()
            }
            "teacher" -> {
//                downloadTeacherCourses()
            }
        }
        setUpNavigationDrawer(role, savedInstanceState)
        setUpBottomNavigationView(role)
    }

    override fun childrenDownloadListener() {
        drawerProfiles()
        downloadChildClassReferences()
    }

    override fun coursesDownloadListener() {
        reInitContentUI()
    }

    override fun childChangedListener() {
        downloadChildClassReferences()
    }

    override fun reInitContentUI() {
        mSectionsPagerAdapter.notifyDataSetChanged()
    }

    private fun downloadChildClassReferences() {
        getDatabase().collection("parents").document(getAuth().uid!!)
                .collection("children").document(activeChild.name)
                .collection("classes")
                .addSnapshotListener { querySnapshot, _ ->
                    querySnapshot?.let { classes ->
                        for (clazz in classes) {
                            val endFlag = clazz == classes.last()
                            downloadSchedule(clazz.reference, endFlag)
                        }
                    }
                }
    }

    private fun downloadAdminClassReferences() {
        //TODO ЕСЛИ СЛИШКОМ ДОЛГО БУДЕТ ЗАГРУЖАТЬ ВСЕ, ТО СДЕЛАТЬ reInitContentUI() в каждом вызове, а не в конце
        getDatabase().collection("filials").document(getUserBranch())
                .collection("courses")
                .addSnapshotListener { querySnapshot, _ ->
                    querySnapshot?.let { courses ->
                        for (course in courses.documents) {
                            getDatabase().document(course.reference.toString()).collection("classes")
                                    .addSnapshotListener { querySnapshot, _ ->
                                        querySnapshot?.let { classes ->
                                            for (clazz in classes) {
                                                val endFlag = course == courses.last() && clazz == classes.last()
                                                downloadSchedule(clazz.reference, endFlag)
                                            }
                                        }
                                    }
                        }
                    }
                }
    }

    private fun downloadSchedule(reference: DocumentReference, endFlag: Boolean) {
        reference.addSnapshotListener { documentSnapshot, _ ->
            documentSnapshot?.let {
                val clazz = it.toObject(Class::class.java)!!
                val oldClass = scheduleMap[clazz.classTime.dayOfWeek]?.find { it.id == clazz.id }
                oldClass?.let {
                    scheduleMap[clazz.classTime.dayOfWeek]?.remove(oldClass)
                }
                scheduleMap[clazz.classTime.dayOfWeek]!!.add(clazz)
                if (endFlag) {
                    reInitContentUI()
                }
            }
        }
    }
}
