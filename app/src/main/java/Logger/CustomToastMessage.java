package Logger;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.rejowan.cutetoast.CuteToast;

public class CustomToastMessage {
    private Toast cuteToast;
    private Handler handler;

    /*
    types of Toast Message::

         INFO = 1;
         WARN = 2;
         ERROR = 3;
         SUCCESS = 4;
         HAPPY = 5;
         SAD = 6;
         CONFUSE = 7;
         DELETE = 8;
         SAVE = 9;
         NORMAL = 10;

     */

    public CustomToastMessage(Context context, String message, int type) {
        if (message.equals(null)) {
            throw new RuntimeException("Message is invalid!");
        }
        if (type > 10 || type < 1) {
            throw new RuntimeException("Toast message type is invalid!");
        }
        cuteToast = CuteToast.ct(context, message, CuteToast.LENGTH_SHORT, type, true);
        handler = new Handler();
    }


    public CustomToastMessage() {

    }


    public void showToastWithLimitedTime(long timeAsMilliseconds) {
        if(timeAsMilliseconds == 0) {
            throw new RuntimeException("Time as millis is invalid!");
        }
        cuteToast.show();

        handler.postDelayed(() -> {
            cuteToast.cancel();
        }, timeAsMilliseconds);
    }

    //show toast, then after 2 sec close toast
    public void showToast() {
        cuteToast.show();
    }

    public void hideToast() {
        cuteToast.cancel();
    }//remove

}

