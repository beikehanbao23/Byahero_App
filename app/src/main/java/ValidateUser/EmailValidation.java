package ValidateUser;

import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuthEmailException;

public class EmailValidation implements UserValidator{
    private EditText email;

    public EmailValidation(EditText email){this.email = email;}

    @Override
    public void isNull() {
    if(email.toString().isEmpty()){
        email.setError("Email is required");
        return;
    }
    email.setError(null);
    }

    @Override
    public void isValid() {
    //check if already registered
    }


//check if null
        //check if email contains(@)
         //checkifalready used

}
