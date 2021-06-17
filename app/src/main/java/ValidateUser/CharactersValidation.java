package ValidateUser;

import java.util.regex.Pattern;

public interface CharactersValidation {
     default boolean hasNumber(String input){
        return Pattern.compile("[0-9]").matcher(input).find();
    }
     default boolean hasSpecialCharacters(String input){
        return Pattern.compile("[!#$%&*()_+=|<>?{}\\[\\]~]").matcher(input).find();
    }

}
