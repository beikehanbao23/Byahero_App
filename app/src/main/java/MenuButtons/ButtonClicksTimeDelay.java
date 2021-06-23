package MenuButtons;

import android.content.Context;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.appcompat.app.AppCompatActivity;

import com.example.commutingapp.Signup;

import Logger.CustomToastMessage;

public class ButtonClicksTimeDelay extends AppCompatActivity {
    private  long backPressedTime = 0;
    private  int timeDelayInMillis = 0;



    public ButtonClicksTimeDelay(int preferredTimeDelayInMillis) {
     this.timeDelayInMillis = preferredTimeDelayInMillis;
    }
    public ButtonClicksTimeDelay(){

    }

    public void setPrefferedTimeDelayInMillis(int preferredTimeDelayInMillis){
        this.timeDelayInMillis = preferredTimeDelayInMillis;
    }
/*
    /public void showToastMessageThenBack(Context act) {

        //    if (isDoubleTapped()) {
               // message.hideMessage();
         //       super.onBackPressed();
         //       return;
            }
         //   message.showMessage();
      //  setBackPressedTimeTo_CurrentTimeMillis();
    }
*/
    public boolean isDoubleTapped() {
        if(timeDelayInMillis == 0){
            throw new RuntimeException("Preferred time set is invalid");
        }

        return backPressedTime + timeDelayInMillis > System.currentTimeMillis();
    }

    public void     setBackPressedTimeTo_CurrentTimeMillis(){
        backPressedTime = System.currentTimeMillis();
    }
}
