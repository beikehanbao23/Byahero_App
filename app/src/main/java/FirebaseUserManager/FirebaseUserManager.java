package FirebaseUserManager;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class FirebaseUserManager   {


    private static FirebaseAuth firebaseAuth;
    private static FirebaseUser firebaseUser;


    public static void getCurrentUser() {

        firebaseUser = firebaseAuth.getCurrentUser();
    }
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
