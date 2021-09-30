package com.example.commutingapp.views.MenuButtons;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import com.rejowan.cutetoast.CuteToast;


public class NavigationButton {

    private static final int timeDelayInMillis = 1650;
    private static long backPressedTime = 0;


    public static void applyDoubleClickToExit(Activity activity) {
        if (doubleClicked()) {
            terminateActivity(activity);
            return;
        }
        beginToastMessage(activity);
    }






    private static boolean doubleClicked() {
        return backPressedTime + timeDelayInMillis > System.currentTimeMillis();
    }
    private static void terminateActivity(Activity activity){
        activity.finish();
    }
    private static void beginToastMessage(Activity activity){
        CuteToast.ct(activity, "Tap again to exit", 1750, CuteToast.NORMAL, true).show();
        registerFirstClick();
    }
    private static void registerFirstClick() {
        
        backPressedTime = System.currentTimeMillis();
    }

}
