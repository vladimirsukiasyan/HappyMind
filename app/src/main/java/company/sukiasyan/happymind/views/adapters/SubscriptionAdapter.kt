package company.sukiasyan.happymind.views.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.models.Course.AgeGroup.Abonement
import kotlinx.android.synthetic.main.item_name_and_delete.view.*

class SubscriptionAdapter(private val abonements: List<Abonement>, val clickListener: (View) -> Unit, val deleteClickListener: (View) -> Unit) : RecyclerView.Adapter<SubscriptionAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(abonements[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_name_and_delete, parent, false))

    override fun getItemCount() = abonements.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(abonement: Abonement) {
            itemView.name.text = "${abonement.countOfClasses} занятий - ${abonement.price} рублей"
            itemView.delete_btn.setOnClickListener {
                deleteClickListener(itemView.apply { tag = adapterPosition })
            }
            itemView.setOnClickListener {
                clickListener(itemView.apply { tag = adapterPosition })
            }
            itemView.tag = adapterPosition
        }
    }
}