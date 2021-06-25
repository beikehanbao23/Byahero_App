package FirebaseUserManager;

import android.content.Context;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ValidateUser.User;


public class FirebaseUserManager   {


    private FirebaseAuth firebaseManager;
    private FirebaseUser firebaseUser;


    public FirebaseUserManager(Context context){

    }

    public FirebaseUserManager(){

    }
    public void getCurrentUser() { firebaseUser = firebaseManager.getCurrentUser(); }

    public FirebaseAuth getFirebaseInstance() {
        return firebaseManager;
    }

    public void initializeFirebase() {
        firebaseManager = FirebaseAuth.getInstance();
    }

    public boolean isUserAlreadySignedIn() {
        return firebaseUser != null;
    }


}
