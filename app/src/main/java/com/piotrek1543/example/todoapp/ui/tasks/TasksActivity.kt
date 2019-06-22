package com.piotrek1543.example.todoapp.ui.tasks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.piotrek1543.example.todoapp.R

/**
 * Tasks Activity houses the Toolbar, the nav UI, the FAB and the fragment holding the tasks list.
 */
class TasksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasks_act)

    }

}