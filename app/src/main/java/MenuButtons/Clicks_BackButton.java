package MenuButtons;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import Logger.CustomToastMessage;

public class Clicks_BackButton extends AppCompatActivity {
    private  long backPressedTime = 0;
    private final Integer timeDelayInMillis;
    private final CustomToastMessage message;


    public Clicks_BackButton(Context context, Integer timeDelayInMillis, String textmessage) {
        this.timeDelayInMillis = timeDelayInMillis;
        message = new CustomToastMessage(context, textmessage,10);
    }

    public void showToastMessageThenBack() {
        if (isDoubleTapped()) {
            message.hideMessage();
            super.onBackPressed();
            return;
        }
        message.showMessage();
        backPressedTime = System.currentTimeMillis();
    }

    private boolean isDoubleTapped() {
        return backPressedTime + timeDelayInMillis > System.currentTimeMillis();
    }
}