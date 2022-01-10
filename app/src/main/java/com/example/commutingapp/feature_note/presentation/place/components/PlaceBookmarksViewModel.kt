package com.example.commutingapp.feature_note.presentation.place.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commutingapp.feature_note.domain.model.InvalidPlaceException
import com.example.commutingapp.feature_note.domain.model.PlaceBookmarks
import com.example.commutingapp.feature_note.domain.use_case.PlaceBookmarksUseCase
import com.example.commutingapp.feature_note.domain.util.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlaceBookmarksViewModel @Inject  constructor(
    private val placeBookmarksUseCase: PlaceBookmarksUseCase):ViewModel() {

    private var lastDeletedPlaceBookmarks: PlaceBookmarks? = null
    private var getPlaceJob: Job? = null

    private val _state = MutableStateFlow(PlaceBookmarksState())
    val state : StateFlow<PlaceBookmarksState> = _state

    private val _event = MutableSharedFlow<PlaceBookmarksUiEvent>()
    val event : SharedFlow<PlaceBookmarksUiEvent> = _event.asSharedFlow()


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

        is PlaceBookmarksEvent.SavePlace->{
            viewModelScope.launch {
                try{
                    placeBookmarksUseCase.addPlaceToBookmarks(placeBookmarksEvent.placeBookmarks)
                    _event.emit(PlaceBookmarksUiEvent.SavePlace)
                }catch (e:InvalidPlaceException){
                    Timber.e("${this.javaClass.name}: ${e.message}")
                    _event.emit(PlaceBookmarksUiEvent.ShowSnackBar(e.message ?: "Couldn't Save Place"))
                }
            }
        }

    }

}

    private fun getPlace(orderType: OrderType) {
        getPlaceJob?.cancel()
        getPlaceJob = placeBookmarksUseCase.getPlaceFromBookmarks(orderType).onEach { places ->
                _state.value = state.value.copy(
                    placeBookmarks = places,
                    orderType = orderType)
            }.launchIn(viewModelScope)
    }
}