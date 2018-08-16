package company.sukiasyan.happymind.views.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.models.Child.ChildCourse.ChildAbonement
import company.sukiasyan.happymind.utils.showToast
import company.sukiasyan.happymind.utils.updateChildCourse
import company.sukiasyan.happymind.views.activities.CoursesDetailActivity.Companion.childCourse
import company.sukiasyan.happymind.views.activities.CoursesDetailActivity.Companion.courseAgeGroup
import company.sukiasyan.happymind.views.adapters.SubscriptionChildAdapter
import kotlinx.android.synthetic.main.content_pay_subscription.*
import kotlinx.android.synthetic.main.toolbar.*

class PaySubscriptionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_subscription)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val indexCourse = intent.extras.getInt("indexCourse")

        val viewAdapter = SubscriptionChildAdapter(courseAgeGroup.abonements) {
            //TODO пока заказчик не решит вопрос с оплатой, добавление абонемента будет происходить без оплаты
            childCourse.abonement = ChildAbonement(it.countOfClasses, it.countOfClasses, it.price)
            //загрузка обновлений
            updateChildCourse(indexCourse)
            //переход в расписание
            startActivity(Intent(this, ScheduleActivity::class.java))
            showToast("Оплата проведена успешно. Ваше расписание обновлено!")
        }
        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@PaySubscriptionActivity)
            adapter = viewAdapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
