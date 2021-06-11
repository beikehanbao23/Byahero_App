package ValidateUser;

import android.widget.EditText;

public class UserManager {
    private EditText name;
    private EditText email;
    private EditText phoneNumber;
    private EditText password;
    private EditText confirmPassword;
    private User user;

    public void createUser(EditText name, EditText email, EditText phoneNumber, EditText password, EditText confirmPassword) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.confirmPassword = confirmPassword;

         user = new User(name.getText().toString(), email.getText().toString(), phoneNumber.getText().toString(), password.getText().toString(), confirmPassword.getText().toString());

    }

    public boolean UserInputRequirementsFailed() {
        boolean result = false;

        if (nameNotValid()) result = true;

        if(emailNotValid()) result = true;

        if(phoneNumberNotValid()) result = true;

        if(passwordNotValid()) result = true;

        return result;
    }



    private boolean phoneNumberNotValid(){
        try{
            user.validatePhoneNumber();
        }catch (RuntimeException ex){
            phoneNumber.setError(ex.getMessage());
            return true;
        }
        return false;
    }

    private boolean passwordNotValid(){
        try{
            user.validatePassword();
        }catch (RuntimeException ex){
            password.setError(ex.getMessage());
            return true;
        }
        return false;
    }

    private boolean emailNotValid() {
        try {
            user.validateEmail();
        } catch (RuntimeException ex) {
            email.setError(ex.getMessage());
            return true;
        }
        return false;
    }

    private boolean nameNotValid() {
        try {
            user.validateName();
        } catch (RuntimeException ex) {
            name.setError(ex.getMessage());
            return true;
        }
        return false;

    }
}
