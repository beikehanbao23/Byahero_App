package ValidateUser;

import android.util.Patterns;
import android.widget.EditText;

import com.example.commutingapp.R;

import Logger.LoggerErrorMessage;

public class User implements CharactersValidation {
    private EditText username;
    private EditText email;
    private EditText phoneNumber;
    private EditText password;
    private EditText confirmPassword;


    public User(EditText Username, EditText email, EditText phoneNumber, EditText password, EditText confirmPassword) {
        this.username = Username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
  public User(EditText email, EditText password){
        this.email = email;
        this.password = password;
  }
  public User(){

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


    public boolean validateUserNameFailed() {

        if (isNull(username.getText().toString())) {
            username.setError(LoggerErrorMessage.getNullErrorMessage());
            username.requestFocus();
            return true;
        }

        if (hasSpecialCharacters(username.getText().toString().trim())) {
            username.setError("Name can only contain alphanumeric characters, spaces, periods, hyphens, and apostrophes.");
            username.requestFocus();
            return true;
        }
        username.setError(null);
        return false;
    }


    public boolean validateEmailFailed() {

        if (isNull(email.getText().toString())) {
            email.setError(LoggerErrorMessage.getNullErrorMessage());
            email.requestFocus();
            return true;
        }

        if (!validEmail()) {
            email.setError("Email is invalid");
            email.requestFocus();
            return true;
        }
        email.setError(null);
        return false;
    }

    public boolean validateConfirmPasswordFailed() {

        if (isNull(confirmPassword.getText().toString())) {
            confirmPassword.setError(LoggerErrorMessage.getNullErrorMessage());
            confirmPassword.requestFocus();
            return true;
        }
        if (Password_Is_Unmatch()) {
            confirmPassword.setError("The specified password do not match.");
            confirmPassword.requestFocus();
            return true;
        }

        if (!isPasswordStrong()) {
            confirmPassword.setError("Password must contain at least 8 characters, including numbers or special characters.");
            confirmPassword.requestFocus();
            return true;

        }
        confirmPassword.setError(null);
        return false;
    }

    public boolean validatePasswordFailed() {

        if (isNull(password.getText().toString())) {
            password.setError(LoggerErrorMessage.getNullErrorMessage());
            password.requestFocus();
            return true;
        }

        password.setError(null);
        return false;
    }

    public boolean validatePhoneNumberFailed() {

        if (hasSpecialCharacters(phoneNumber.getText().toString().trim()) || isNumberSizeIncorrect() || isPhoneNumberDoesNotStartAtZero()) {
            phoneNumber.setError("Phone number is invalid.");
            phoneNumber.requestFocus();
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
        return Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches();
    }

    private boolean isNumberSizeIncorrect() {
        return phoneNumber.getText().toString().trim().toCharArray().length != 11;
    }

    private boolean isNull(String input) {
        return input.trim().isEmpty();
    }
    private boolean isPhoneNumberDoesNotStartAtZero(){
        return !phoneNumber.getText().toString().trim().startsWith("0");
    }
}
