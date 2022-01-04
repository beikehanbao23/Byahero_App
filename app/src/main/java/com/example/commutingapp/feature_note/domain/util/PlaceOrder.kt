package com.example.commutingapp.feature_note.domain.util

sealed class PlaceOrder(val orderType: OrderType){
    class PlaceName(orderType: OrderType): PlaceOrder(orderType)
    class PlaceText(orderType: OrderType):PlaceOrder(orderType)
}
