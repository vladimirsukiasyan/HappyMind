package company.sukiasyan.happymind.views.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.models.Course.AgeGroup.Class
import kotlinx.android.synthetic.main.item_name_and_delete.view.*

class ClassAdapter(private val classes: List<Class>, val clickListener: (View) -> Unit, val deleteClickListener: (Int) -> Unit) : RecyclerView.Adapter<ClassAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(classes[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_name_and_delete, parent, false))

    override fun getItemCount(): Int {
        return classes.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(clazz: Class) {
            with(clazz.classTime) {
                itemView.name.text = "${dayOfWeek.toUpperCase()}: $startClassHour:$startClassMinute- $endClassHour:$endClassMinute"
            }
            itemView.delete_btn.setOnClickListener {
                deleteClickListener(adapterPosition)
            }
            itemView.setOnClickListener {
                clickListener(itemView.apply { tag = adapterPosition })
            }
        }
    }
}