package com.example.commutingapp.views.ui.Fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.commutingapp.R
import com.example.commutingapp.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingFragment: Fragment(R.layout.tracking_fragment)  {
    private val viewModel : MainViewModel by viewModels()
}