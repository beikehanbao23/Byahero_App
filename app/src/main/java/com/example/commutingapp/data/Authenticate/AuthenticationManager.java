package com.example.commutingapp.data.Authenticate;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class AuthenticationManager {

    private static FirebaseAuth firebaseAuth;
    private static FirebaseUser firebaseUser;


    public static void getCreatedUserAccount() {

        firebaseUser = firebaseAuth.getCurrentUser();
    }

    public static FirebaseUser getFirebaseUserInstance(){

        return firebaseUser;
    }
    public static FirebaseAuth getFirebaseAuthInstance() {

        return firebaseAuth;
    }

    public static void initializeFirebaseApp() {

        firebaseAuth = FirebaseAuth.getInstance();// todo make an init
    }

    public static boolean hasAccountRemainingInCache() {

        return firebaseUser != null;
    }



}
