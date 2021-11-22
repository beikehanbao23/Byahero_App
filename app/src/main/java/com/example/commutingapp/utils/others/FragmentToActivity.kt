package com.example.commutingapp.utils.others

interface FragmentToActivity<in T> {
    fun onFirstNotify()
    fun onSecondNotify()
    fun onThirdNotify(t:T)
}