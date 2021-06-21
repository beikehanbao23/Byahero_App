package ValidateUser;

import android.widget.EditText;

public class UserManager {
    private User userRegisterAndLogin;

    public void verifyUserForSignUp(EditText name, EditText email, EditText phoneNumber, EditText password, EditText confirmPassword) {
        userRegisterAndLogin = new User(name, email, phoneNumber, password, confirmPassword);
    }

    public void verifyUserForSignIn(EditText email, EditText password){
        userRegisterAndLogin = new User(email,password);
    }

    public boolean UserInputRequirementsFailedAtSignUp() {
        return userRegisterAndLogin.validateUserNameFailed() || userRegisterAndLogin.validateEmailFailed() || userRegisterAndLogin.validatePhoneNumberFailed() || userRegisterAndLogin.validatePasswordFailed() || userRegisterAndLogin.validateConfirmPasswordFailed();
    }
    public boolean UserInputRequirementsFailedAtSignIn(){
        return userRegisterAndLogin.validateEmailFailed() || userRegisterAndLogin.validatePasswordFailed();
    }

}
