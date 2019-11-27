package company.sukiasyan.happymind.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.models.Course.AgeGroup
import company.sukiasyan.happymind.models.Course.AgeGroup.Abonement
import company.sukiasyan.happymind.models.Course.AgeGroup.Class
import company.sukiasyan.happymind.utils.ARG_ITEM_POSITION
import company.sukiasyan.happymind.utils.TAG
import company.sukiasyan.happymind.utils.interfaces.OnAgeGroupInteractionListener
import company.sukiasyan.happymind.utils.interfaces.OnSetCourseActivityInteractionListener
import company.sukiasyan.happymind.utils.showToast
import company.sukiasyan.happymind.utils.validateEditView
import company.sukiasyan.happymind.views.adapters.ClassAdapter
import company.sukiasyan.happymind.views.adapters.SubscriptionAdapter
import kotlinx.android.synthetic.main.agegroup_detail.view.*
import kotlinx.android.synthetic.main.toolbar.*
import java.lang.RuntimeException

/**
 * A fragment representing a single AgeGroup detail screen.
 * This fragment is either contained in a [SetCourseActivitySecond]
 * in two-pane mode (on tablets) or a [AgeGroupDetailActivity]
 * on handsets.
 */
class AgeGroupDetailFragment : Fragment(), SetClassDialogFragment.OnClassFragmentFinishListener, SetAbonementDialogFragment.OnAbonementFragmentFinishListener, OnSetCourseActivityInteractionListener {
    private lateinit var ageGroup: AgeGroup
    private lateinit var oldAgeGroup: AgeGroup
    private var mIsLargeLayout: Boolean = false
    private var mPosition: Int = -1

    private var listener: OnAgeGroupInteractionListener? = null

    private val classClickListener: (View) -> Unit = {
        showClassDialog(it)
    }
    private var classFragmentFlag = false
    private val abonementClickListener: (View) -> Unit = {
        showAbonementDialog(it)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        ageGroup = arguments!!.getParcelable(ARG_ITEM)
        oldAgeGroup = ageGroup.copy()
        mPosition = arguments!!.getInt(ARG_ITEM_POSITION)

        if (isAdding()) {
            activity?.toolbar?.title = "Добавление группы"
        } else {
            activity?.toolbar?.title = "${ageGroup.minAge}-${ageGroup.maxAge} лет"
        }

        mIsLargeLayout = resources.getBoolean(R.bool.large_layout)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.agegroup_detail, container, false)

        with(rootView) {
            if (!isAdding()) {
                minAge.setText(ageGroup.minAge.toString())
                maxAge.setText(ageGroup.maxAge.toString())
                duration.setText(ageGroup.duration.toString())
            }
            if (isAdding() && mIsLargeLayout) {
                add_btn.visibility = View.VISIBLE
            }
            add_class_btn.setOnClickListener { classClickListener(it) }
            add_abonement_btn.setOnClickListener { abonementClickListener(it) }
            add_btn.setOnClickListener {
                val error = setAndValidationAllProperty()
                if (!error) {
                    listener?.onAgeGroupSaveListenerActivity(ageGroup, mPosition)
                }
            }
        }

