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
        if (message == null) { throw new RuntimeException("Message cannot be null!");}
        if (context == null){throw new RuntimeException("Context cannot be null");}
        if (type > 10 || type < 1) { throw new RuntimeException("Type of toast is out of bounds"); }

        cuteToast = CuteToast.ct(context, message, CuteToast.LENGTH_SHORT, type, true);
        handler = new Handler();
    }


    public CustomToastMessage() {

    }


    public void showToastWithLimitedTimeThenClose(long timeAsMilliseconds) {
        if(timeAsMilliseconds == 0) {
            throw new RuntimeException("Time as milliseconds is invalid!");
        }
        cuteToast.show();

        handler.postDelayed(() -> {
            cuteToast.cancel();
        }, timeAsMilliseconds);
    }


    public void showToast() {
        cuteToast.show();
    }

    public void hideToast() {
        cuteToast.cancel();
    }

}

