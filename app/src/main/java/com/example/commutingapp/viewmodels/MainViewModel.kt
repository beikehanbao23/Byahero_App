package com.example.commutingapp.viewmodels

import androidx.lifecycle.ViewModel
import com.example.commutingapp.data.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(repository: MainRepository):ViewModel()