package MenuButtons;


import androidx.appcompat.app.AppCompatActivity;



public class backButton extends AppCompatActivity {
    private static long backPressedTime = 0;
    private static final int timeDelayInMillis = 1800;


    public static boolean isDoubleTapped() {
        return backPressedTime + timeDelayInMillis > System.currentTimeMillis();
    }

    public static void registerFirstClick(){
        backPressedTime = System.currentTimeMillis();
    }
}
