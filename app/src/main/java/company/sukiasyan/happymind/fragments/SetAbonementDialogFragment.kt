package company.sukiasyan.happymind.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.util.Log
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.models.Course.AgeGroup.Abonement
import company.sukiasyan.happymind.utils.TAG
import kotlinx.android.synthetic.main.fragment_set_abonement_dialog.view.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SetAbonementDialogFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SetAbonementDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class SetAbonementDialogFragment : DialogFragment() {

    private lateinit var mAbonement: Abonement
    private var mPosition: Int = -1

    private var listener: OnAbonementFragmentFinishListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (targetFragment is OnAbonementFragmentFinishListener) {
            listener = targetFragment as OnAbonementFragmentFinishListener
        } else {
            throw RuntimeException(context.toString() + " must implement OnAbonementFragmentFinishListener")
        }

        mAbonement = arguments!!.getParcelable(ARG_ITEM)
        mPosition = arguments!!.getInt(ARG_ITEM_POSITION)
    }

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        Log.d(TAG, "SetAbonementDialogFragment: onCreateView()")
//
//
//        return view
//    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d(TAG, "SetAbonementDialogFragment: onCreateDialog()")
        val view = activity?.layoutInflater?.inflate(R.layout.fragment_set_abonement_dialog, null)!!
        view.count_edit.setText(mAbonement.countOfClasses.toString())
        view.price_edit.setText(mAbonement.price.toString())
        return AlertDialog.Builder(activity)
                .setView(view)
                .setTitle("Абонемент")
                .setPositiveButton("OK") { _, _ ->
                    mAbonement.countOfClasses = view.count_edit.text.toString().toInt()
                    mAbonement.price = view.price_edit.text.toString().toInt()
                    listener?.onAbonementFragmentFinish(mAbonement, mPosition)
                    dismiss()
                }
                .setNegativeButton("Отмена") { _, _ ->
                    dismiss()
                }
                .create()
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
    interface OnAbonementFragmentFinishListener {
        fun onAbonementFragmentFinish(abonement: Abonement, position: Int)
    }

    companion object {
        private const val ARG_ITEM = "abonement"
        private const val ARG_ITEM_POSITION = "position"

        @JvmStatic
        fun newInstance(abonement: Abonement, position: Int) =
                SetAbonementDialogFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_ITEM, abonement)
                        putInt(ARG_ITEM_POSITION, position)
                    }
                }
    }

    private fun isAdd() = mPosition == -1
}
