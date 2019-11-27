//package company.sukiasyan.happymind.views.activities
//
//import android.content.Intent
//import android.os.Bundle
//import android.support.design.widget.FloatingActionButton
//import android.support.v7.widget.LinearLayoutManager
//import android.support.v7.widget.RecyclerView
//import android.util.Log
//import android.view.Menu
//import android.view.MenuItem
//import android.view.View
//import android.widget.EditText
//import company.sukiasyan.happymind.R
//import company.sukiasyan.happymind.fragments.AgeGroupDetailFragment
//import company.sukiasyan.happymind.models.Course.AgeGroup
//import company.sukiasyan.happymind.utils.*
//import company.sukiasyan.happymind.utils.interfaces.OnAgeGroupInteractionListener
//import company.sukiasyan.happymind.utils.interfaces.OnSetCourseActivityInteractionListener
//import company.sukiasyan.happymind.views.adapters.AgeGroupAdapter
//import kotlinx.android.synthetic.main.activity_agegroup_list.*
//import kotlinx.android.synthetic.main.agegroup_list.*
//import kotlinx.android.synthetic.main.toolbar.*
//
///**
// * An activity representing a list of Pings. This activity
// * has different presentations for handset and tablet-size devices. On
// * handsets, the activity presents a list of items, which when touched,
// * lead to a [AgeGroupDetailActivity] representing
// * item details. On tablets, the activity presents the list of items and
// * item details side-by-side using two vertical panes.
// */
//class SetCourseActivitySecond : SetCourseBaseActivity(), OnAgeGroupInteractionListener {
//    /**
//     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
//     * device.
//     */
//    private var twoPane: Boolean = false
//    private var listener: OnSetCourseActivityInteractionListener? = null
//
//    private val clickListener: (View) -> Unit = {
//        showAgeGroupDetail(it)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_agegroup_list)
//
//        setSupportActionBar(toolbar)
//        toolbar.title = "Возрастные группы"
//
//        fab.setOnClickListener { clickListener(it) }
////        fab.setGravity()
//
//        // Show the Up button in the action bar.
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//
//        if (agegroup_detail_container != null) {
//            // The detail container view will be present only in the
//            // large-screen layouts (res/values-w900dp).
//            // If this view is present, then the
//            // activity should be in two-pane mode.
//            twoPane = true
//        }
//
//        course = intent!!.getParcelableExtra(EXTRA_COURSE)
//        initLocalIdClasses()
//        oldCourse = course.copy()
//        bundle = intent!!.getBundleExtra(EXTRA_BUNDLE)
//        mPosition = intent!!.getIntExtra(ARG_ITEM_POSITION, -1)
//
//        setupRecyclerView(agegroup_list)
//    }
//
//    private fun initLocalIdClasses() {
//        //делается для того, чтобы после изменений найти нужный класс для изменения
//        //TODO конечно, лучше генерировать для каждой группы(класса) свой id (в идеале делается через CloudFunction)
//        var count = 0
////        course.ageGroups.forEach {
////            it.classes.forEach {
////                it.id = count++
////            }
////        }
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem) =
//            when (item.itemId) {
//                android.R.id.home -> {
//                    // This ID represents the Home or Up button. In the case of this
//                    // activity, the Up button is shown. Use NavUtils to allow users
//                    // to navigate up one level in the application structure. For
//                    // more details, see the Navigation pattern on Android Design:
//                    //
//                    // http://developer.android.com/design/patterns/navigation.html#up-vs-back
//                    setResult(-1, Intent().putExtra(EXTRA_COURSE, course))
//                    finish()
//                    true
//                }
//                R.id.action_done -> {
//                    listener?.let {
//                        val error = it.onAgeGroupSaveListenerFragment()
//                        if (error) return true
//                    }
//                    uploadCourse()
//                    startActivity(Intent(this, CoursesActivity::class.java))
//                    finish()
//                    true
//                }
//                else -> super.onOptionsItemSelected(item)
//            }
//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_set_course_second, menu)
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        when (requestCode) {
//            AgeGroupDetailActivity.REQUEST_AGE_GROUP -> {
//                data?.let {
//                    val ageGroup = it.getParcelableExtra<AgeGroup>(EXTRA_AGE_GROUP)
//                    val position = it.getIntExtra(ARG_ITEM_POSITION, -1)
//                    onAgeGroupSaveListenerActivity(ageGroup, position)
//                }
//            }
//        }
//    }
//
//    override fun onAgeGroupSaveListenerActivity(ageGroup: AgeGroup, position: Int) {
//        //fragment is destroying=>save ageGroup
//        if (position == -1) {
//            course.ageGroups.add(ageGroup)
//            agegroup_list.adapter?.notifyItemInserted(course.ageGroups.size - 1)
//            removeCurrentDialog()
//        } else {
//            course.ageGroups[position] = ageGroup
//            agegroup_list.adapter?.notifyItemChanged(position)
//        }
//    }
//
//    override fun onAgeGroupIntersectionValidation(newAgeGroup: AgeGroup, oldAgeGroup: AgeGroup, minAgeEdit: EditText): Boolean {
//        val error = course.ageGroups.any { it.minAge <= newAgeGroup.maxAge && it.maxAge >= newAgeGroup.minAge && it != oldAgeGroup }
//        if (error) {
//            minAgeEdit.requestFocus()
//            minAgeEdit.error = "Возрастные группы не должны пересекаться!"
//        }
//        return error
//    }
//
//
//    private fun setupRecyclerView(recyclerView: RecyclerView) {
//        with(recyclerView) {
//            setHasFixedSize(true)
//            layoutManager = LinearLayoutManager(this@SetCourseActivitySecond)
//            adapter = AgeGroupAdapter(course.ageGroups, clickListener) { position ->
//                //delete button clickListener
//                //remove detail fragment according to deleting age group
//                if(!course.ageGroups[position].classes.any { it.children.size!=0 }){
//                    removeCurrentDialog()
//                    course.ageGroups.removeAt(position)
//                    adapter?.notifyItemRemoved(position)
//                }
//                else{
//                    showToast("Невозможно удалить возрастную группу, пока в нее записаны дети!")
//                }
//
//            }
//        }
//    }
//
//    private fun showAgeGroupDetail(view: View) {
//        val isAdding = view is FloatingActionButton
//        var ageGroup = AgeGroup()
//        var position = -1
//
//        if (!isAdding) {
//            position = view.tag as Int
//            ageGroup = course.ageGroups[position]
//        }
//        if (twoPane) {
//            listener?.let {
//                val error = it.onAgeGroupSaveListenerFragment()
//                if (error) return
//            }
//            val fragment = AgeGroupDetailFragment.newInstance(ageGroup.copy(), position)
//            listener = fragment
//            supportFragmentManager
//                    .beginTransaction()
//                    .replace(R.id.agegroup_detail_container, fragment)
//                    .commit()
//        } else {
//            val intent = Intent(this, AgeGroupDetailActivity::class.java).apply {
//                putExtra(AgeGroupDetailFragment.ARG_ITEM, ageGroup.copy())
//                putExtra(ARG_ITEM_POSITION, position)
//                putExtra(EXTRA_COURSE, course.copy())
//            }
//            startActivityForResult(intent, AgeGroupDetailActivity.REQUEST_AGE_GROUP)
//        }
//    }
//
//    private fun removeCurrentDialog() {
//        supportFragmentManager.findFragmentById(R.id.agegroup_detail_container)?.let {
//            supportFragmentManager
//                    .beginTransaction()
//                    .remove(it)
//                    .commitNow()
//        }
//        listener=null
//    }
//
//}
