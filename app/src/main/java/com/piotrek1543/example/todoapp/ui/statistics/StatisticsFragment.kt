/*
 * Copyright 2017, The Android Open Source Project
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
package com.piotrek1543.example.todoapp.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.piotrek1543.example.todoapp.R
import com.piotrek1543.example.todoapp.databinding.FragmentStatisticsBinding
import com.piotrek1543.example.todoapp.presentation.statistics.StatisticsViewModel
import com.piotrek1543.example.todoapp.ui.util.obtainViewModel

/**
 * Main UI for the statistics screen.
 */
class StatisticsFragment : Fragment() {

    private lateinit var viewDataBinding: FragmentStatisticsBinding

    private lateinit var statisticsViewModel: StatisticsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistics, container,
                false)
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        statisticsViewModel = obtainViewModel(StatisticsViewModel::class.java)
        viewDataBinding.stats = statisticsViewModel
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        activity?.findViewById<View>(R.id.fab)?.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        statisticsViewModel.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.findViewById<View>(R.id.fab)?.visibility = View.VISIBLE
    }
}
