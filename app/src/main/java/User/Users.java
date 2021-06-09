package User;

public class Users {
    private final String name;
    private final String email;
    private final String phoneNumber;
    private final String password;
    private final String confirmPassword;

    public Users(String name, String email, String phoneNumber, String password, String confirmPassword) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public String getPassword() {
        return password;
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
}
