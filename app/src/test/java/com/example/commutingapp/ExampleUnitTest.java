package com.example.commutingapp;

import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.sql.Connection;

import InternetConnection.ConnectionManager;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void test(){
    System.out.println(R.string.noInternetConnectionAtSignMessage);
    }

}