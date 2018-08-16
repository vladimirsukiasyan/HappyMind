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
import company.sukiasyan.happymind.utils.TAG
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
class AgeGroupDetailFragment : Fragment(), SetClassDialogFragment.OnClassFragmentFinishListener, SetAbonementDialogFragment.OnAbonementFragmentFinishListener {
    private lateinit var ageGroup: AgeGroup
    private var mIsLargeLayout: Boolean = false
    private var mPosition: Int = -1

    private var listener: OnAgeGroupFinishListener? = null

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
        //TODO добавить подсказки TextView instead hint text

        with(rootView) {
            minAge.setText(ageGroup.minAge.toString())
            maxAge.setText(ageGroup.maxAge.toString())
            duration.setText(ageGroup.duration.toString())
            if (isAdding() && mIsLargeLayout) {
                add_btn.visibility = View.VISIBLE
            }
            add_class_btn.setOnClickListener { classClickListener(it) }
            add_abonement_btn.setOnClickListener { abonementClickListener(it) }
            add_btn.setOnClickListener {
                setAllProperty()
                listener?.onAgeGroupSaveListener(ageGroup, mPosition)

            }
        }

        //TODO надо добавить placeholder text( is VISIBLE) in layout when list is empty => recycler view is GONE, если нет recycler view is VISIBILITY, placeholder is GONE
        rootView.recycler_view_classes.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ClassAdapter(ageGroup.classes, classClickListener) { position ->
                //delete click listener
                Log.d(TAG, "AgeGroupDetailFragment: deleting position=$position , class=${ageGroup.classes[position]}")
                ageGroup.classes.removeAt(position)
                adapter?.notifyItemRemoved(position)
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
                setAllProperty()
                listener?.onAgeGroupSaveListener(ageGroup, mPosition)
                fragmentManager
                        ?.beginTransaction()
                        ?.remove(this)
                        ?.commitNow()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        //save only if ageGroup is edditing
        if (!isAdding() && !classFragmentFlag) {
            setAllProperty()
            listener?.onAgeGroupSaveListener(ageGroup, mPosition)
        }
        super.onDestroyView()
    }

    interface OnAgeGroupFinishListener {
        fun onAgeGroupSaveListener(ageGroup: AgeGroup, position: Int)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnAgeGroupFinishListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnAgeGroupFinishListener")
        }
    }

    private fun setAllProperty() {
        //TODO сделать validate проверку и проверить не пересекаются ли возрастные группы
        ageGroup.minAge = view?.minAge?.text.toString().toInt()
        ageGroup.maxAge = view?.maxAge?.text.toString().toInt()
        ageGroup.duration = view?.duration?.text.toString().toInt()
    }

    //TODO определять по boolean флагу
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
        const val ARG_ITEM_POSITION = "position"
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
