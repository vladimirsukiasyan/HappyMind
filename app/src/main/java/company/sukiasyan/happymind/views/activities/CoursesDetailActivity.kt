//package company.sukiasyan.happymind.views.activities
//
//import android.content.Intent
//import android.os.Build
//import android.os.Bundle
//import android.support.design.widget.TabLayout
//import android.support.v7.app.AppCompatActivity
//import android.util.Log
//import android.view.MenuItem
//import com.bumptech.glide.Glide
//import com.bumptech.glide.signature.StringSignature
//import com.firebase.ui.storage.images.FirebaseImageLoader
//import company.sukiasyan.happymind.R
//import company.sukiasyan.happymind.models.Child.ChildCourse
//import company.sukiasyan.happymind.models.Course
//import company.sukiasyan.happymind.utils.*
//import company.sukiasyan.happymind.views.adapters.CourseDetailPagerAdapter
//import kotlinx.android.synthetic.main.activity_course_detail.*
//
//class CoursesDetailActivity : AppCompatActivity() {
//
//    private var indexCourse = -1
//
//    private lateinit var mPagerAdapter: CourseDetailPagerAdapter
//
//    companion object {
//        lateinit var course: Course
//        lateinit var courseAgeGroup: Course.AgeGroup
//        lateinit var childCourse: ChildCourse
//    }
//
//
//    fun initProperty() {
//        //get course from Intent
//        course = intent.extras.get(EXTRA_COURSE) as Course
//
//        //get copy info about this course according to active child
//        //TODO ЗАПОМНИ!!!КОТЛИН ВЫПОЛНЯЕТ КОПИРОВАНИЕ copy() ТОЛЬКО ДЛЯ ПРИМИТИВНЫХ ОБЪЕКТОВ. ЕСЛИ ЕСТЬ ВНУТРИ НЕ ПРИМИТИВНЫЕ ОБЪЕКТЫ(классы, списки и т.д),
//        //TODO ТО НУЖНО ПЕРЕОПРЕДЕЛИТЬ МЕТОД copy() ТАК, ЧТОБЫ ВНУТРИ ТОГО ОБЪЕКТА,КОТОРЫЙ КОПИРУЕТСЯ БЫЛИ ТОЛЬКО ПРИМИТИВНЫЕ
//
////        childCourse = activeChild.courses.find { it.id == course.id }?.copy() ?: ChildCourse(name = course.name, id = course.id)
////
////        courseAgeGroup = getCourseAgeGroup()
////        indexCourse = activeChild.courses.indexOf(childCourse)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_course_detail)
//        Log.d(TAG, "onCreate: CoursesDetailActivity ")
//
//        initProperty()
//
//        //setup toolbar
//        setSupportActionBar(toolbar_collapse)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//
//        //init ViewPager with TabLayout
//        mPagerAdapter = CourseDetailPagerAdapter(supportFragmentManager)
//        container.adapter = mPagerAdapter
//
//        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tab_layout))
//        tab_layout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
//
//
//        fab.setOnClickListener {
//            //Если родитель добавляет курс или еще одно занятие (а на балансе у него 0 занятий), то отправляем на оплату
//            if (childCourse.abonement.balance == 0) {
//                val intent = Intent(this, PaySubscriptionActivity::class.java)
//                intent.putExtra("indexCourse", indexCourse)
//                startActivity(intent)
//            } else {
////                updateChildCourse(indexCourse)
////                startActivity(Intent(this, ScheduleActivity::class.java))
////                finish()
//            }
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            item_image.transitionName = course.id
//        }
//        item_rating.rating = course.rating
//
//        val reference = getStorage().reference.child("courses/${getUserBranch()}/${course.id}/${course.id}.jpg")
//        Glide.with(this)
//                .using(FirebaseImageLoader())
//                .load(reference)
//                .signature(StringSignature(course.photo_uri))
//                .error(R.drawable.item_placeholder)
//                .placeholder(R.drawable.item_placeholder)
//                .into(item_image)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> {
//                supportFinishAfterTransition()
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }
//}
