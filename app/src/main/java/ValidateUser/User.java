package ValidateUser;

import android.content.res.Resources;
import android.util.Patterns;
import android.widget.EditText;

import androidx.core.content.res.TypedArrayUtils;

import com.example.commutingapp.R;

import static android.provider.Settings.System.getString;
import static com.example.commutingapp.R.string.*;


public class User implements CharactersValidation {
    private EditText name;
    private EditText email;
    private EditText phoneNumber;
    private EditText password;
    private EditText confirmPassword;


    public User(EditText name, EditText email, EditText phoneNumber, EditText password, EditText confirmPassword) {
        this.name = name;
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
    public String getNname() {
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


    public boolean validateNameFailed() {
        if(name.getText().toString().length() < 3){
            name.setError("Name should be at least 3 character long");
            name.requestFocus();
            return true;
        }
        if (isNull(name.getText().toString())) {
            name.setError(Resources.getSystem().getString(noInternetConnectionAtSignMessage));
            name.requestFocus();
            return true;
        }

        if (hasSpecialCharacters(name.getText().toString().trim())) {
            name.setError("Name can only contain alphanumeric characters, spaces, periods, hyphens, and apostrophes.");
            name.requestFocus();
            return true;
        }
        name.setError(null);
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
