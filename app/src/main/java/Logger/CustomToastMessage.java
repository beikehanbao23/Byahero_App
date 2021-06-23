package Logger;

import android.content.Context;
import android.widget.Toast;

import com.rejowan.cutetoast.CuteToast;

public class CustomToastMessage {
    private Toast cuteToast;

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
       cuteToast = CuteToast.ct(context, message, CuteToast.LENGTH_SHORT,type,true);

    }

    public void showMessage() { cuteToast.show(); }

    public void hideMessage() {
        cuteToast.cancel();
    }

}

