package company.sukiasyan.happymind.views.activities

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.models.Course
import company.sukiasyan.happymind.utils.*
import company.sukiasyan.happymind.views.adapters.CoursesAdapter
import kotlinx.android.synthetic.main.activity_courses.*
import kotlinx.android.synthetic.main.content_courses.*
import kotlinx.android.synthetic.main.toolbar.*


class CoursesActivity : BasicActivity(1) {

    private lateinit var viewAdapter: CoursesAdapter
    private lateinit var viewManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: CoursesActivity")

        //setting standard views
        setContentView(R.layout.activity_courses)
        setSupportActionBar(toolbar)
        setViewAdapter()

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
                downloadCourses()
                fab.visibility = View.VISIBLE
                fab.setOnClickListener {
                    val intent = Intent(this, SetCourseActivityFirst::class.java)
                    intent.putExtra(ARG_ITEM_POSITION, -1)
                    startActivity(intent)
                }
            }
            "teacher" -> {

//                downloadTeacherCourses()
            }
        }
        setUpNavigationDrawer(role, savedInstanceState)
        setUpBottomNavigationView(role)
    }


    override fun initContentUI() {
        downloadCourses()
    }

    private fun downloadCourses() {
        getDatabase().collection("filials").document(getUserBranch())
                .collection("courses")
                .get()
                .addOnSuccessListener {
                    courses = it.toObjects(Course::class.java)

                    if (getUserRole() == "parent") {
                        val age = activeChild.getAge()
                        courses = courses.filter { course ->
                            course.ageGroups.any {
                                it.minAge <= age && it.maxAge >= age
                            }
                        }
                    }
                    Log.d(TAG, "CoursesActivity: downloadCourses() $courses")
                    reInitViewAdapter()
                }
    }

    private fun setViewAdapter() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = CoursesAdapter(this) { position, course_image ->
            var intent = Intent()
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, course_image, courses[position].id)
            when (getUserRole()) {
                "admin" -> {
                    intent = Intent(this, SetCourseActivityFirst::class.java)
                    intent.putExtra(ARG_ITEM_POSITION, position)
                }
                "parent" -> {
                    intent = Intent(this, CoursesDetailActivity::class.java)
                }
            }
            intent.putExtra(EXTRA_COURSE, courses[position].copy())
            startActivity(intent, options.toBundle())
        }
        //TODO удаление курса( МБ контекстное меню?!) или внутри SetCourseActivityFirst в Toolbar в Settings кнопку вставить??
        recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun reInitViewAdapter() {
        viewAdapter.notifyDataSetChanged()
    }

    override fun onRestart() {
        Log.d(TAG, "onRestart: CoursesActivity")
        downloadCourses()
        super.onRestart()
    }
}
