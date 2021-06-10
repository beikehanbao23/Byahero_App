package ValidateUser;

import android.widget.EditText;

public class PhoneNumberValidation implements CharactersValidation,Validation{

    private EditText phoneNumber;

    public PhoneNumberValidation(EditText phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean is_Unfit() {
        if(hasSpecialCharacters(phoneNumber.getText().toString().trim()) || isNumberSizeIncorrect()){
            phoneNumber.setError("Phone number is invalid.");
            return true;
        }

        phoneNumber.setError(null);
        return false;
    }

    //check if null
    //check character is 11
    //check if contains special characters
    //check if alreadyused

    private boolean isNumberSizeIncorrect(){
        return phoneNumber.getText().toString().toCharArray().length != 11;
    }



    //already used
}
