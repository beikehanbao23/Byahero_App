package com.example.commutingapp.data.Auth;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class AuthenticationManager {

    private static FirebaseAuth firebaseAuth;
    private static FirebaseUser firebaseUser ;

    public static void initializeFirebaseApp() {

        firebaseAuth = FirebaseAuth.getInstance();// todo make an init, check if direct boot
    }
    public static void getCreatedUserAccount() {

        firebaseUser = firebaseAuth.getCurrentUser();
    }

    public static FirebaseUser getFirebaseUserInstance(){

        return firebaseUser;
    }

    public static boolean hasAccountRemainingInCache() {

        return firebaseUser != null;
    }

    public static FirebaseAuth getFirebaseAuthInstance() {

        return firebaseAuth;
    }





}
