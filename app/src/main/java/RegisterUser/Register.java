package RegisterUser;

import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

import Logger.NullErrorDialog;
import ValidateUser.UserManager;

public class Register implements NullErrorDialog {



    public void RegisterUser(EditText name, EditText email, EditText phoneNumber, EditText password, EditText confirmPassword) {
    UserManager userManager = new UserManager();
    userManager.createUser(name,email,phoneNumber,password,confirmPassword);

    if(userManager.UserInputRequirementsFailed()){
        return;
    }

    }












}
