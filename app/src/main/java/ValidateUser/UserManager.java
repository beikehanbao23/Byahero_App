package ValidateUser;


import android.content.Context;
import android.widget.EditText;

public class UserManager extends User{

    public UserManager(Context context,  EditText email,  EditText password, EditText confirmPassword) {
        super( email, password, confirmPassword);
        super.setContext(context);
    }

    public UserManager(Context context,EditText email, EditText password){
      super(email, password);
      super.setContext(context);
    }

    public boolean UserInputRequirementsFailedAtSignUp() {
        return  validateEmailFailed() || validatePasswordFailed() || validateConfirmPasswordFailed();
    }
    public boolean UserInputRequirementsFailedAtSignIn(){
        return validateEmailFailed() || validatePasswordFailed();
    }

}
