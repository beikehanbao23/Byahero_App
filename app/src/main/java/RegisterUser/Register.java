package RegisterUser;

import android.widget.EditText;

import Logger.NullErrorDialog;

public class Register implements NullErrorDialog {

    private EditText name,email,phoneNo,password,confirmPassword;
    public Register(EditText name, EditText email, EditText phoneNumber, EditText password, EditText confirmPassword) {
    this.name = name;
    this.email = email;
    this.phoneNo = phoneNumber;
    this.password = password;
    this.confirmPassword = confirmPassword;
    }


    public void registerUser(){
        if(oneInputIsNull()){
            return;
        }
    }
    public boolean oneInputIsNull(){
        EditText inputs[] = {name,email,phoneNo,password,confirmPassword};
        for (EditText values: inputs){
           if(values.getText().toString().trim().isEmpty()){
               values.setError(getErrorMessage());
               return true;
           }
            values.setError(null);
        }

        return false;
    }
}
