package MenuButtons;

public class CustomBackButton {
    private BackButtonDoubleClicked backButtonDoubleClicked;
    public  CustomBackButton(BackButtonDoubleClicked backButtonDoubleClicked){
        this.backButtonDoubleClicked = backButtonDoubleClicked;
    }
    public void backButtonIsClicked(){
        backButtonDoubleClicked.backButtonClicked();
    }
}
