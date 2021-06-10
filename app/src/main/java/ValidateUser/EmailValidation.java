package ValidateUser;

import android.widget.EditText;

import Logger.NullErrorDialog;

public class EmailValidation implements CharactersValidation,Validation {
    private EditText email;

    public EmailValidation(EditText email) {
        this.email = email;
    }

    public EmailValidation() {
    }

    @Override
    public boolean is_Unfit() {

        if (!validEmail()) {
            email.setError("Email is invalid");
            return true;
        }
        email.setError(null);
        return false;
    }
    private boolean validEmail(){
        return android.util.Patterns.EMAIL_ADDRESS.matcher((CharSequence) email.getText().toString().trim()).matches();
    }
}