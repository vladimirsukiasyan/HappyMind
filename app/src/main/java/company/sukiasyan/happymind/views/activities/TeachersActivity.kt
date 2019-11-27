package company.sukiasyan.happymind.views.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import company.sukiasyan.happymind.R
import company.sukiasyan.happymind.models.Teacher
import company.sukiasyan.happymind.utils.ARG_ITEM_POSITION
import company.sukiasyan.happymind.utils.EXTRA_TEACHER
import company.sukiasyan.happymind.utils.TAG
import company.sukiasyan.happymind.utils.getDatabase
import company.sukiasyan.happymind.views.adapters.TeacherAdapter
import kotlinx.android.synthetic.main.activity_teachers.*
import kotlinx.android.synthetic.main.content_teachers.*
import kotlinx.android.synthetic.main.toolbar.*

class TeachersActivity : AppCompatActivity() {
    //THIS ACTIVITY ONLY FOR ADMIN
    private var teachers: MutableList<Teacher> = mutableListOf()
    private lateinit var viewAdapter: TeacherAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teachers)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        downloadTeachers()
        setViewAdapter()

        fab.setOnClickListener {
            val intent = Intent(this, TeacherDetailActivity::class.java)
            startActivity(intent)
        }
    }

    private fun downloadTeachers() {
        getDatabase().collection("teachers")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    firebaseFirestoreException?.let {
                        Log.d(TAG, "TeachersActivity: downloadTeachers(): Listen failed.", firebaseFirestoreException)
                        return@addSnapshotListener
                    }
                    querySnapshot?.let {
                        teachers.clear()
                        teachers.addAll(it.toObjects(Teacher::class.java))
                        Log.d(TAG, "Teachers have been downloaded: teachers=$teachers")
                        reInitContentUI()
                    }
                }
    }

    private fun reInitContentUI() {
        viewAdapter.notifyDataSetChanged()
    }

    private fun setViewAdapter() {
        viewAdapter = TeacherAdapter(teachers,
                clickListener = { position ->
                    val intent = Intent(this, TeacherDetailActivity::class.java).apply {
                        putExtra(ARG_ITEM_POSITION, position)
                        putExtra(EXTRA_TEACHER, teachers[position])
                    }
                    startActivity(intent)
                },
                deleteListener = {
                    //TODO УДАЛЕНИЕ АККАУНТА
                    teachers.removeAt(it)
                    viewAdapter.notifyItemRemoved(it)
                }
        )

        recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@TeachersActivity)
            adapter = viewAdapter
            addItemDecoration(DividerItemDecoration(this@TeachersActivity, DividerItemDecoration.HORIZONTAL))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                android.R.id.home -> {
                    finish()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
}