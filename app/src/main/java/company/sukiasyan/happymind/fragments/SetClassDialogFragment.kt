package company.sukiasyan.happymind.fragments

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.models.Child
import company.sukiasyan.happymind.models.Course.AgeGroup.Class
import company.sukiasyan.happymind.utils.*
import company.sukiasyan.happymind.views.adapters.ChildrenAdapter
import kotlinx.android.synthetic.main.fragment_set_class_dialog.view.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SetClassDialogFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SetClassDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class SetClassDialogFragment : DialogFragment() {

    private lateinit var mClass: Class
    private var mChildren = mutableListOf<Child>()
    private var mIsLargeLayout: Boolean = false
    private var mPosition: Int = -1

    private lateinit var viewAdapter: ChildrenAdapter
    private var listener: OnClassFragmentFinishListener? = null
    private val actionDoneListener: (View) -> Boolean = {
        val error = validateEditView(it.places_edit, it.occupied_places_edit, it.reserved_places_edit)
        if (!error) {
            with(mClass) {
                places = it.places_edit.text.toString().toInt()
                occupied_places = it.occupied_places_edit.text.toString().toInt()
                reserved_places = it.reserved_places_edit.text.toString().toInt()
                classTime.dayOfWeek = it.day_of_week_spinner.selectedItem.toString()
            }
            listener?.onClassFragmentFinish(mClass, mPosition)
            dismiss()
        }
        error
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        listener = targetFragment as OnClassFragmentFinishListener

        mClass = arguments!!.getParcelable(ARG_ITEM)
        mPosition = arguments!!.getInt(ARG_ITEM_POSITION)

        Log.d(TAG, "SetClassDialogFragment: onCreate() class=$mClass")
        mIsLargeLayout = resources.getBoolean(R.bool.large_layout)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d(TAG, "SetClassDialogFragment: onCreateView()")

        val rootView = inflater.inflate(R.layout.fragment_set_class_dialog, container, false)

        with(rootView) {
            if (!isAdding()) {
                downloadChildren()
                val index = resources.getStringArray(R.array.days_of_week).indexOf(mClass.classTime.dayOfWeek)
                day_of_week_spinner.setSelection(index)
                places_edit.setText(mClass.places.toString())
                occupied_places_edit.setText(mClass.occupied_places.toString())
                reserved_places_edit.setText(mClass.reserved_places.toString())
            } else {
                day_of_week_spinner.setSelection(0)
            }

            initTimePicker(this)
            if (!mIsLargeLayout) {
                cancel_button.visibility = View.GONE
                ok_button.visibility = View.GONE
            } else {
                cancel_button.setOnClickListener {
                    //close dialog without any save
                    dismiss()
                }
                ok_button.setOnClickListener {
                    actionDoneListener(this)
                }
            }
        }

        viewAdapter = ChildrenAdapter(mChildren)
        with(rootView.recycler_view_children) {
            layoutManager = LinearLayoutManager(activity)
            adapter = viewAdapter
        }
        return rootView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d(TAG, "SetClassDialogFragment: onCreateDialog()")
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        return super.onCreateDialog(savedInstanceState)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "SetClassDialogFragment: onOptionsItemSelected()")
        return when (item.itemId) {
            R.id.action_done -> {
                val error=actionDoneListener(view!!)
                if(!error){
                    fragmentManager?.popBackStack()
                }
                true
            }
            android.R.id.home -> {
                fragmentManager?.popBackStack()
                true
            }
            else -> true
        }
    }

    override fun onResume() {
        if (mIsLargeLayout) {
            val width = resources.displayMetrics.widthPixels / 2
            val height = dialog.window.attributes.height
            dialog.window!!.setLayout(width, height)
        }
        super.onResume()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    interface OnClassFragmentFinishListener {
        fun onClassFragmentFinish(clazz: Class, position: Int)
    }

    private fun downloadChildren() {
        mClass.children.forEach { reference ->
            getDatabase().document(reference)
                    .get()
                    .addOnSuccessListener {
                        it.toObject(Child::class.java)?.let { child ->
                            mChildren.add(child)
                            viewAdapter.notifyItemInserted(mChildren.indexOf(child))
                        }
                    }
        }
    }

    private fun initTimePicker(view: View) {
        with(mClass.classTime) {
            if (isAdding()) {
                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                startClassHour = hour
                endClassHour = hour
                startClassMinute = minute
                endClassMinute = minute
            }
            view.time_btn.setOnClickListener { button ->
                button as Button
                //button is clicked, call first time dialog
                TimePickerDialog(activity, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    //first time dialog is closed
                    startClassHour = hourOfDay
                    startClassMinute = minute
                    button.text = "${timeTemplate.format(startClassHour, startClassMinute)} - ${timeTemplate.format(endClassHour, endClassMinute)}"

                    //Call second time dialog
                    TimePickerDialog(activity, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                        //second time dialog is closed
                        endClassHour = hourOfDay
                        endClassMinute = minute
                        button.text = "${timeTemplate.format(startClassHour, startClassMinute)} - ${timeTemplate.format(endClassHour, endClassMinute)}"

                    }, endClassHour, endClassMinute, true).apply {
                        setCancelable(false)
                        activity?.showToast("Выберите время конца")
                        show()
                    }

                }, startClassHour, startClassMinute, true).apply {
                    activity?.showToast("Выберите время начала")
                    setCancelable(false)
                    show()
                }
            }
            view.time_btn.text = "${timeTemplate.format(startClassHour, startClassMinute)} - ${timeTemplate.format(endClassHour, endClassMinute)}"
        }
    }

    private fun isAdding() = mPosition == -1

    companion object {
        private const val ARG_ITEM = "class"
        private const val ARG_ITEM_POSITION = "position"

        @JvmStatic
        fun newInstance(clazz: Class, position: Int) =
                SetClassDialogFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_ITEM, clazz)
                        putInt(ARG_ITEM_POSITION, position)
                    }
                }
    }
}
