package Logger;

import android.content.Context;
import android.widget.Toast;

import com.example.commutingapp.R;
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
        if(message.equals(null)){
            throw new RuntimeException("Message is invalid!");
        }
        if(type > 10 || type < 1){
            throw new RuntimeException("Toast message type is invalid!");
        }

       cuteToast = CuteToast.ct(context, message, CuteToast.LENGTH_SHORT,type,true);

    }
    public CustomToastMessage(){

    }

    public void showMessage() {
        cuteToast.show();
    }

    public void hideMessage() {
        cuteToast.cancel();
    }

}

