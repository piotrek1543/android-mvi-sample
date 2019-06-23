/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.piotrek1543.example.todoapp.ui.taskdetail

import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.piotrek1543.example.todoapp.EventObserver
import com.piotrek1543.example.todoapp.R
import com.piotrek1543.example.todoapp.databinding.FragmentTaskdetailBinding
import com.piotrek1543.example.todoapp.presentation.taskdetail.TaskDetailViewModel
import com.piotrek1543.example.todoapp.ui.util.DELETE_RESULT_OK
import com.piotrek1543.example.todoapp.ui.util.obtainViewModel
import com.piotrek1543.example.todoapp.ui.util.setupSnackbar

/**
 * Main UI for the task detail screen.
 */
class TaskDetailFragment : Fragment() {
    private lateinit var viewDataBinding: FragmentTaskdetailBinding

    private lateinit var viewModel: TaskDetailViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupFab()
        viewDataBinding.viewmodel?.let {
            view?.setupSnackbar(this, it.snackbarMessage, Snackbar.LENGTH_SHORT)
        }

        setupNavigation()
    }

    private fun setupNavigation() {
        viewModel.deleteTaskCommand.observe(this, EventObserver {
            val action = TaskDetailFragmentDirections
                    .actionTaskDetailFragmentToTasksFragment(DELETE_RESULT_OK)
            findNavController().navigate(action)
        })
        viewModel.editTaskCommand.observe(this, EventObserver {
            val taskId = TaskDetailFragmentArgs.fromBundle(arguments!!).TASKID
            val action = TaskDetailFragmentDirections
                    .actionTaskDetailFragmentToAddEditTaskFragment(taskId,
                            resources.getString(R.string.edit_task))
            findNavController().navigate(action)
        })
    }

    private fun setupFab() {
        activity?.findViewById<FloatingActionButton>(R.id.fab)?.let {
            it.setImageDrawable(resources.getDrawable(R.drawable.ic_add, it.context!!.theme))
            it.setOnClickListener {
                viewDataBinding.viewmodel?.editTask()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val taskId = arguments?.let {
            TaskDetailFragmentArgs.fromBundle(it).TASKID
        }
        viewDataBinding.viewmodel?.start(taskId)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_taskdetail, container, false)
        viewModel = obtainViewModel(TaskDetailViewModel::class.java)
        viewDataBinding = FragmentTaskdetailBinding.bind(view).apply {
            viewmodel = viewModel
            listener = object : TaskDetailUserActionsListener {
                override fun onCompleteChanged(v: View) {
                    viewmodel?.setCompleted((v as CheckBox).isChecked)
                }
            }
        }
        viewDataBinding.setLifecycleOwner(this.viewLifecycleOwner)
        setHasOptionsMenu(true)
        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete -> {
                viewDataBinding.viewmodel?.deleteTask()
                true
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.taskdetail_fragment_menu, menu)
    }
}
