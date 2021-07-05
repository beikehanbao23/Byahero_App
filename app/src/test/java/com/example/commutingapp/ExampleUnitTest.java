package com.example.commutingapp;

import android.content.res.Resources;
import android.os.CountDownTimer;
import android.util.Log;

import org.junit.Test;

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
        new CountDownTimer(20000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e(getClass().getName(),"Ticking");
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }


}
