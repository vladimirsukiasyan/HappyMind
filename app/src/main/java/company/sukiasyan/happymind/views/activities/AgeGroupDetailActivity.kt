package company.sukiasyan.happymind.views.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.fragments.AgeGroupDetailFragment
import company.sukiasyan.happymind.fragments.AgeGroupDetailFragment.Companion.ARG_ITEM
import company.sukiasyan.happymind.models.Course
import company.sukiasyan.happymind.models.Course.AgeGroup
import company.sukiasyan.happymind.utils.ARG_ITEM_POSITION
import company.sukiasyan.happymind.utils.EXTRA_AGE_GROUP
import company.sukiasyan.happymind.utils.EXTRA_COURSE
import company.sukiasyan.happymind.utils.TAG
import company.sukiasyan.happymind.utils.interfaces.OnAgeGroupInteractionListener
import kotlinx.android.synthetic.main.toolbar.*
import java.lang.IllegalArgumentException

/**
 * An activity representing a single AgeGroup detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [SetCourseActivitySecond].
 */
class AgeGroupDetailActivity : AppCompatActivity(), OnAgeGroupInteractionListener {
    private var course = Course()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agegroup_detail)
        setSupportActionBar(toolbar)

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            val fragment = AgeGroupDetailFragment.newInstance(intent.getParcelableExtra(ARG_ITEM), intent.getIntExtra(ARG_ITEM_POSITION, -1))

            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.agegroup_detail_container, fragment, "age_group_fragment")
                    .commit()
        }
        course = intent.getParcelableExtra(EXTRA_COURSE)
    }

    override fun onOptionsItemSelected(item: MenuItem)=
        when (item.itemId) {
            android.R.id.home -> {
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                val fragment = supportFragmentManager.findFragmentById(R.id.agegroup_detail_container)
                //call onOptionItemSelected()

                when(fragment.tag ){
                    "age_group_fragment"-> {
                        finish()
                        true
                    }
                    "class_dialog"-> {
                        fragment.onOptionsItemSelected(item)
                    }
                    else->throw IllegalArgumentException("AgeGroupDetailActivity doesn't contain any fragment(ClassDialog or AgeGroupDetail. Maybe tag is wrong also!")
                }
            }
            R.id.action_done -> {
                Log.d(TAG, "AgeGroupDetailActivity: onOptionsItemSelected()")

                val fragment = supportFragmentManager.findFragmentById(R.id.agegroup_detail_container)
                //call onOptionItemSelected()
                fragment.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_set_course_second, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onAgeGroupSaveListenerActivity(ageGroup: AgeGroup, position: Int) {
        setResult(-1, Intent().apply {
            putExtra(EXTRA_AGE_GROUP, ageGroup)
            putExtra(ARG_ITEM_POSITION, position)
        })
        finish()
    }

    override fun onAgeGroupIntersectionValidation(newAgeGroup: AgeGroup, oldAgeGroup: AgeGroup, minAgeEdit: EditText): Boolean {
        val error = course.ageGroups.any { it.minAge <= newAgeGroup.maxAge && it.maxAge >= newAgeGroup.minAge && it != oldAgeGroup }
        if (error) {
            minAgeEdit.requestFocus()
            minAgeEdit.error = "Возрастные группы не должны пересекаться!"
        }
        return error
    }


    companion object {
        const val REQUEST_AGE_GROUP = 200
    }
}
