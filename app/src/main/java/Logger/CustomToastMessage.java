package Logger;

import android.content.Context;
import android.widget.Toast;

import com.rejowan.cutetoast.CuteToast;

public class CustomToastMessage {
    private Toast cuteToast;


    public CustomToastMessage(Context context, String message, int type) {
       cuteToast = CuteToast.ct(context, message, CuteToast.LENGTH_SHORT,type,true);

    }

    public void showMessage() {
        cuteToast.show();
    }

    public void hideMessage() {
        cuteToast.cancel();
    }

}

