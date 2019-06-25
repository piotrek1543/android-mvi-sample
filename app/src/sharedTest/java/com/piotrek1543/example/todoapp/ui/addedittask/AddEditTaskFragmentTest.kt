package com.piotrek1543.example.todoapp.ui.addedittask

import android.content.Context
import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.piotrek1543.example.todoapp.R
import com.piotrek1543.example.todoapp.ServiceLocator
import com.piotrek1543.example.todoapp.data.FakeRepository
import com.piotrek1543.example.todoapp.data.Result
import com.piotrek1543.example.todoapp.data.repository.TasksRepository
import com.piotrek1543.example.todoapp.ui.tasks.TasksActivity
import com.piotrek1543.example.todoapp.util.getTasksBlocking
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.LooperMode
import org.robolectric.annotation.TextLayoutMode

/**
 * Integration test for the Add Task screen.
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
@LooperMode(LooperMode.Mode.PAUSED)
@TextLayoutMode(TextLayoutMode.Mode.REALISTIC)
@ExperimentalCoroutinesApi
class AddEditTaskFragmentTest {
    private lateinit var repository: TasksRepository

    @Before
    fun initRepository() {
        repository = FakeRepository()
        ServiceLocator.tasksRepository = repository
    }

    @After
    fun cleanupDb() = runBlockingTest {
        ServiceLocator.resetRepository()
    }

    @Test
    fun emptyTask_isNotSaved() {
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        activityScenario.onActivity {
            val bundle = AddEditTaskFragmentArgs(null,
                    ApplicationProvider.getApplicationContext<Context>().getString(R.string.add_task)).toBundle()
            it.findNavController(R.id.nav_host_fragment).navigate(R.id.addEditTaskFragment, bundle)
        }
        // GIVEN - On the "Add Task" screen.
        onView(withId(R.id.edit_task_title)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_task_description)).check(matches(isDisplayed()))

        // WHEN - Enter invalid title and description combination and click save
        onView(withId(R.id.edit_task_title)).perform(clearText())
        onView(withId(R.id.edit_task_description)).perform(clearText())
        onView(withId(R.id.fab)).perform(click())

        // THEN - Entered Task is still displayed (a correct task would close it).
        onView(withId(R.id.edit_task_title)).check(matches(isDisplayed()))
    }

    @Test
    fun validTask_isSaved() {
        val title = "title"
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        activityScenario.onActivity {
            val bundle = AddEditTaskFragmentArgs(null,
                    ApplicationProvider.getApplicationContext<Context>().getString(R.string.add_task)).toBundle()
            it.findNavController(R.id.nav_host_fragment).navigate(R.id.addEditTaskFragment, bundle)
        }

        // GIVEN - On the "Add Task" screen.
        onView(withId(R.id.edit_task_title)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_task_description)).check(matches(isDisplayed()))

        // WHEN - Valid title and description combination and click save
        onView(withId(R.id.edit_task_title)).perform(replaceText(title))
        onView(withId(R.id.edit_task_description)).perform(replaceText("description"))
        onView(withId(R.id.fab)).perform(click())

        // THEN - Verify that the repository saved the task
        onView(withId(R.id.text_title)).check(matches(withText(containsString(title))))
    }

    @Test
    fun validTask_navigatesBack() {
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        activityScenario.onActivity {
            val bundle = AddEditTaskFragmentArgs(null,
                    ApplicationProvider.getApplicationContext<Context>().getString(R.string.add_task)).toBundle()
            it.findNavController(R.id.nav_host_fragment).navigate(R.id.addEditTaskFragment, bundle)
        }

        // GIVEN - On the "Add Task" screen.
        onView(withId(R.id.edit_task_title)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_task_description)).check(matches(isDisplayed()))

        // WHEN - Valid title and description combination and click save
        onView(withId(R.id.edit_task_title)).perform(replaceText("title"))
        onView(withId(R.id.edit_task_description)).perform(replaceText("description"))
        onView(withId(R.id.fab)).perform(click())

        // THEN - Verify that we navigated back to the tasks screen.
        onView(withId(R.id.check_complete)).check(matches(isDisplayed()))
        onView(withId(R.id.text_title)).check(matches(isDisplayed()))
    }

    @Test
    fun validTask_isSaved_solution() {
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        activityScenario.onActivity {
            val bundle = AddEditTaskFragmentArgs(null,
                    ApplicationProvider.getApplicationContext<Context>().getString(R.string.add_task)).toBundle()
            it.findNavController(R.id.nav_host_fragment).navigate(R.id.addEditTaskFragment, bundle)
        }

        // GIVEN - On the "Add Task" screen.
        onView(withId(R.id.edit_task_title)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_task_description)).check(matches(isDisplayed()))

        // WHEN - Valid title and description combination and click save
        onView(withId(R.id.edit_task_title)).perform(replaceText("title"))
        onView(withId(R.id.edit_task_description)).perform(replaceText("description"))
        onView(withId(R.id.fab)).perform(click())

        // THEN - Verify that the repository saved the task
        val tasks = (repository.getTasksBlocking(true) as Result.Success).data
        assertEquals(tasks.size, 1)
        assertEquals(tasks[0].title, "title")
        assertEquals(tasks[0].description, "description")
    }
}
