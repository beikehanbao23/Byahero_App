package RegisterUser;

import android.widget.EditText;

import Logger.NullErrorDialog;

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


    /*
    if validate throw message put the thrown message as setError
    if catched then return true else false
    create a function called register with multiple  if's

     */









}
