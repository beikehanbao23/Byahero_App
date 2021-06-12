package ValidateUser;

import android.widget.EditText;

import Logger.NullErrorDialog;

public class User implements CharactersValidation, NullErrorDialog {
    private final EditText name;
    private final EditText email;
    private final EditText phoneNumber;
    private final EditText password;
    private final EditText confirmPassword;


    public User(EditText name, EditText email, EditText phoneNumber, EditText password, EditText confirmPassword) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getName() {
        return name.getText().toString();
    }

    public String getEmail() {
        return email.getText().toString();
    }

    public String getPhoneNumber() {
        return phoneNumber.getText().toString();
    }

    public String getPassword() {
        return password.getText().toString();
    }

    public String getConfirmPassword() {
        return confirmPassword.getText().toString();
    }


    public boolean validateName() {

        if (isNull(name.getText().toString())) {
            name.setError(getErrorMessage());
            return true;
        }

        if (hasSpecialCharacters(name.getText().toString().trim())) {
            name.setError("Name can only contain alphanumeric characters, spaces, periods, hyphens, and apostrophes.");
            return true;
        }
        name.setError(null);
        return false;
    }


    public boolean validateEmail() {

        if (isNull(email.getText().toString())) {
            email.setError(getErrorMessage());
            return true;
        }

        if (!validEmail()) {
            email.setError("Email is invalid");
            return true;
        }
        email.setError(null);
        return false;
    }

    public boolean validateConfirmPassword() {

        if (isNull(confirmPassword.getText().toString())) {
            confirmPassword.setError(getErrorMessage());
            return true;
        }
        if (Password_Is_Unmatch()) {
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

    public boolean validatePassword() {

        if (isNull(password.getText().toString())) {
            password.setError(getErrorMessage());
            return true;
        }

        password.setError(null);
        return false;
    }

    public boolean validatePhoneNumber() {

        if (hasSpecialCharacters(phoneNumber.getText().toString().trim()) || isNumberSizeCorrect()) {
            phoneNumber.setError("Phone number is invalid.");
            return true;
        }

        phoneNumber.setError(null);
        return false;
    }


    private boolean Password_Is_Unmatch() {
        return (!password.getText().toString().trim().equals(confirmPassword.getText().toString().trim()));
    }

    private boolean isPasswordStrong() {
        return confirmPassword.getText().toString().trim().toCharArray().length >= 8 && (hasNumber(confirmPassword.getText().toString().trim()) || hasSpecialCharacters(confirmPassword.getText().toString().trim()));
    }

    private boolean validEmail() {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches();
    }

    private boolean isNumberSizeCorrect() {
        return phoneNumber.getText().toString().trim().toCharArray().length != 11;
    }

    private boolean isNull(String input) {
        return input.trim().isEmpty();
    }
}
