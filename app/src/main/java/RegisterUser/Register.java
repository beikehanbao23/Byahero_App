package RegisterUser;

import android.widget.EditText;

import Logger.NullErrorDialog;
import ValidateUser.EmailValidation;
import ValidateUser.NameValidation;
import ValidateUser.PasswordValidation;
import ValidateUser.PhoneNumberValidation;
import ValidateUser.Validation;

public class Register implements NullErrorDialog {

    private EditText name;
    private EditText email;
    private EditText phoneNo;
    private EditText password;
    private EditText confirmPassword;

    public Register(EditText name, EditText email, EditText phoneNumber, EditText password, EditText confirmPassword) {
    this.name = name;
    this.email = email;
    this.phoneNo = phoneNumber;
    this.password = password;
    this.confirmPassword = confirmPassword;
    }


    public void registerUser(){
        if(oneInputIsNull()) return;

        if(isInputUnfit())return;



    }

    private boolean oneInputIsNull(){

        for (EditText values: getInputs()){
           if(values.getText().toString().trim().isEmpty()){
               values.setError(getErrorMessage());
               return true;
           }
            values.setError(null);
        }

        return false;
    }

    private boolean isInputUnfit(){
        for (Validation validation:getValidateInputs()) {
            if(validation.is_Unfit()){
                return true;
            }
        }
        return false;
    }


    private EditText[] getInputs(){
        return new EditText[]{name,email,phoneNo,password,confirmPassword};
    }
    private Validation[] getValidateInputs(){
        return new Validation[]{new NameValidation(name), new EmailValidation(email),new PhoneNumberValidation(phoneNo), new PasswordValidation(password,confirmPassword)};
    }
}
