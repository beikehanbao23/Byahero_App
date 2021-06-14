package MenuButtons;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.prefs.BackingStoreException;

import Logger.ToastMessage;

public class BackButton extends AppCompatActivity {
    private  long backPressedTime = 0;
    private final Integer timeDelayInMillis;
    private final ToastMessage message;


    public BackButton(Context context, Integer timeDelayInMillis, String textmessage) {
        this.timeDelayInMillis = timeDelayInMillis;
        message = new ToastMessage(context, textmessage);
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
