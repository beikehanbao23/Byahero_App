package com.example.commutingapp.feature_note.domain.use_case

interface IUseCase<in I, out O> {
    operator fun invoke(input : I):O
}