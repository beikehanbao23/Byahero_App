package ValidateUser;


import android.widget.EditText;

public class UserManager extends User{

    public UserManager(EditText name, EditText email, EditText phoneNumber, EditText password, EditText confirmPassword) {
        super(name, email, phoneNumber, password, confirmPassword);
    }

    public UserManager(EditText email, EditText password){
      super(email, password);
    }

    public boolean UserInputRequirementsFailedAtSignUp() {
        return validateNameFailed() || validateEmailFailed() || validatePhoneNumberFailed() || validatePasswordFailed() || validateConfirmPasswordFailed();
    }
    public boolean UserInputRequirementsFailedAtSignIn(){
        return validateEmailFailed() || validatePasswordFailed();
    }

}
