package com.example.commutingapp.views

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.commutingapp.R
import com.example.commutingapp.views.ui.Signup
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EmailUITest {


   @Before
   fun setup(){
     ActivityScenario.launch(Signup::class.java).apply {
           moveToState(Lifecycle.State.RESUMED)
       }

   }


    @Test
    fun inputTextViews(){



        onView(withId(R.id.editTextSignUpEmailAddress)).perform(ViewActions.typeText("johndoe1234@gmail.com"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.editTextSignUpPassword)).perform(ViewActions.typeText("johndoe1234"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.editTextSignUpConfirmPassword)).perform(ViewActions.typeText("johndoe1234"), ViewActions.closeSoftKeyboard());
    }
    @Test
    fun signUp(){

    }





}