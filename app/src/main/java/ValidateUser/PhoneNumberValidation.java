package ValidateUser;

import android.util.Patterns;
import android.widget.EditText;

public class PhoneNumberValidation implements CharactersValidation,Validation{

    private EditText phoneNumber;

    public PhoneNumberValidation(EditText phoneNumber){
        this.phoneNumber = phoneNumber;
    }
    // +639198103939
    @Override
    public boolean is_Unfit() {
        if(hasSpecialCharacters(phoneNumber.getText().toString().trim()) || isNumberSizeCorrect()){
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

    private boolean isNumberSizeCorrect(){
        return phoneNumber.getText().toString().trim().toCharArray().length != 11;
    }

}
