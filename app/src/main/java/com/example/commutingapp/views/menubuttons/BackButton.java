package com.example.commutingapp.views.menubuttons;

import android.app.Activity;
import android.widget.Toast;

import com.example.commutingapp.R;


public class BackButton {

    private static final int timeDelayInMillis = 1650;
    private static long backPressedTime = 0;


    public void applyDoubleClickToExit(Activity activity) {
        if (doubleClicked()) {
            activity.finish();
            return;
        }
        showToastMessage(activity);
    }






    private static boolean doubleClicked() {
        return backPressedTime + timeDelayInMillis > System.currentTimeMillis();
    }

    private static void showToastMessage(Activity activity){
        Toast.makeText(activity, activity.getString(R.string.doubleTappedMessage), Toast.LENGTH_SHORT).show();
        registerFirstClick();
    }
    private static void registerFirstClick() {
        
        backPressedTime = System.currentTimeMillis();
    }

}
