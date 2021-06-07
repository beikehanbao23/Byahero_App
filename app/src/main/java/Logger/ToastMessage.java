package Logger;

import android.content.Context;
import android.widget.Toast;

public class ToastMessage {
    private Toast toast;


    public ToastMessage(Context context, String message) {
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
    }

    public void showMessage() {
        toast.show();
    }

    public void hideMessage() {
        toast.cancel();
    }

}

