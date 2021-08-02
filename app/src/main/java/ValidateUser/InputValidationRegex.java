package ValidateUser;

import java.util.regex.Pattern;

public class RegexValidation {
    public static boolean hasNumeric(String input){
        return Pattern.compile("[0-9]").matcher(input).find();
    }
    public static boolean hasSpecialCharacters(String input){
        return Pattern.compile("[!#$%&*()_+=|<>?{}\\[\\]~]").matcher(input).find();
    }
}
