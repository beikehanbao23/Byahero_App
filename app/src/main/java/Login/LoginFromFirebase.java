package Login;

import FirebaseUserManager.FirebaseUserManager;
import ValidateUser.User;

public class LoginFromFirebase implements UserAccount {
    private  FirebaseUserManager firebaseUserManager;

    public LoginFromFirebase(){
        firebaseUserManager = new FirebaseUserManager();
        firebaseUserManager.initializeFirebase();
    }

    @Override
    public void LoginAccount() {

    }

    @Override
    public void SignOutAccount() {

    }


}
