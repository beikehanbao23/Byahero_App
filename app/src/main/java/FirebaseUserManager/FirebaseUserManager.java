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
    private String email;
    private String confirmPassword;

    public void createUser(EditText name, EditText email, EditText phoneNumber, EditText password, EditText confirmPassword) {
        userRegisterAndLogin = new User_RegisterAndLogin(name, email, phoneNumber, password, confirmPassword);
        this.email = email.getText().toString().trim();
        this.confirmPassword =  confirmPassword.getText().toString().trim();

    }

    public void LogInUser(){

    }


    public String getEmail() {
        return email;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
    public void getCurrentUser() {
        firebaseUser = firebaseManager.getCurrentUser();
    }

    public FirebaseAuth getFirebaseAuthenticate() {
        return firebaseManager;
    }

    public void signOutUserAccount(){
        firebaseManager.getInstance().signOut();
    }
    public void initializeFirebase() {
        firebaseManager = FirebaseAuth.getInstance();
    }

    public boolean isUserAlreadySignedIn() {
        return firebaseUser != null;
    }

    public boolean UserInputRequirementsFailed() {
        return userRegisterAndLogin.validateName() || userRegisterAndLogin.validateEmail() || userRegisterAndLogin.validatePhoneNumber() || userRegisterAndLogin.validatePassword() || userRegisterAndLogin.validateConfirmPassword();
    }


}
