package ValidateUser;

import android.widget.EditText;

public class NameValidation implements Validation, CharactersValidation {
    private final EditText name;

    public NameValidation(EditText name) {
        this.name = name;
    }

    @Override
    public boolean is_Unfit() {
        if (hasSpecialCharacters(name.getText().toString().trim())) {
            name.setError("Name can only contain alphanumeric characters, spaces, periods, hyphens, and apostrophes.");
            return true;
        }
        name.setError(null);
        return false;
    }


    //check if null
    //check if already used


}
