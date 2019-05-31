package com.piotrek1543.example.todoapp.ui.taskdetail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.piotrek1543.example.todoapp.R
import com.piotrek1543.example.todoapp.ui.statistics.StatisticsFragment
import com.piotrek1543.example.todoapp.ui.util.addFragmentToActivity

/**
 * Displays task details screen.
 */
class TaskDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.taskdetail_act)

        // Set up the toolbar.
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // Get the requested task id
        val taskId = intent.getStringExtra(EXTRA_TASK_ID)

        supportFragmentManager.findFragmentById(R.id.contentFrame) as StatisticsFragment?
                ?: TaskDetailFragment(taskId).also {
                    addFragmentToActivity(it, R.id.contentFrame)
                }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_TASK_ID = "TASK_ID"
    }
}
