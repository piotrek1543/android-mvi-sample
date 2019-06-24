package com.piotrek1543.example.todoapp

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.piotrek1543.example.todoapp.data.model.Task
import com.piotrek1543.example.todoapp.data.repository.TasksRepository
import com.piotrek1543.example.todoapp.ui.tasks.TasksActivity
import com.piotrek1543.example.todoapp.ui.util.EspressoIdlingResource
import com.piotrek1543.example.todoapp.util.DataBindingIdlingResource
import com.piotrek1543.example.todoapp.util.deleteAllTasksBlocking
import com.piotrek1543.example.todoapp.util.monitorActivity
import com.piotrek1543.example.todoapp.util.saveTaskBlocking
import org.hamcrest.Matchers.allOf
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Large End-to-End test for the tasks module.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class TasksActivityTest {

    private lateinit var repository: TasksRepository

    // An Idling Resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun init() {
        repository = ServiceLocator.provideTasksRepository(getApplicationContext())
        repository.deleteAllTasksBlocking()
    }

    @After
    fun reset() {
        ServiceLocator.resetRepository()
    }

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun createTask() {
        // start up Tasks screen
        // TODO

        // Click on the "+" button, add details, and save
        // TODO

        // Then verify task is displayed on screen
        // TODO
    }

    @Test
    fun editTask() {
        repository.saveTaskBlocking(Task("TITLE1", "DESCRIPTION"))

        // start up Tasks screen
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the task on the list and verify that all the data is correct
        onView(withText("TITLE1")).perform(click())
        onView(withId(R.id.task_detail_title)).check(matches(withText("TITLE1")))
        onView(withId(R.id.task_detail_description)).check(matches(withText("DESCRIPTION")))
        onView(withId(R.id.task_detail_complete)).check(matches(not(isChecked())))

        // Click on the edit button, edit, and save
        onView(withId(R.id.fab)).perform(click())
        onView(withId(R.id.edit_task_title)).perform(replaceText("NEW TITLE"))
        onView(withId(R.id.edit_task_description)).perform(replaceText("NEW DESCRIPTION"))
        onView(withId(R.id.fab)).perform(click())

        // Verify task is displayed on screen in the task list.
        onView(withText("NEW TITLE")).check(matches(isDisplayed()))
        // Verify previous task is not displayed
        onView(withText("TITLE1")).check(doesNotExist())
    }

    @Test
    fun createOneTask_deleteTask() {

        // start up Tasks screen
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Add active task
        onView(withId(R.id.fab)).perform(click())
        onView(withId(R.id.edit_task_title)).perform(typeText("TITLE1"), closeSoftKeyboard())
        onView(withId(R.id.edit_task_description)).perform(typeText("DESCRIPTION"))
        onView(withId(R.id.fab)).perform(click())

        // Open it in details view
        onView(withText("TITLE1")).perform(click())
        // Click delete task in menu
        onView(withId(R.id.menu_delete)).perform(click())

        // Verify it was deleted
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(doesNotExist())
    }

    @Test
    fun createTwoTasks_deleteOneTask() {
        repository.saveTaskBlocking(Task("TITLE1", "DESCRIPTION"))
        repository.saveTaskBlocking(Task("TITLE2", "DESCRIPTION"))

        // start up Tasks screen
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Open the second task in details view
        onView(withText("TITLE2")).perform(click())
        // Click delete task in menu
        onView(withId(R.id.menu_delete)).perform(click())

        // Verify only one task was deleted
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(matches(isDisplayed()))
        onView(withText("TITLE2")).check(doesNotExist())
    }

    @Test
    fun markTaskAsCompleteOnDetailScreen_taskIsCompleteInList() {
        // Add 1 active task
        val taskTitle = "COMPLETED"
        repository.saveTaskBlocking(Task(taskTitle, "DESCRIPTION"))

        // start up Tasks screen
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the task on the list
        onView(withText(taskTitle)).perform(click())

        // Click on the checkbox in task details screen
        onView(withId(R.id.task_detail_complete)).perform(click())

        // Press back button to go back to the list
        pressBack()

        // Check that the task is marked as completed
        onView(allOf(withId(R.id.check_complete), hasSibling(withText(taskTitle))))
                .check(matches(isChecked()))
    }

    @Test
    fun markTaskAsActiveOnDetailScreen_taskIsActiveInList() {
        // Add 1 completed task
        val taskTitle = "ACTIVE"
        repository.saveTaskBlocking(Task(taskTitle, "DESCRIPTION", true))

        // start up Tasks screen
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the task on the list
        onView(withText(taskTitle)).perform(click())
        // Click on the checkbox in task details screen
        onView(withId(R.id.task_detail_complete)).perform(click())

        // Press back button to go back to the list
        pressBack()

        // Check that the task is marked as active
        onView(allOf(withId(R.id.check_complete), hasSibling(withText(taskTitle))))
                .check(matches(not(isChecked())))
    }

    @Test
    fun markTaskAsCompleteAndActiveOnDetailScreen_taskIsActiveInList() {
        // Add 1 active task
        val taskTitle = "ACT-COMP"
        repository.saveTaskBlocking(Task(taskTitle, "DESCRIPTION"))

        // start up Tasks screen
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the task on the list
        onView(withText(taskTitle)).perform(click())
        // Click on the checkbox in task details screen
        onView(withId(R.id.task_detail_complete)).perform(click())
        // Click again to restore it to original state
        onView(withId(R.id.task_detail_complete)).perform(click())

        // Press back button to go back to the list
        pressBack()

        // Check that the task is marked as active
        onView(allOf(withId(R.id.check_complete), hasSibling(withText(taskTitle))))
                .check(matches(not(isChecked())))
    }

    @Test
    fun markTaskAsActiveAndCompleteOnDetailScreen_taskIsCompleteInList() {
        // Add 1 completed task
        val taskTitle = "COMP-ACT"
        repository.saveTaskBlocking(Task(taskTitle, "DESCRIPTION", true))

        // start up Tasks screen
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the task on the list
        onView(withText(taskTitle)).perform(click())
        // Click on the checkbox in task details screen
        onView(withId(R.id.task_detail_complete)).perform(click())
        // Click again to restore it to original state
        onView(withId(R.id.task_detail_complete)).perform(click())

        // Press back button to go back to the list
        pressBack()

        // Check that the task is marked as active
        onView(allOf(withId(R.id.check_complete), hasSibling(withText(taskTitle))))
                .check(matches(isChecked()))
    }

    @Test
    fun createTask_solution() {
        // start up Tasks screen
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the "+" button, add details, and save
        onView(withId(R.id.fab)).perform(click())
        onView(withId(R.id.edit_task_title)).perform(typeText("title"), closeSoftKeyboard())
        onView(withId(R.id.edit_task_description)).perform(typeText("description"))
        onView(withId(R.id.fab)).perform(click())

        // Then verify task is displayed on screen
        onView(withText("title")).check(matches(isDisplayed()))
    }
}
