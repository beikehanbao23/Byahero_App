package FirebaseUserManager;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.example.commutingapp.Signup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import Logger.NullErrorDialog;
import ValidateUser.User;


public class FirebaseUserRegister implements NullErrorDialog {
    private FirebaseAuth firebaseAuthenticate;
    private FirebaseUser firebaseUser;
    private User user;

    public void createUser(EditText name, EditText email, EditText phoneNumber, EditText password, EditText confirmPassword) {
       user = new User(name, email, phoneNumber, password, confirmPassword);
    }
    public void registerUser(){
        firebaseAuthenticate.createUserWithEmailAndPassword(user.getEmail(),user.getConfirmPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

            }
        })


    }







    public void getCurrentUser(){
        firebaseUser = firebaseAuthenticate.getCurrentUser();
    }
    public void initializeFirebase(){
        firebaseAuthenticate = FirebaseAuth.getInstance();
    }
    public boolean isUserAlreadySignedIn(){
        return firebaseUser != null;
    }

    public boolean UserInputRequirementsFailed() {
        return user.validateName() || user.validateEmail() || user.validatePhoneNumber() || user.validatePassword() || user.validateConfirmPassword();
    }











}
