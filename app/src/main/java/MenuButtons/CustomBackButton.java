package MenuButtons;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.rejowan.cutetoast.CuteToast;

import Logger.CustomToastMessage;

public class BackButton extends AppCompatActivity {

    private static final int timeDelayInMillis = 1800;
    private static long backPressedTime = 0;
    private final Activity activity;
    private final Context context;
    private final CustomToastMessage toastMessage;

    public BackButton(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
        toastMessage = new CustomToastMessage(context, "Tap again to exit", CuteToast.NORMAL);
    }


    /*

        fun test(activity: Activity,context: Context) {
            val toastMessageBackButton = CustomToastMessage(context, "Tap again to exit!", 10)
            CustomBackButton(label@BackButtonOnPressed {
                if (backButton.isDoubleTapped()) {
                    toastMessageBackButton.hideToast()

                    activity.finish()
                    return@BackButtonOnPressed
                }
                toastMessageBackButton.showToast()
                backButton.registerFirstClick()
            }).backButtonIsClicked()
        }
*/



    public void doubleClickOnPressed() {
        if (isDoubleTapped()) {
            toastMessage.hideToast();
            activity.finish();
            return;
        }
        toastMessage.showToast();
        registerFirstClick();
    }
    
    private static boolean isDoubleTapped() {
        
        return backPressedTime + timeDelayInMillis > System.currentTimeMillis();
    }
    private static void registerFirstClick() {
        
        backPressedTime = System.currentTimeMillis();
    }
    
}
