//package company.sukiasyan.happymind.fragments
//
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.graphics.Bitmap
//import android.graphics.drawable.BitmapDrawable
//import android.os.Bundle
//import android.provider.MediaStore
//import android.support.v4.app.Fragment
//import android.util.Log
//import android.view.*
//import android.widget.ArrayAdapter
//import com.bumptech.glide.Glide
//import com.firebase.ui.storage.images.FirebaseImageLoader
//import company.sukiasyan.happymind.R
//import company.sukiasyan.happymind.models.Course
//import company.sukiasyan.happymind.models.Teacher
//import company.sukiasyan.happymind.utils.*
//import kotlinx.android.synthetic.main.fragment_set_course_first.view.*
//import java.io.ByteArrayOutputStream
//
//
//class SetCourseFirstFragment : Fragment() {
//    private val GALLERY_PICKER_CODE = 1
//
//    private var listener: OnNextFragmentInteractionListener? = null
//    private lateinit var course: Course
//    private lateinit var teachers: List<Teacher>
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        Log.d(TAG, "SetCourseFirstFragment: onCreate()")
//
//        setHasOptionsMenu(true)
//        arguments?.let {
//            course = it.getParcelable(EXTRA_COURSE)
//        }
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View? {
//        Log.d(TAG, "SetCourseFirstFragment: onCreateView()")
//        val view = inflater.inflate(R.layout.fragment_set_course_first, container, false)
//        downloadTeacherList()
//        with(view) {
//            if (isCourseEdit()) {
//                downloadCoursePhoto()
//                course_name.setText(course.name)
//                course_description.setText(course.description)
//            }
//
//            next_btn.setOnClickListener {
//                val error = validateEditView(course_name, course_description)
//                if (!error) {
//                    course.name = course_name.text.toString()
//                    course.description = course_description.text.toString()
//                    course.teacher_uid = teachers[course_teacher_spinner.selectedItemPosition].uid
//                    listener?.onNextFragmentInteractionListener(course, getBundleForTransmit())
//                }
//            }
//            course_image.setOnClickListener {
//                val photoPicker = Intent(Intent.ACTION_GET_CONTENT)
//                photoPicker.type = "image/*"
//                startActivityForResult(photoPicker, GALLERY_PICKER_CODE)
//            }
//        }
//
//        return view
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
//        if (!isCourseEdit()) {
//            //adding course
//            menu?.findItem(R.id.action_add)?.isVisible = false
//        }
//        super.onCreateOptionsMenu(menu, inflater)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == R.id.action_add) {
//            //updating course
//            with(view!!) {
//                course.name = course_name.text.toString()
//                course.description = course_description.text.toString()
//                course.teacher_uid = teachers[course_teacher_spinner.selectedItemPosition].uid
//            }
//            updateCoursePhoto()
//            updateCourse()
//        }
//        return super.onOptionsItemSelected(item)
//    }
//
//    private fun updateCoursePhoto() {
//        val bitmap = (view?.course_image?.drawable as BitmapDrawable).bitmap
//        val baos = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//        val data = baos.toByteArray()
//
//        getStorage().reference.child("courses").child(activity!!.getUserBranch()).child(course.name).child(course.name)
//                .putBytes(data)
//                .addOnSuccessListener {
//                    Log.d(TAG, "SetCourseFirstFragment: uploadCoursePhoto() course's photo has been updated")
//                }
//    }
//
//    private fun updateCourse() {
//        getDatabase().collection("filials").document(activity!!.getUserBranch())
//                .collection("courses").document(course.name)
//                .set(course)
//                .addOnSuccessListener {
//                    Log.d(TAG, "SetCourseFirstFragment: onOptionsItemSelected() course has been updated")
//                }
//
//    }
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is OnNextFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
//        }
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        Log.d(TAG, "SetCourseFirstFragment: onDetach()")
//        listener = null
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        when (requestCode) {
//            GALLERY_PICKER_CODE -> {
//                if (resultCode == Activity.RESULT_OK) {
//                    Log.d(TAG, "onActivityResult: OnActivityResult is ok")
//                    val bitmap: Bitmap
//                    val selectedImage = data?.data
//                    bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedImage)
//                    view?.course_image?.setImageBitmap(bitmap)
//                }
//            }
//        }
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     */
//
//    interface OnNextFragmentInteractionListener {
//        fun onNextFragmentInteractionListener(course: Course, bundle: Bundle)
//    }
//
//    private fun getBundleForTransmit() = Bundle().apply {
//        putParcelable(SetCourseFirstFragment.EXTRA_COURSE_PHOTO, (view?.course_image?.drawable as BitmapDrawable).bitmap)
//    }
//
//    private fun isCourseEdit() = course.name.isNotEmpty()
//
//    private fun downloadCoursePhoto() {
//        val reference = getStorage().reference.child("courses/${activity?.getUserBranch()}/${course.name}/${course.name}.jpg")
//        Glide.with(activity)
//                .using(FirebaseImageLoader())
//                .load(reference)
//                .placeholder(R.drawable.item_placeholder)
//                .error(R.drawable.item_placeholder)
//                .into(view?.course_image)
//    }
//
//    private fun downloadTeacherList() {
//        getDatabase().collection("teachers")
//                .get()
//                .addOnSuccessListener {
//                    teachers = it.toObjects(Teacher::class.java)
//                    val teacherName = teachers.map { it.name + " " + it.last_name }
//                    view?.course_teacher_spinner?.adapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, teacherName).apply {
//                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                    }
//                    if (isCourseEdit()) {
//                        val teacher = teachers.find { it.uid == course.teacher_uid }
//                        teacher?.let {
//                            view?.course_teacher_spinner?.setSelection(teachers.indexOf(it))
//                        }
//                    }
//                }
//    }
//
//    companion object {
//        const val EXTRA_COURSE_PHOTO = "COURSE_PHOTO"
//        @JvmStatic
//        fun newInstance(course: Course) =
//                SetCourseFirstFragment().apply {
//                    arguments = Bundle().apply {
//                        putParcelable(EXTRA_COURSE, course)
//                    }
//                }
//    }
//}