        //TODO надо добавить placeholder text( is VISIBLE) in layout when list is empty => recycler view is GONE, если нет recycler view is VISIBILITY, placeholder is GONE
        rootView.recycler_view_classes.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ClassAdapter(ageGroup.classes, classClickListener) { position ->
                if(ageGroup.classes[position].children.size==0){
                    //delete click listener
                    Log.d(TAG, "AgeGroupDetailFragment: deleting position=$position , class=${ageGroup.classes[position]}")
                    ageGroup.classes.removeAt(position)
                    adapter?.notifyItemRemoved(position)
                }
                else{
                    activity?.showToast("Невозможно удалить группу, пока в нее записаны дети!")
                }

            }
        }

        rootView.recycler_view_abonements.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = SubscriptionAdapter(ageGroup.abonements, abonementClickListener) {
                val position = it.tag as Int
                Log.d(TAG, "AgeGroupDetailFragment: deleting position=$position , abonement=${ageGroup.abonements[position]}")
                ageGroup.abonements.removeAt(position)
                adapter?.notifyItemRemoved(position)
            }
        }

        return rootView
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "AgeGroupDetailFragment: onOptionsItemSelected()")
        when (item.itemId) {
            R.id.action_done -> {
                //call on only handset
                val error = setAndValidationAllProperty()
                if (!error) {
                    listener?.onAgeGroupSaveListenerActivity(ageGroup, mPosition)
                    fragmentManager
                            ?.beginTransaction()
                            ?.remove(this)
                            ?.commitNow()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnAgeGroupInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnAgeGroupInteractionListener")
        }
    }

    override fun onAgeGroupSaveListenerFragment(): Boolean {
        //save only if ageGroup is editing
        return if (isAdding()) false
        else {
            val error = setAndValidationAllProperty()
            if (mIsLargeLayout && !error) {
                listener?.onAgeGroupSaveListenerActivity(ageGroup, mPosition)
                false
            } else true
        }
    }

    private fun setAndValidationAllProperty(): Boolean {
        view!!.let {
            val errorEmptyEditText = validateEditView(it.minAge, it.maxAge, it.duration)
            return if (!errorEmptyEditText) {
                ageGroup.minAge = it.minAge.text.toString().toInt()
                ageGroup.maxAge = it.maxAge.text.toString().toInt()
                if (ageGroup.minAge > ageGroup.maxAge) {
                    it.minAge.requestFocus()
                    it.minAge.error = "Минимальный возраст должен быть не больше чем максимальный"
                    return true
                }
                ageGroup.duration = it.duration.text.toString().toInt()
                listener!!.onAgeGroupIntersectionValidation(ageGroup, oldAgeGroup, it.minAge)
            } else {
                true
            }
        }
    }


    private fun isAdding() = mPosition == -1

    override fun onClassFragmentFinish(clazz: Class, position: Int) {
        Log.d(TAG, "AgeGroupDetailFragment: onClassFragmentFinish()")
        if (position == -1) {
            ageGroup.classes.add(clazz)
            view?.recycler_view_classes?.adapter?.notifyItemInserted(ageGroup.classes.size - 1)
        } else {
            ageGroup.classes[position] = clazz
            view?.recycler_view_classes?.adapter?.notifyItemChanged(position)
        }
        Log.d(TAG, "AgeGroupDetailFragment: onClassFragmentFinish() ageGroup=${ageGroup.classes}")
        classFragmentFlag = false
    }

    override fun onAbonementFragmentFinish(abonement: Abonement, position: Int) {
        Log.d(TAG, "AgeGroupDetailFragment: onAbonementFragmentFinish()")
        if (position == -1) {
            ageGroup.abonements.add(abonement)
            view?.recycler_view_abonements?.adapter?.notifyItemInserted(ageGroup.abonements.size - 1)
        } else {
            ageGroup.abonements[position] = abonement
            view?.recycler_view_abonements?.adapter?.notifyItemChanged(position)
        }
    }

    private fun showClassDialog(view: View) {
        val isAdding = view is Button
        var clazz = Class()
        var position = -1

        if (!isAdding) {
            position = view.tag as Int
            clazz = ageGroup.classes[position]
        }

        val classDialogFragment = SetClassDialogFragment.newInstance(clazz.copy(), position)
        classDialogFragment.setTargetFragment(this, 300)

        if (mIsLargeLayout) {
            // The device is using a large layout, so show the fragment as a dialog
            //call onCreate() in SetClassDialogFragment
            classDialogFragment.show(fragmentManager, "class_dialog")
        } else {
            // The device is smaller, so show the fragment fullscreen
            // To make it fullscreen, use the 'content' root view as the container
            // for the fragment, which is always the root view for the activity
            //call onCreateView() in SetClassDialogFragment
            classFragmentFlag = true
            fragmentManager?.let {
                it.beginTransaction()
                        .replace(R.id.agegroup_detail_container, classDialogFragment, "class_dialog")
                        .addToBackStack(null)
                        .commit()
            }

        }
    }

    private fun showAbonementDialog(view: View) {
        val isAdding = view is Button
        var abonement = Abonement()
        var position = -1

        if (!isAdding) {
            position = view.tag as Int
            abonement = ageGroup.abonements[position]
        }

        val abonementDialogFragment = SetAbonementDialogFragment.newInstance(abonement, position)
        abonementDialogFragment.setTargetFragment(this, 100)
        abonementDialogFragment.show(fragmentManager, "abonement_dialog")

    }

    companion object {
        const val ARG_ITEM = "ageGroup"
        @JvmStatic
        fun newInstance(ageGroup: AgeGroup, position: Int): AgeGroupDetailFragment =
                AgeGroupDetailFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_ITEM, ageGroup)
                        putInt(ARG_ITEM_POSITION, position)
                    }
                }
    }
}
