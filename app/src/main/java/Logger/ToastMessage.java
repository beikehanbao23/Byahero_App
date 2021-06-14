package Logger;

import android.content.Context;
import android.widget.Toast;

import com.rejowan.cutetoast.CuteToast;

public class ToastMessage {
    private Toast cuteToast;


    public ToastMessage(Context context, String message) {
       cuteToast = CuteToast.ct(context, message, CuteToast.LENGTH_SHORT,CuteToast.NORMAL,true);
    }

    public void showMessage() {
        cuteToast.show();
    }

    public void hideMessage() {
        cuteToast.cancel();
    }

}

