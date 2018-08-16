package company.sukiasyan.happymind.views.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.utils.ClassSchedule
import kotlinx.android.synthetic.main.item_schedule.view.*

class ScheduleAdapter(var list: List<ClassSchedule>, val context: Context) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false))

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(classSchedule: ClassSchedule) {
            view.schedule_name.text = classSchedule.name
            with(classSchedule) {
                //TODO выводить время в формате HH:MM (12:00 instead of 12:0)
                view.schedule_time.text = "$startClassHour:$startClassMinute - $endClassHour:$endClassMinute"
            }
        }
    }
}
