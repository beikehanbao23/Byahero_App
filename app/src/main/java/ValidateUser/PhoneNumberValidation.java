package ValidateUser;

import android.widget.EditText;

public class PhoneNumberValidation implements CharactersValidation,ProperInput{

    private EditText phoneNumber;

    public PhoneNumberValidation(EditText phoneNumber){
        this.phoneNumber = phoneNumber;
    }
    @Override
    public boolean is_Unacceptable() {
        if(hasSpecialCharacters(phoneNumber.toString().trim()) || isNumberSizeCorrect()){
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
        return phoneNumber.toString().toCharArray().length == 11;
    }



    //already used
}
