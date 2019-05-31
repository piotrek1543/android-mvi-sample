package com.piotrek1543.example.todoapp.presentation.statistics

import com.piotrek1543.example.todoapp.presentation.base.BaseAction

sealed class StatisticsAction : BaseAction {

    object LoadStatisticsAction : StatisticsAction()

}
