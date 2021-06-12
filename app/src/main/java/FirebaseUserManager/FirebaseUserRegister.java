package FirebaseUserManager;

import android.widget.EditText;
import android.widget.Toast;

import com.example.commutingapp.Signup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import Logger.NullErrorDialog;
import ValidateUser.User;


public class FirebaseUserRegister implements NullErrorDialog {


    private FirebaseAuth firebaseAuthenticate;
    private FirebaseUser firebaseUser;
    private User user;
    private String email;
    private String confirmPassword;

    public void createUser(EditText name, EditText email, EditText phoneNumber, EditText password, EditText confirmPassword) {
        user = new User(name, email, phoneNumber, password, confirmPassword);
        this.email = email.getText().toString().trim();
        this.confirmPassword =  confirmPassword.getText().toString().trim();

    }


    public String getEmail() {
        return email;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
    public void getCurrentUser() {
        firebaseUser = firebaseAuthenticate.getCurrentUser();
    }

    public FirebaseAuth getFirebaseAuthenticate() {
        return firebaseAuthenticate;
    }

    public void initializeFirebase() {
        firebaseAuthenticate = FirebaseAuth.getInstance();
    }

    public boolean isUserAlreadySignedIn() {
        return firebaseUser != null;
    }

    public boolean UserInputRequirementsFailed() {
        return user.validateName() || user.validateEmail() || user.validatePhoneNumber() || user.validatePassword() || user.validateConfirmPassword();
    }


}
