package Logger;

import android.widget.EditText;

public interface NullErrorDialog {

    public default String getErrorMessage(){
        return "Field cannot be left blank.";
    }
}
