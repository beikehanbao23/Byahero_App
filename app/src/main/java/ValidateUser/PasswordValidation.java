package ValidateUser;

import android.widget.EditText;

public class PasswordValidation implements ProperInput, CharactersValidation {
    private final EditText password;
    private final EditText confirmPassword;

    public PasswordValidation(EditText password, EditText confirmPassword) {
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    @Override
    public boolean isProper() {

        if (password.toString().trim() != confirmPassword.toString().trim()) {
            confirmPassword.setError("The specified password do not match.");
            return true;
        }

        if (isPasswordWeak()) {
            confirmPassword.setError("Password must contain at least 8 characters, including numbers or special characters.");
            return true;
        }

        confirmPassword.setError(null);
        return false;
    }


    public boolean isPasswordWeak() {
        return confirmPassword.toString().trim().toCharArray().length >= 8 && !hasNumber(confirmPassword.toString()) || !hasSpecialCharacters(confirmPassword.toString());
    }

    //check if password is weak
    //check if not equal
    //check if null
}
