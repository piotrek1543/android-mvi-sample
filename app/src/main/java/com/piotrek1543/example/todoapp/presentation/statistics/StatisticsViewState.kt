package com.piotrek1543.example.todoapp.presentation.statistics

import com.piotrek1543.example.todoapp.presentation.base.BaseViewState

data class StatisticsViewState(
        val isLoading: Boolean,
        val activeCount: Int,
        val completedCount: Int,
        val error: Throwable?
) : BaseViewState {
    companion object {

        fun idle(): StatisticsViewState {
            return StatisticsViewState(
                    isLoading = false,
                    activeCount = 0,
                    completedCount = 0,
                    error = null
            )
        }
    }
}
