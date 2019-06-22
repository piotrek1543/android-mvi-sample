package com.piotrek1543.example.todoapp.ui.addedittask

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.piotrek1543.example.todoapp.R
import com.piotrek1543.example.todoapp.ui.util.replaceFragmentInActivity
import com.piotrek1543.example.todoapp.ui.util.setupActionBar

class AddEditTaskActivity : AppCompatActivity() {

    private lateinit var actionBar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addtask_act)

        // Set up the toolbar.
        setupActionBar(R.id.toolbar) {
            actionBar = this
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        supportFragmentManager.findFragmentById(R.id.contentFrame) as AddEditTaskFragment?
                ?: AddEditTaskFragment().also {
                    replaceFragmentInActivity(it, R.id.contentFrame)
                }

        val taskId = intent.getStringExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID)
        setToolbarTitle(taskId)

        supportFragmentManager.findFragmentById(R.id.contentFrame) as AddEditTaskFragment?
                ?: AddEditTaskFragment.invoke().apply {
                    if (taskId != null) {
                        val args = Bundle()
                        args.putString(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId)
                        arguments = args
                    }
                }.also {
                    replaceFragmentInActivity(it, R.id.contentFrame)
                }
    }

    private fun setToolbarTitle(taskId: String?) {
        actionBar.setTitle(if (taskId == null) R.string.add_task else R.string.edit_task)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val REQUEST_ADD_TASK = 1
    }
}
