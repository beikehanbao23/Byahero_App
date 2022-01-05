package com.example.commutingapp.feature_note.presentation.place.components

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commutingapp.feature_note.domain.model.PlaceBookmarks
import com.example.commutingapp.feature_note.domain.use_case.PlaceBookmarksUseCase
import com.example.commutingapp.feature_note.domain.util.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceBookmarksViewModel @Inject constructor(
    private val placeBookmarksUseCase: PlaceBookmarksUseCase
):ViewModel() {


    private val _state = MutableLiveData(PlaceBookmarksState())
    val bookmarksState: LiveData<PlaceBookmarksState> = _state
    private var getPlaceJob: Job? = null
    var lastDeletedPlaceBookmarks: PlaceBookmarks? = null



    init{
        getPlace(OrderType.Ascending)
    }
    fun onEvent(placeBookmarksEvent:PlaceBookmarksEvent){

    when(placeBookmarksEvent){
        is PlaceBookmarksEvent.DeletePlaceBookmarks->{
            viewModelScope.launch {
                placeBookmarksUseCase.deletePlaceFromBookmarks(placeBookmarksEvent.placeBookmarks)
                lastDeletedPlaceBookmarks = placeBookmarksEvent.placeBookmarks
            }
        }

        is PlaceBookmarksEvent.ChangeOrder->{
            getPlace(placeBookmarksEvent.orderType)
        }

        is PlaceBookmarksEvent.RestoreNote->{
            viewModelScope.launch {
                placeBookmarksUseCase.addPlaceToBookmarks(lastDeletedPlaceBookmarks ?: return@launch)
                lastDeletedPlaceBookmarks = null
            }
        }

        is PlaceBookmarksEvent.SearchPlaceBookmarks->{
            viewModelScope.launch {
                placeBookmarksUseCase.getPlaceNameFromBookmarks(placeBookmarksEvent.placeName)
            }
        }


    }

}


    private fun getPlace(orderType: OrderType){
        getPlaceJob?.cancel()
        getPlaceJob = placeBookmarksUseCase.getPlaceFromBookmarks(orderType).onEach { places->
            _state.value = bookmarksState.value!!.copy(
                placeBookmarks = places,
                orderType = orderType)
        }.launchIn(viewModelScope)
    }
}