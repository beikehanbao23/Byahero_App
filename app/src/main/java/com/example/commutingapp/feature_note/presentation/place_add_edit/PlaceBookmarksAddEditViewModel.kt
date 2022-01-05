package com.example.commutingapp.feature_note.presentation.place_add_edit

import androidx.lifecycle.ViewModel
import com.example.commutingapp.feature_note.domain.use_case.PlaceBookmarksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlaceBookmarksAddEditViewModel @Inject constructor(
private val placeBookmarksUseCase: PlaceBookmarksUseCase): ViewModel() {



}
