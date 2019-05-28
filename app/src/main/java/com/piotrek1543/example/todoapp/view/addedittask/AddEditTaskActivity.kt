package com.piotrek1543.example.todoapp.view.addedittask

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.piotrek1543.example.todoapp.R
import com.piotrek1543.example.todoapp.view.util.replaceFragmentInActivity
import com.piotrek1543.example.todoapp.view.util.setupActionBar

class AddEditTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addtask_act)

        // Set up the toolbar.
        setupActionBar(R.id.toolbar) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        supportFragmentManager.findFragmentById(R.id.contentFrame) as AddEditTaskFragment?
                ?: AddEditTaskFragment().also {
                    replaceFragmentInActivity(it, R.id.contentFrame)
                }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}