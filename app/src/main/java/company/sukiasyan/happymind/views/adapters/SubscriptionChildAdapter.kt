package company.sukiasyan.happymind.views.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.models.Course.AgeGroup.Abonement
import kotlinx.android.synthetic.main.item_abonement.view.*

class SubscriptionChildAdapter(val abonements: List<Abonement>, val clickListener: (Abonement) -> Unit) : RecyclerView.Adapter<SubscriptionChildAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(abonements[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_abonement, parent, false))

    override fun getItemCount() = abonements.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(abonement: Abonement) {
            itemView.abonement_text.text = "${abonement.countOfClasses} занятий"
            itemView.abonement_price.text = "${abonement.price} руб."
            itemView.setOnClickListener { clickListener(abonement) }
        }
    }
}