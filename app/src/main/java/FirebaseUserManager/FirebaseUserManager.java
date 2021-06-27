package FirebaseUserManager;

import android.content.Context;

import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;


public class FirebaseUserManager   {


    private static FirebaseAuth firebaseAuth;
    private static FirebaseUser firebaseUser;


    public static void getCurrentUser() {

        firebaseUser = firebaseAuth.getCurrentUser();
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
