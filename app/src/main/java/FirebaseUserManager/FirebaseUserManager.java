package FirebaseUserManager;



import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class FirebaseUserManager    {


    private static FirebaseAuth firebaseAuth;
    private static FirebaseUser firebaseUser;

    //2
    public static void getCurrentUser() {

        firebaseUser = firebaseAuth.getCurrentUser();
    }
    //1
    public static FirebaseUser getFirebaseUser(){

        return firebaseUser;
    }
    public static FirebaseAuth getFirebaseAuth() {

        return firebaseAuth;
    }

    public static void initializeFirebase() {

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public static boolean isUserAlreadySignedIn() {

        return firebaseUser != null;
    }



}
