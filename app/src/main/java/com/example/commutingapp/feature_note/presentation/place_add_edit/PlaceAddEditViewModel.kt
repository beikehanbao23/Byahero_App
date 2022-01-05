package com.example.commutingapp.feature_note.presentation.place_add_edit

import androidx.lifecycle.ViewModel
import com.example.commutingapp.feature_note.domain.use_case.PlaceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlaceAddEditViewModel @Inject constructor(
private val placeUseCase: PlaceUseCase): ViewModel() {



}
