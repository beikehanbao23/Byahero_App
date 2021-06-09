package RegisterUser;

import android.widget.EditText;

public class Register {

    private EditText name,email,phoneNo,password,confirmPassword;
    public Register(EditText name, EditText email, EditText phoneNumber, EditText password, EditText confirmPassword) {
    this.name = name;
    this.email = email;
    this.phoneNo = phoneNumber;
    this.password = password;
    this.confirmPassword = confirmPassword;
    }
    public boolean oneInputIsNull(){
        EditText inputs[] = {name,email,phoneNo,password,confirmPassword};//class dapat
        for (EditText i: inputs){
            return i.toString().isEmpty();
        }
        return false;
    }
}
