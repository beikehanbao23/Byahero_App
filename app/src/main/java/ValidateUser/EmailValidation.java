package ValidateUser;

import android.widget.EditText;

import Logger.NullErrorDialog;

public class EmailValidation implements CharactersValidation, NullErrorDialog,ProperInput {
    private EditText email;

    public EmailValidation(EditText email){this.email = email;}


    @Override
    public boolean isProper() {

        if(!email.toString().trim().contains("@")){
            email.setError("Email is invalid");
            return true;
        }
        email.setError(null);
        return false;
    }



//check if null
        //check if email contains(@)
         //checkifalready used

}
