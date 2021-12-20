package com.example.commutingapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commutingapp.data.local_db.Commuter
import com.example.commutingapp.data.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepository):ViewModel(){

    fun insertCommuter(commuter: Commuter){
        viewModelScope.launch {
            repository.insertCommuter(commuter)
        }
    }
}