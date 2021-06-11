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

         user = new User(name, email, phoneNumber, password, confirmPassword);

    }

    public boolean UserInputRequirementsFailed() {
    return  true;
    }


}
