package company.sukiasyan.happymind.views.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.models.Course.Class
import company.sukiasyan.happymind.models.Teacher
import company.sukiasyan.happymind.utils.getDatabase
import company.sukiasyan.happymind.utils.getUserRole
import company.sukiasyan.happymind.utils.timeTemplate
import kotlinx.android.synthetic.main.item_schedule.view.*

class ScheduleAdapter(var list: List<Class>, val context: Context) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false))

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(clazz: Class) {
            view.schedule_name.text = clazz.name
            with(clazz) {
                view.schedule_time.text = "${timeTemplate.format(classTime.startClassHour, classTime.startClassMinute)} - ${timeTemplate.format(classTime.endClassHour, classTime.endClassMinute)}"
            }
            when (context.getUserRole()) {
                "parent" -> {
                    view.schedule_age_group.visibility = View.GONE
                    view.schedule_teacher.visibility = View.GONE
                }
                "teacher" -> {
                    view.schedule_age_group.text = "${clazz.minAge} - ${clazz.maxAge} лет"
                    view.schedule_teacher.visibility=View.GONE
                }
                "admin" -> {
                    view.schedule_age_group.text = "${clazz.minAge} - ${clazz.maxAge} лет"
                    getDatabase().collection("teachers").document(clazz.teacher_uid)
                            .get()
                            .addOnSuccessListener {
                                val teacher = it.toObject(Teacher::class.java)
                                view.schedule_teacher.text = "${teacher?.name} ${teacher?.last_name}"
                            }
                }
            }
        }
    }
}
