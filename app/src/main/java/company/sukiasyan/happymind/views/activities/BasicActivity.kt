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

    companion object {
        lateinit var headerResult: AccountHeader
        lateinit var result: Drawer
        lateinit var activeChild: Child
        var courses: List<Course> = listOf()
        var children = mutableListOf<Child>()
    }

    abstract fun initContentUI()

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
                            "Курсы" -> CoursesActivity::class.java
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
                                this@BasicActivity.onRestart()
                            }
                        }
                        false
                    }
                }
            }

//            sectionHeader("MaterialDrawerKt demos") {
//                divider = false
//            }
            primaryItem("Выйти") {
                icon = R.drawable.ic_sing_out
                onClick { _ ->
                    getAuth().signOut()
                    startActivity(Intent(this@BasicActivity, LoginActivity::class.java))
                    true
                }
            }
        }
    }

    fun downloadChildren() {
//        getDatabase().collection("parents").document(getAuth().uid!!)
//                .collection("children").get()
//                .addOnCompleteListener {
//                    if (it.isSuccessful) {
//                        children = it.result.toObjects(Child::class.java)
//                        Log.d(TAG, "downloadList: $children")
//                        drawerProfiles()
//                    }
//                }
        //SET REALTIME UPDATES
        getDatabase().collection("parents").document(getAuth().uid!!)
                .collection("children").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    firebaseFirestoreException?.let {
                        Log.d(TAG, "Listen failed.", firebaseFirestoreException)
                        return@addSnapshotListener
                    }
                    querySnapshot?.let {
                        children = it.toObjects(Child::class.java)
                        Log.d(TAG, "downloadChildren() snapshotListener: $children")
                        drawerProfiles()
                    }
                }
    }

    private fun drawerProfiles() {
        var count = 0
        children.forEach {
            headerResult.clear()
            headerResult.addProfile(ProfileDrawerItem().apply {
                withName(it.name)
                withIcon("http://users/parents/${getAuth().uid}/${it.name}/${it.name}")
                withIdentifier(it.id)
            }, count++)
        }
        headerResult.setActiveProfile(getActiveChildId())
        activeChild = children.find { it.id == getActiveChildId() }!!
        initContentUI()
    }

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