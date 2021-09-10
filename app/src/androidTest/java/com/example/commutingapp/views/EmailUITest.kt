package com.example.commutingapp.views

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.commutingapp.views.ui.SignIn
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EmailUITest {


   @Before
   fun setup(){
     ActivityScenario.launch(SignIn::class.java).apply {
           moveToState(Lifecycle.State.RESUMED)

       }

   }









}