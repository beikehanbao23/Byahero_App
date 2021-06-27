package ValidateUser;

import android.content.Context;
import android.util.Patterns;
import android.widget.EditText;

import static com.example.commutingapp.R.string.*;


public class User {
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private Context context;


    public User(EditText email, EditText password, EditText confirmPassword) {

        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public User(EditText email, EditText password) {
        this.email = email;
        this.password = password;
    }

    public User() {

    }

    public void setContext(Context context) {
        if (context == null) throw new RuntimeException("Context cannot be null");
        this.context = context;
    }


    public boolean validateEmailFailed() {
        String emailInput = email.getText().toString().trim();

        if (isNull(emailInput)) {
            email.setError(context.getString(getFieldLeftBlankMessage));
            email.requestFocus();
            return true;
        }

        if (!validEmail()) {
            email.setError(context.getString(getEmailIsInvalidMessage));
            email.requestFocus();
            return true;
        }
        email.setError(null);
        return false;
    }

    public boolean validateConfirmPasswordFailed() {

        String confirmPasswordInput = confirmPassword.getText().toString().trim();

        if (isNull(confirmPasswordInput)) {
            confirmPassword.setError(context.getString(getFieldLeftBlankMessage));
            confirmPassword.requestFocus();
            return true;
        }
        if (passwordIsNotMatch()) {
            confirmPassword.setError(context.getString(getPasswordIsNotMatchMessage));
            confirmPassword.requestFocus();
            return true;
        }

        if (!isPasswordStrong()) {
            confirmPassword.setError(context.getString(getPasswordIsWeakMessage));
            confirmPassword.requestFocus();
            return true;

        }
        confirmPassword.setError(null);
        return false;
    }

    public boolean validatePasswordFailed() {
        String passwordInput = password.getText().toString().trim();

        if (isNull(passwordInput)) {
            password.setError(context.getString(getFieldLeftBlankMessage));
            password.requestFocus();
            return true;
        }

        password.setError(null);
        return false;
    }


    private boolean passwordIsNotMatch() {
        return (!password.getText().toString().trim().equals(confirmPassword.getText().toString().trim()));
    }

    private boolean isPasswordStrong() {
        return confirmPassword.getText().toString().trim().toCharArray().length >= 8 && (RegexValidation.isNumeric(confirmPassword.getText().toString().trim()) || RegexValidation.isSpecialCharacters(confirmPassword.getText().toString().trim()));
    }

    private boolean validEmail() {
        return Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches();
    }


    private boolean isNull(String input) {
        return input.trim().isEmpty();
    }
}

