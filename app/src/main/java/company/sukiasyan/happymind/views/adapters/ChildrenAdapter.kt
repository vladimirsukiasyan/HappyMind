package company.sukiasyan.happymind.views.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.models.Child
import kotlinx.android.synthetic.main.item_name_and_delete.view.*

class ChildrenAdapter(private val children: List<Child>) : RecyclerView.Adapter<ChildrenAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(children[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_name_and_delete, parent, false))

    override fun getItemCount() = children.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(child: Child) {
            itemView.name.text = child.name
            itemView.delete_btn.visibility = View.GONE
        }
    }
}