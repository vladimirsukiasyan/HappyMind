package company.sukiasyan.happymind.views.adapters

import android.support.v4.content.ContextCompat.getColor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.models.Course.AgeGroup.Class
import company.sukiasyan.happymind.views.activities.CoursesDetailActivity.Companion.childCourse
import kotlinx.android.synthetic.main.item_child_class.view.*

class ClassChildAdapter(private val classes: List<Class>, val checkBoxListener: (Class, Int, Boolean) -> Unit) : RecyclerView.Adapter<ClassChildAdapter.ClassHolder>() {

    override fun onBindViewHolder(holder: ClassHolder, position: Int) {
        holder.bind(classes[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ClassHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_child_class, parent, false))

    override fun getItemCount(): Int {
        return classes.size
    }

    inner class ClassHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(clazz: Class, position: Int) {
            with(view) {
                with(clazz.classTime) {
                    class_time.text = "${dayOfWeek.toUpperCase()}: $startClassHour:$startClassMinute- $endClassHour:$endClassMinute"
                }
                class_free_place.text = "Осталось мест: ${clazz.places - clazz.occupied_places}"
                val isAddedClass = childCourse.classes.any { it == clazz.classTime }
                class_checkbox.isChecked = isAddedClass
                if ((clazz.places - clazz.occupied_places) == 0 && !isAddedClass) {
                    class_checkbox.isEnabled = false
                    view.setBackgroundColor(getColor(context, R.color.grey))
                } else {
                    setOnClickListener {
                        class_checkbox.isChecked = !class_checkbox.isChecked
                    }
                    class_checkbox.setOnCheckedChangeListener { _, isChecked -> checkBoxListener(clazz, position, isChecked) }
                }
            }
        }
    }
}