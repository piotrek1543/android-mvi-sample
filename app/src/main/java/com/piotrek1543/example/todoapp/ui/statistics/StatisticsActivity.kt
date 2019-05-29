package com.piotrek1543.example.todoapp.ui.statistics

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.piotrek1543.example.todoapp.R
import com.piotrek1543.example.todoapp.ui.util.replaceFragmentInActivity
import com.piotrek1543.example.todoapp.ui.util.setupActionBar

/**
 * Activity houses the Toolbar, the nav UI, the FAB and the fragment for stats.
 */
class StatisticsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statistics_act)

        // Set up the toolbar.
        setupActionBar(R.id.toolbar) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        supportFragmentManager.findFragmentById(R.id.contentFrame) as StatisticsFragment?
                ?: StatisticsFragment().also {
                    replaceFragmentInActivity(it, R.id.contentFrame)
                }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}