package com.piotrek1543.example.todoapp.presentation.statistics

import com.piotrek1543.example.todoapp.presentation.base.BaseResult

sealed class StatisticsResult : BaseResult {

    sealed class LoadStatisticsResult : StatisticsResult() {

        data class Success(val activeCount: Int, val completedCount: Int) : LoadStatisticsResult()

        data class Failure(val error: Throwable) : LoadStatisticsResult()

        object InFlight : LoadStatisticsResult()

    }
}
