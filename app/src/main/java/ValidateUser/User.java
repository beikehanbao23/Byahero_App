package ValidateUser;

public class User implements CharactersValidation{
    private String name;
    private String email;
    private String phoneNumber;
    private String password;
    private String confirmPassword;


    public User(String name, String email, String phoneNumber, String password, String confirmPassword) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }


    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }




    public void validateName(){
        if (hasSpecialCharacters(name.trim())) {
            throw new RuntimeException("Name can only contain alphanumeric characters, spaces, periods, hyphens, and apostrophes.");
        }
    }

    public void validateEmail(){
        if(!validEmail()){
            throw new RuntimeException("Email is invalid");
        }
    }

    public void validatePassword(){
    if(Password_Is_Unmatch()){
        throw new RuntimeException("The specified password do not match.");
    }

    if(!isPasswordStrong()){
        throw new RuntimeException("Password must contain at least 8 characters, including numbers or special characters.");
    }

    }

    public void validatePhoneNumber(){
        if(hasSpecialCharacters(phoneNumber.trim()) || isNumberSizeCorrect()){
            throw new RuntimeException("Phone number is invalid.");
        }
    }



    private boolean Password_Is_Unmatch(){
        return (!password.trim().equals(confirmPassword.trim()));
    }
    private boolean isPasswordStrong() {
        return confirmPassword.trim().toCharArray().length >= 8   &&   (hasNumber(confirmPassword.trim()) || hasSpecialCharacters(confirmPassword.trim()));
    }
    private boolean validEmail(){
        return android.util.Patterns.EMAIL_ADDRESS.matcher((CharSequence) email.trim()).matches();
    }
    private boolean isNumberSizeCorrect(){
        return phoneNumber.trim().toCharArray().length != 11;
    }
}
