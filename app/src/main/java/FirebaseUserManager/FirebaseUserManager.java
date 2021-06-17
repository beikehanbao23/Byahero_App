package FirebaseUserManager;

import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import Logger.NullErrorDialog;
import ValidateUser.User_RegisterAndLogin;


public class FirebaseUserManager implements NullErrorDialog {


    private FirebaseAuth firebaseManager;
    private FirebaseUser firebaseUser;
    private User_RegisterAndLogin userRegisterAndLogin;


    public void createUser(EditText name, EditText email, EditText phoneNumber, EditText password, EditText confirmPassword) {
        userRegisterAndLogin = new User_RegisterAndLogin(name, email, phoneNumber, password, confirmPassword);
    }


    public void loginUser(EditText username, EditText password){
        userRegisterAndLogin = new User_RegisterAndLogin(username,password);
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
