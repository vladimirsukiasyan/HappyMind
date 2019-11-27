package company.sukiasyan.happymind.views.adapters

import android.content.Context
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.StringSignature
import com.firebase.ui.storage.images.FirebaseImageLoader
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.models.Course
import company.sukiasyan.happymind.utils.getStorage
import company.sukiasyan.happymind.utils.getUserBranch
import kotlinx.android.synthetic.main.item_course.view.*

class CourseAdapter(val context: Context, val clickListener: (Int, ImageView) -> Unit) : RecyclerView.Adapter<CourseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false))

    override fun getItemCount() = listOf<Course>().size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            with(itemView) {
                val course = listOf<Course>()[adapterPosition]
                ViewCompat.setTransitionName(item_image, course.id)

                setOnClickListener { clickListener(adapterPosition, item_image) }

                item_name.text = course.name

                val reference = getStorage().reference.child("courses/${context.getUserBranch()}/${course.id}/${course.id}.jpg")
                Glide.with(context)
                        .using(FirebaseImageLoader())
                        .load(reference)
                        .signature(StringSignature(course.photo_uri))
                        .error(R.drawable.item_placeholder)
                        .placeholder(R.drawable.item_placeholder)
                        .into(item_image)
            }
        }
    }
}
