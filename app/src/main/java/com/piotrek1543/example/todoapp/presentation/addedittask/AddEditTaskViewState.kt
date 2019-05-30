package com.piotrek1543.example.todoapp.presentation.addedittask

import com.piotrek1543.example.todoapp.presentation.base.BaseViewState

data class AddEditTaskViewState(
        val isEmpty: Boolean,
        val isSaved: Boolean,
        val title: String,
        val description: String,
        val error: Throwable?
) : BaseViewState {

    companion object {

        fun idle(): AddEditTaskViewState {
            return AddEditTaskViewState(
                    title = "",
                    description = "",
                    error = null,
                    isEmpty = false,
                    isSaved = false
            )
        }

    }

}