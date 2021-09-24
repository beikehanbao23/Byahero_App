package com.example.commutingapp.views.MenuButtons;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.rejowan.cutetoast.CuteToast;


public class CustomBackButton  {
   
    private static final int timeDelayInMillis = 1650;
    private static long backPressedTime = 0;
    private final Activity activity;
    private final Toast toast;

    public CustomBackButton(Activity activity, Context context) {
        this.activity = activity;
        toast = CuteToast.ct(context, "Tap again to exit", 1750, CuteToast.NORMAL, true);

    }




    public void applyDoubleClickToExit() {
        if (doubleClicked()) {
            terminateActivity();
            return;
        }
        beginToastMessage();
    }
    private static boolean doubleClicked() {
        return backPressedTime + timeDelayInMillis > System.currentTimeMillis();
    }
    private void terminateActivity(){
        toast.cancel();
        activity.finish();
    }
    private void beginToastMessage(){
        toast.show();
        registerFirstClick();
    }
    private static void registerFirstClick() {
        
        backPressedTime = System.currentTimeMillis();
    }
    
}
