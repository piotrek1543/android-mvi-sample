package com.piotrek1543.example.todoapp.view.addedittask

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import com.piotrek1543.example.todoapp.R
import kotlinx.android.synthetic.main.addtask_act.*

/**
 * Fragment for adding/editing tasks.
 */
class AddEditTaskFragment : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fab_edit_task_done?.apply {
            setImageResource(R.drawable.ic_done)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.addtask_frag, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.addtask_fragment_menu, menu)
    }

}