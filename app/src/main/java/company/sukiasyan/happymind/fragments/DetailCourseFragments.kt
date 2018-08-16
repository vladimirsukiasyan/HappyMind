package company.sukiasyan.happymind.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.utils.TAG
import company.sukiasyan.happymind.utils.getChildReference
import company.sukiasyan.happymind.utils.getStorage
import company.sukiasyan.happymind.utils.getUserBranch
import company.sukiasyan.happymind.views.activities.CoursesDetailActivity.Companion.childCourse
import company.sukiasyan.happymind.views.activities.CoursesDetailActivity.Companion.course
import company.sukiasyan.happymind.views.activities.CoursesDetailActivity.Companion.courseAgeGroup
import company.sukiasyan.happymind.views.adapters.ClassChildAdapter
import kotlinx.android.synthetic.main.activity_courses_detail.*
import kotlinx.android.synthetic.main.fragment_description.view.*


class DetailCourseFragments() : Fragment() {
    private var sectionNumber: Int = 0

    @SuppressLint("ValidFragment")
    constructor(_sectionNumber: Int) : this() {
        sectionNumber = _sectionNumber
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            when (sectionNumber) {
                1 -> {
                    Log.d(TAG, "onCreateView: DetailCourseFragment")
                    val rootView = inflater.inflate(R.layout.fragment_description, container, false)

                    rootView.nested_scroll_view.scrollTo(0, 0)
                    rootView.item_name.text = course.name
                    rootView.item_age.text = "От ${courseAgeGroup.minAge} до ${courseAgeGroup.maxAge} лет"
                    rootView.item_duration.text = "${courseAgeGroup.duration} минут"
                    rootView.item_balance.text = "Остаток занятий: ${childCourse.abonement.balance}"
                    rootView.item_place.text = context!!.getUserBranch()
                    downloadTeacherPhoto(rootView)
                    rootView.item_teacher.text = course.teacher_uid //TODO Получить данные препода и сделать ссылку на страницу

                    val viewAdapter = ClassChildAdapter(courseAgeGroup.classes) { clazz, position, isChecked ->
                        activity?.fab?.show()
                        if (isChecked) {
                            childCourse.classes.add(clazz.classTime)
                            with(courseAgeGroup.classes[position]) {
                                occupied_places++
                                children.add(getChildReference())
                            }
                        } else {
                            childCourse.classes.remove(clazz.classTime)
                            with(courseAgeGroup.classes[position]) {
                                occupied_places--
                                children.remove(getChildReference())
                            }
                        }
                    }
                    rootView.recycler_view.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(context)
                        adapter = viewAdapter
                    }
                    rootView
                }
                2 -> {
                    val rootView = inflater.inflate(R.layout.fragment_main, container, false)
                    rootView
                }
                else -> {
                    null
                }
            }


    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(sectionNumber: Int): DetailCourseFragments {
            val fragment = DetailCourseFragments(sectionNumber)
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }


    private fun downloadTeacherPhoto(view: View) {
        val reference = getStorage().reference.child("users/teachers/${course.teacher_uid}.jpg")
        Glide.with(this)
                .using(FirebaseImageLoader())
                .load(reference)
                .centerCrop()
                .error(R.drawable.ic_photo)
                .placeholder(R.drawable.ic_photo)
                .into(view.photo_teacher)
    }
}