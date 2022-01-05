package com.example.commutingapp.feature_note.presentation.place.components

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commutingapp.feature_note.domain.model.Place
import com.example.commutingapp.feature_note.domain.use_case.PlaceUseCase
import com.example.commutingapp.feature_note.domain.util.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceViewModel @Inject constructor(
    private val placeUseCase: PlaceUseCase
):ViewModel() {


    private val _state = MutableLiveData(PlaceState())
    val state: LiveData<PlaceState> = _state
    private var getPlaceJob: Job? = null
    var lastDeletedPlace: Place? = null



    init{
        getPlace(OrderType.Ascending)
    }
    fun onEvent(placeEvent:PlaceEvent){

    when(placeEvent){
        is PlaceEvent.DeletePlace->{
            viewModelScope.launch {
                placeUseCase.deletePlace(placeEvent.place)
                lastDeletedPlace = placeEvent.place
            }
        }

        is PlaceEvent.ChangeOrder->{
            getPlace(placeEvent.orderType)
        }

        is PlaceEvent.RestoreNote->{
            viewModelScope.launch {
                placeUseCase.addPlace(lastDeletedPlace ?: return@launch)
                lastDeletedPlace = null
            }
        }

        is PlaceEvent.SearchPlace->{
            viewModelScope.launch {
                placeUseCase.getPlaceName(placeEvent.placeName)
            }
        }


    }

}


    private fun getPlace(orderType: OrderType){
        getPlaceJob?.cancel()
        getPlaceJob = placeUseCase.getPlace(orderType).onEach { places->
            _state.value = state.value!!.copy(
                places = places,
                orderType = orderType)
        }.launchIn(viewModelScope)
    }
}