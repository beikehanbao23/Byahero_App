package ValidateUser;

public class UserManager {
    public void createUser(String name, String email, String phoneNumber, String password, String confirmPassword){
        User user = new User(name, email, phoneNumber, password, confirmPassword);
        validateUser(user);
    }
    private void validateUser(User usr){
        usr.validateName();
        usr.validateEmail();
        usr.validatePhoneNumber();
        usr.validatePassword();

    }
}
