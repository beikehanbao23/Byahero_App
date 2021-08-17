package MenuButtons;

import android.app.Activity;
import android.content.Context;

import com.rejowan.cutetoast.CuteToast;

import Logger.CustomToastMessage;

public class CustomBackButton  {

    private static final int timeDelayInMillis = 1800;
    private static long backPressedTime = 0;
    private final Activity activity;
    private final CustomToastMessage toastMessage;

    public CustomBackButton(Activity activity, Context context) {
        this.activity = activity;
        toastMessage = new CustomToastMessage(context, "Tap again to exit", CuteToast.NORMAL);
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
        toastMessage.hideToast();
        activity.finish();
    }
    private void beginToastMessage(){
        toastMessage.showToast();
        registerFirstClick();
    }
    private static void registerFirstClick() {
        
        backPressedTime = System.currentTimeMillis();
    }
    
}
