package com.example.commutingapp.feature_note.domain.use_case

interface IAsyncUseCase<in I> {

    suspend operator fun invoke(input : I)
}