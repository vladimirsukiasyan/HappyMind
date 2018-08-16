package company.sukiasyan.happymind.views.adapters

import android.content.Context
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.utils.getStorage
import company.sukiasyan.happymind.utils.getUserBranch
import company.sukiasyan.happymind.views.activities.BasicActivity.Companion.courses
import kotlinx.android.synthetic.main.course_item.view.*

class CoursesAdapter(val context: Context, val clickListener: (Int, ImageView) -> Unit) : RecyclerView.Adapter<CoursesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.course_item, parent, false))

    override fun getItemCount() = courses.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            val course = courses[adapterPosition]
            ViewCompat.setTransitionName(view.item_image, course.id)

            view.setOnClickListener { clickListener(adapterPosition, view.item_image) }

            view.item_name.text = course.name

            val reference = getStorage().reference.child("courses/${context.getUserBranch()}/${course.id}/${course.id}.jpg")
            Glide.with(view.context)
                    .using(FirebaseImageLoader())
                    .load(reference)
                    .error(R.drawable.item_placeholder)
                    .placeholder(R.drawable.item_placeholder)
                    .into(view.item_image)

        }
    }
}
