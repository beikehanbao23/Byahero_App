package ValidateUser;

import android.widget.EditText;

public class PasswordValidation implements CharactersValidation,Validation {
    private final EditText password;
    private final EditText confirmPassword;

    public PasswordValidation(EditText password, EditText confirmPassword) {
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    @Override
    public boolean is_Unfit() {

        if (isPasswordUnmatch()) {
            confirmPassword.setError("The specified password do not match.");
            return true;
        }

        if (!isPasswordStrong()) {
            confirmPassword.setError("Password must contain at least 8 characters, including numbers or special characters.");
            return true;
        }

        confirmPassword.setError(null);
        return false;
    }

    private boolean isPasswordUnmatch(){
        return (!password.getText().toString().trim().equals(confirmPassword.getText().toString().trim()));
    }
    private boolean isPasswordStrong() {
        return confirmPassword.getText().toString().trim().toCharArray().length >= 8   &&   (hasNumber(confirmPassword.getText().toString().trim()) || hasSpecialCharacters(confirmPassword.getText().toString().trim())); // special characters validation removed
    }
//johndominic123

    //check if null
}
