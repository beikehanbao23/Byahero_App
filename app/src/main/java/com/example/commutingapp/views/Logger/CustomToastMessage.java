package com.example.commutingapp.views.Logger;

import android.content.Context;
import android.widget.Toast;

import com.rejowan.cutetoast.CuteToast;

public class CustomToastMessage {
    private Toast toast;

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

        toast = CuteToast.ct(context, message, 1400, type, true);
    }
     CustomToastMessage() {

    }
    public void showToast() {
        toast.show();
    }
    public void hideToast() {
        toast.cancel();
    }

}

