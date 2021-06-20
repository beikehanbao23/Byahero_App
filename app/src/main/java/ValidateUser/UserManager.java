package ValidateUser;

import android.widget.EditText;

import Logger.LoggerErrorMessage;

public class UserManager implements CharactersValidation {
    private EditText username;
    private EditText email;
    private EditText phoneNumber;
    private EditText password;
    private EditText confirmPassword;


    public UserManager(EditText Username, EditText email, EditText phoneNumber, EditText password, EditText confirmPassword) {
        this.username = Username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
  public UserManager(EditText username, EditText password){
        this.username = username;
        this.password = password;
  }
    public String getName() {
        return username.getText().toString();
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


    public boolean validateUserName() {

        if (isNull(username.getText().toString())) {
            username.setError(LoggerErrorMessage.getNullErrorMessage());
            return true;
        }

        if (hasSpecialCharacters(username.getText().toString().trim())) {
            username.setError("Name can only contain alphanumeric characters, spaces, periods, hyphens, and apostrophes.");
            return true;
        }
        username.setError(null);
        return false;
    }


    public boolean validateEmail() {

        if (isNull(email.getText().toString())) {
            email.setError(LoggerErrorMessage.getNullErrorMessage());
            return true;
        }

        if (!validEmail()) {
            email.setError("Email is invalid"); ;
            return true;
        }
        email.setError(null);
        return false;
    }

    public boolean validateConfirmPassword() {

        if (isNull(confirmPassword.getText().toString())) {
            confirmPassword.setError(LoggerErrorMessage.getNullErrorMessage());
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
            password.setError(LoggerErrorMessage.getNullErrorMessage());
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
