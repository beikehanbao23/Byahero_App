package FirebaseUserManager;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class FirebaseUserManager   {


    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    public void getCurrentUser() {
        firebaseUser = firebaseAuth.getCurrentUser();
    }


    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public void initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public boolean isUserAlreadySignedIn() {
        return firebaseUser != null;
    }


}
