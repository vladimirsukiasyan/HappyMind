package company.sukiasyan.happymind.views.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.models.Course.AgeGroup
import kotlinx.android.synthetic.main.item_name_and_delete.view.*

class AgeGroupAdapter(private val list: List<AgeGroup>, private val clickListener: (View) -> Unit, private val deleteClickListener: (Int) -> Unit) : RecyclerView.Adapter<AgeGroupAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_name_and_delete, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: AgeGroup) {
            with(itemView) {
                name.text = "${item.minAge}-${item.maxAge} лет"
                setOnClickListener { clickListener(this.apply { tag = adapterPosition }) }
                delete_btn.setOnClickListener { deleteClickListener(adapterPosition) }
            }
        }
    }
}