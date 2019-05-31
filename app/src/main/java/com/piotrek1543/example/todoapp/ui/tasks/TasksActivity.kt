package com.piotrek1543.example.todoapp.ui.tasks

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.piotrek1543.example.todoapp.R
import com.piotrek1543.example.todoapp.ui.statistics.StatisticsActivity
import com.piotrek1543.example.todoapp.ui.util.addFragmentToActivity
import com.piotrek1543.example.todoapp.ui.util.replaceFragmentInActivity
import com.piotrek1543.example.todoapp.ui.util.setupActionBar
import kotlinx.android.synthetic.main.tasks_act.*

/**
 * Tasks Activity houses the Toolbar, the nav UI, the FAB and the fragment holding the tasks list.
 */
class TasksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasks_act)

        // Set up the toolbar.
        setupActionBar(R.id.toolbar) {
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
        }

        // Set up the navigation drawer.
        drawerLayout.setStatusBarBackground(R.color.colorPrimaryDark)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        if (navigationView != null) {
            setupDrawerContent(navigationView)
        }

        setupDrawerContent(nav_view)

        // Use existing content fragment, or create one from scratch.
        supportFragmentManager.findFragmentById(R.id.contentFrame) as TasksFragment?
                ?: TasksFragment().also {
                    addFragmentToActivity(it, R.id.contentFrame)
                }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Open the navigation drawer when the home icon is selected from the toolbar.
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.list_navigation_menu_item -> {
                    // Do nothing, we're already on that screen
                }
                R.id.statistics_navigation_menu_item -> {
                    val intent = Intent(this@TasksActivity, StatisticsActivity::class.java)
                    startActivity(intent)
                }
                else -> {
                }
            }
            // Close the navigation drawer when an item is selected.
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }
    }
}