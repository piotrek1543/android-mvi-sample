package com.piotrek1543.example.todoapp.presentation.statistics

import com.piotrek1543.example.todoapp.presentation.base.BaseIntent

sealed class StatisticsIntent : BaseIntent {

    object InitialIntent : StatisticsIntent()

}
