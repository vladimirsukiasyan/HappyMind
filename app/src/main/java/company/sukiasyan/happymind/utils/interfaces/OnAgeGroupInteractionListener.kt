package company.sukiasyan.happymind.utils.interfaces

import android.widget.EditText
import company.sukiasyan.happymind.models.Course

interface OnAgeGroupInteractionListener {
    fun onAgeGroupSaveListenerActivity(ageGroup: Course.AgeGroup, position: Int)
    fun onAgeGroupIntersectionValidation(newAgeGroup: Course.AgeGroup, oldAgeGroup: Course.AgeGroup, minAgeEdit: EditText): Boolean
}