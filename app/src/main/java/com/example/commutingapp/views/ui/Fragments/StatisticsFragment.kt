package com.example.commutingapp.views.ui.Fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.commutingapp.R
import com.example.commutingapp.viewmodels.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment: Fragment(R.layout.statistics_fragment)  {
    private val viewModel : StatisticsViewModel by viewModels()
}