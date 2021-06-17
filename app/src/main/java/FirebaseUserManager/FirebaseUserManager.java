package FirebaseUserManager;

import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ValidateUser.UserManager;


public class FirebaseUserManager   {


    private FirebaseAuth firebaseManager;
    private FirebaseUser firebaseUser;
    private UserManager userRegisterAndLogin;


    public void verifyUserForSignUp(EditText name, EditText email, EditText phoneNumber, EditText password, EditText confirmPassword) {
        userRegisterAndLogin = new UserManager(name, email, phoneNumber, password, confirmPassword);
    }


    public void verifyUserForSignIn(EditText username, EditText password){
        userRegisterAndLogin = new UserManager(username,password);
    }



    public void getCurrentUser() {
        firebaseUser = firebaseManager.getCurrentUser();
    }

    public FirebaseAuth getFirebaseAuthenticate() {
        return firebaseManager;
    }

    public void initializeFirebase() {
        firebaseManager = FirebaseAuth.getInstance();
    }

    public boolean isUserAlreadySignedIn() {
        return firebaseUser != null;
    }




    public boolean UserInputRequirementsFailedAtSignUp() {
        return userRegisterAndLogin.validateUserName() || userRegisterAndLogin.validateEmail() || userRegisterAndLogin.validatePhoneNumber() || userRegisterAndLogin.validatePassword() || userRegisterAndLogin.validateConfirmPassword();
    }
    public boolean UserInputRequirementsFailedAtSignIn(){
        return userRegisterAndLogin.validateUserName()|| userRegisterAndLogin.validatePassword();
    }

}
