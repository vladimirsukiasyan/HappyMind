package company.sukiasyan.happymind.views.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import co.zsmb.materialdrawerkt.builders.accountHeader
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.profile.profileSetting
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.models.Child
import company.sukiasyan.happymind.models.Course
import company.sukiasyan.happymind.utils.*
import kotlinx.android.synthetic.main.activity_courses.*
import kotlinx.android.synthetic.main.toolbar.*


abstract class BasicActivity(val navItemNumber: Int) : AppCompatActivity() {

    lateinit var headerResult: AccountHeader
    lateinit var result: Drawer
    var activeChild: Child = Child()
    var courses: List<Course> = listOf()
    var children = mutableListOf<Child>()


    abstract fun reInitContentUI()
    abstract fun childrenDownloadListener()
    abstract fun coursesDownloadListener()
    abstract fun childChangedListener()

    fun setUpBottomNavigationView(userRole: String) {
        with(bottom_navigation_view) {
            accentColor = getColorById(R.color.accent)
            inactiveColor = getColorById(R.color.darker_gray)
            defaultBackgroundColor = getColorById(R.color.white)
            isBehaviorTranslationEnabled = false
            titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW
            val adapter = AHBottomNavigationAdapter(this@BasicActivity, when (userRole) {
                "parent" -> R.menu.navigation_parent
                "admin" -> R.menu.navigation_admin
                "teacher" -> R.menu.navigation_teacher
                else -> throw Exception()
            })
            adapter.setupWithBottomNavigation(this)
            fab?.let { manageFloatingActionButtonBehavior(it) }
            currentItem = navItemNumber

            setOnTabSelectedListener { position, wasSelected ->
                val nextActivity =
                        when (getItem(position).getTitle(this@BasicActivity)) {
                            "Расписание" -> ScheduleActivity::class.java
//                            "Курсы" -> CoursesActivity::class.java
                            else -> {
                                Log.d(TAG, "unknown nav item has been clicked!")
                                null
                            }
                        }
                if (nextActivity != null) {
                    val intent = Intent(this@BasicActivity, nextActivity)
                    startActivity(intent)
                    false
                } else {
                    false
                }
            }
        }
    }


    fun setUpNavigationDrawer(role: String, savedInstanceState: Bundle?) {
        result = drawer {
            toolbar = this@BasicActivity.toolbar
            hasStableIds = true
            savedInstance = savedInstanceState
            showOnFirstLaunch = true

            headerResult = accountHeader {
                background = R.drawable.side_nav_bar
                savedInstance = savedInstanceState
                translucentStatusBar = true

                if (role == "parent") {
                    profileSetting("Добавить ребенка") {
                        icon = R.drawable.ic_child_add
                        identifier = 100_000
                    }
                    onProfileChanged { _, profile, _ ->
                        when (profile.identifier) {
                            100_000L -> {
                                startActivity(Intent(this@BasicActivity, AddChildActivity::class.java))
                            }
                            else -> {
                                activeChild = children.find { it.id == profile.identifier }!!
                                saveActiveChildId(profile.identifier)
                                childChangedListener()
                            }
                        }
                        false
                    }
                }
            }

            if (role == "admin") {
                primaryItem {
                    name = "Преподаватели"
                    icon = R.drawable.ic_course_teacher
                    onClick { _ ->
                        startActivity(Intent(this@BasicActivity, TeachersActivity::class.java))
                        true
                    }
                }
            }

            primaryItem {
                name = "Выйти"
                icon = R.drawable.ic_sing_out
                onClick { _ ->
                    getAuth().signOut()
                    startActivity(Intent(this@BasicActivity, LoginActivity::class.java))
                    true
                }
            }
        }
    }

    fun drawerProfiles() {
        var count = 0
        children.forEach {
            headerResult.removeProfileByIdentifier(it.id)
        }
        children.forEach {
            headerResult.addProfile(ProfileDrawerItem().apply {
                withName(it.name)
                withIcon("http://users/parents/${getAuth().uid}/${it.name}/${it.name}")
                withIdentifier(it.id)
            }, count++)
        }
        headerResult.setActiveProfile(getActiveChildId())
        activeChild = children.find { it.id == getActiveChildId() }!!
    }

    fun downloadChildren() {
        //SET REALTIME UPDATES
        getDatabase().collection("parents").document(getAuth().uid!!)
                .collection("children").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    firebaseFirestoreException?.let {
                        Log.d(TAG, "BasicActivity: downloadChildren(): Listen failed.", firebaseFirestoreException)
                        return@addSnapshotListener
                    }
                    querySnapshot?.let {
                        children.clear()
                        children.addAll(it.toObjects(Child::class.java))
                        Log.d(TAG, "downloadChildren() snapshotListener: $children")
                        childrenDownloadListener()
                    }
                }
    }

    fun downloadCourses() {
        getDatabase().collection("filials").document(getUserBranch())
                .collection("courses")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    firebaseFirestoreException?.let {
                        Log.d(TAG, "CoursesActivity: downloadCourses() Listen failed.")
                        return@addSnapshotListener
                    }
                    querySnapshot?.let {
                        courses = it.toObjects(Course::class.java)

                        Log.d(TAG, "CoursesActivity: downloadCourses() $courses")
                        coursesDownloadListener()
                    }
                }
    }

//    fun filterCourseByAge() {
//        val age = activeChild.getAge()
//        courses = courses.filter { course ->
//            course.ageGroups.any {
//                it.minAge <= age && it.maxAge >= age
//            }
//        }
//    }

    override fun onBackPressed() {
        if (result.isDrawerOpen) {
            result.closeDrawer()
        } else {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        result.saveInstanceState(outState)
        headerResult.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }
}