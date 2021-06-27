package MenuButtons;


import androidx.appcompat.app.AppCompatActivity;



public class ButtonClicksTimeDelay extends AppCompatActivity {
    private  long backPressedTime = 0;
    private  int timeDelayInMillis = 0;

    public ButtonClicksTimeDelay(int preferredTimeDelayInMillis) {
     this.timeDelayInMillis = preferredTimeDelayInMillis;
    }


    public boolean isDoubleTapped() {
        if(timeDelayInMillis == 0){
            throw new RuntimeException("Preferred time set is invalid");
        }

        return backPressedTime + timeDelayInMillis > System.currentTimeMillis();
    }

    public void registerFirstClick(){
        backPressedTime = System.currentTimeMillis();
    }
}
