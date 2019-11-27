package company.sukiasyan.happymind.views.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.StringSignature
import com.firebase.ui.storage.images.FirebaseImageLoader
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.models.Teacher
import company.sukiasyan.happymind.utils.getStorage
import kotlinx.android.synthetic.main.item_teacher.view.*

class TeacherAdapter(val teachers: List<Teacher>, val clickListener: (Int) -> Unit, val deleteListener: (Int) -> Unit) : RecyclerView.Adapter<TeacherAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_teacher, parent, false))

    override fun getItemCount() = teachers.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            with(itemView) {
                val teacher = teachers[adapterPosition]

                setOnClickListener { clickListener(adapterPosition) }
                item_name.text = "${teacher.name} ${teacher.last_name}"

                val reference = getStorage().reference.child("users/teachers/${teacher.uid}.jpg")
                Glide.with(context)
                        .using(FirebaseImageLoader())
                        .load(reference)
                        .dontAnimate()
                        .signature(StringSignature(teacher.photo_uri))
                        .error(R.drawable.profile_placeholder)
                        .placeholder(R.drawable.profile_placeholder)
                        .into(item_image)

                setOnCreateContextMenuListener { menu, v, menuInfo ->
                    val delete = menu.add(Menu.NONE, 1, 1, "Удалить")
                    delete.setOnMenuItemClickListener {
                        deleteListener(adapterPosition)
                        true
                    }
                }
            }
        }
    }
}
