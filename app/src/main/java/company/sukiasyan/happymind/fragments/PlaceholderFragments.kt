package company.sukiasyan.happymind.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.models.Course.Class
import company.sukiasyan.happymind.utils.EXTRA_SCHEDULE
import company.sukiasyan.happymind.utils.TAG
import company.sukiasyan.happymind.views.adapters.ScheduleAdapter
import kotlinx.android.synthetic.main.fragment_main.view.*

class PlaceholderFragment : Fragment() {
    private var position: Int = 0
    private lateinit var schedule: List<Class>
    private lateinit var viewAdapter: ScheduleAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        position = getPosition()
        Log.d(TAG, "PlaceholderFragment: onCreateView() $position")

        getSchedule()

        val rootView = inflater.inflate(R.layout.fragment_main, container, false)

        viewAdapter = ScheduleAdapter(schedule, context!!)

        rootView.recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = viewAdapter
        }

        return rootView
    }

    fun getSchedule() {
        //TODO сортировать по начальному времени

        schedule = (arguments!!.getParcelableArray(EXTRA_SCHEDULE) as Array<Class>).toList()
//
//        schedule = when (context!!.getUserRole()) {
//            "parent" -> {
//                activeChild.courses
//                        .flatMap { course ->
//                            course.classes.map { ClassSchedule(course.name, it.dayOfWeek, it.startClassHour, it.startClassMinute, it.endClassHour, it.endClassMinute) }
//                        }
//                        .filter { clazz ->
//                            clazz.dayOfWeek == daysOfWeek[position]
//                        }
//
//            }
//            "admin" ->
//                courses
//                        .flatMap { course ->
//                            course.ageGroups.flatMap { ageGroup ->
//                                ageGroup.classes.map {
//                                    with(it.classTime) {
//                                        ClassSchedule(course.name, dayOfWeek, startClassHour, startClassMinute, endClassHour, endClassMinute, ageGroup.minAge, ageGroup.maxAge, course.teacher_uid)
//                                    }
//                                }
//                            }
//                        }
//                        .filter { clazz ->
//                            clazz.dayOfWeek == daysOfWeek[position]
//                        }
//            else -> listOf()
//        }
//        schedule = schedule.sortedBy { it.startClassHour }
    }

    fun getPosition() = arguments!!.getInt(ARG_SECTION_NUMBER)

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"
        private val daysOfWeek = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье")

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(sectionNumber: Int, schedule: MutableList<Class>): PlaceholderFragment {
            Log.d(TAG, "PlaceholderFragment: newInstance() $sectionNumber")
            val fragment = PlaceholderFragment()
            fragment.arguments = Bundle().apply {
                putInt(ARG_SECTION_NUMBER, sectionNumber)
                putParcelableArray(EXTRA_SCHEDULE, schedule.toTypedArray())
            }
            return fragment
        }
    }
}