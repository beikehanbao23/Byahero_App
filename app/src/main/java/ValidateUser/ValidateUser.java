package ValidateUser;

public class ValidateUser {
    public boolean isNull(String attribute){
        return attribute.isEmpty();
    }
    public boolean isAlreadyUsed(String attribute){
        return true;
    }
}
