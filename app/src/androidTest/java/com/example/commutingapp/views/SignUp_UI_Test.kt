package com.example.commutingapp.views

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.commutingapp.R
import com.example.commutingapp.views.ui.Signup
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignUp_UI_Test {


   @Before
   fun setup(){
     ActivityScenario.launch(Signup::class.java).apply {
           moveToState(Lifecycle.State.RESUMED)
       }

   }
    @Test
    fun isActivityShowing(){
        onView(withId(R.id.signUpActivity)).check(matches(isDisplayed()))
        //noInternetDialog
    }


    @Test
    fun input(){
        onView(withId(R.id.editTextSignUpEmailAddress)).perform(typeText("NewEmail22@gmail.com"),closeSoftKeyboard())
        onView(withId(R.id.editTextSignUpPassword)).perform(typeText("njknjk123"),closeSoftKeyboard())
        onView(withId(R.id.editTextSignUpConfirmPassword)).perform(typeText("njknjk123"),closeSoftKeyboard())
        onView(withId(R.id.CreateButton)).perform(click())
        onView(withId(R.id.signUpActivity)).check(matches(isDisplayed()))

        /*
        onView(withId(R.id.noInternetDialog))
            .inRoot(isDialog()) // <---
            .check(matches(isDisplayed()));


         */
    }











}