package com.example.commutingapp.views.ui;

import android.os.*;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.commutingapp.R;
import com.example.commutingapp.databinding.*;
import com.example.commutingapp.viewmodels.EmailSentViewModel;
import com.example.commutingapp.utils.FirebaseUserManager.*;
import com.example.commutingapp.views.Logger.*;
import com.example.commutingapp.views.MenuButtons.CustomBackButton;
import com.example.commutingapp.utils.ui_utilities.ActivitySwitcher;
import com.example.commutingapp.utils.ui_utilities.AttributesInitializer;
import com.example.commutingapp.utils.ui_utilities.BindingDestroyer;
import static com.example.commutingapp.R.string.*;


public class EmailSent extends AppCompatActivity implements BindingDestroyer, AttributesInitializer {


    private DialogPresenter customPopupDialog;
    private final long DELAY_INTERVAL_FOR_NO_INTERNET_DIALOG = 1500;
    private final long DELAY_INTERVAL_FOR_MAIN_SCREEN_DIALOG = 2000;
    private CustomToastMessage toastMessageBackButton;
    private CustomEmailsentDialogBinding emailDialogBinding;
    private CircularProgressbarBinding progressbarBinding;
    private EmailSentViewModel viewModel;

    @Override protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        initializeAttributes();
        FirebaseManager.initializeFirebaseApp();
        displayUserEmailToTextView();
        applyObservers();
    }

    private void applyObservers(){
        observeNoInternetActivityTransition();
        observeMainActivityTransition();
        observeEmailVerification();
    }
    private void observeMainActivityTransition(){
        viewModel.getMainScreenActivityTransition().observe(this, transition->{
            if(transition.getContentIfNotHandled()!=null){ showMainScreenActivity(); }
        });
    }
    private void observeNoInternetActivityTransition(){
        viewModel.getNoInternetActivityTransition().observe(this,transition-> showNoInternetActivity());
    }
    private void observeEmailVerification(){
        viewModel.getSendEmailOnSuccess().observe(this,task->{
            customPopupDialog.showSuccessDialog("New email sent", getString(resendEmailSuccessMessage));
        });
        viewModel.getSendEmailOnFailed().observe(this,task->{
            customPopupDialog.showWarningDialog("Please check your inbox", getString(resendEmailFailedMessage));
        });
    }
    @Override public void initializeAttributes() {
        emailDialogBinding = CustomEmailsentDialogBinding.inflate(getLayoutInflater());
        progressbarBinding = CircularProgressbarBinding.bind(emailDialogBinding.getRoot());
        setContentView(emailDialogBinding.getRoot());
        customPopupDialog = new CustomDialogs(this);
        toastMessageBackButton = new CustomToastMessage(this, getString(doubleTappedMessage), 10);
        viewModel = new ViewModelProvider(this).get(EmailSentViewModel.class);

    }
    @Override public void destroyBinding() {
        emailDialogBinding = null;
        progressbarBinding = null;
    }
    @Override protected void onStart() {
        super.onStart();
        viewModel.refreshEmailSynchronously();
    }
    private void showNoInternetActivity(){
        progressbarBinding.circularProgressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(()-> {
           ActivitySwitcher.INSTANCE.startActivityOf(this, NoInternet.class);
            progressbarBinding.circularProgressBar.setVisibility(View.INVISIBLE);
       }, DELAY_INTERVAL_FOR_NO_INTERNET_DIALOG);
    }
    private void showMainScreenActivity() {
        progressbarBinding.circularProgressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(()->{
            ActivitySwitcher.INSTANCE.startActivityOf(this,this, MainScreen.class);
        },DELAY_INTERVAL_FOR_MAIN_SCREEN_DIALOG);

    }
    @Override public void onBackPressed() {
       new CustomBackButton(this,this).applyDoubleClickToExit();
    }
    private void displayUserEmailToTextView() {
        viewModel.displayUserEmailToTextView().observe(this,userEmail-> emailDialogBinding.textViewEmail.setText(userEmail));
    }
    public void resendEmailIsClicked(View view) {

        startVerificationTimer();
        viewModel.sendEmail();
    }

    private void displayWhenVerificationTimerStarted(long secondsLeft) {
        emailDialogBinding.ResendVerificationButton.setTextColor(ContextCompat.getColor(this, R.color.gray));
        emailDialogBinding.ResendVerificationButton.setText("Resend verification in " + secondsLeft + "s");
        emailDialogBinding.ResendVerificationButton.setEnabled(false);
    }
    private void displayWhenVerificationTimerIsFinished() {
        emailDialogBinding.ResendVerificationButton.setTextColor(ContextCompat.getColor(this, R.color.blue2));
        emailDialogBinding.ResendVerificationButton.setText("Resend verification");
        emailDialogBinding.ResendVerificationButton.setEnabled(true);
    }
    private void startVerificationTimer() {
        viewModel.startTimer();
        viewModel.getTimerOnRunning().observe(this, this::displayWhenVerificationTimerStarted);
        viewModel.getTimerOnFinished().observe(this, timer->displayWhenVerificationTimerIsFinished());
    }
    @Override protected void onDestroy() {
        super.onDestroy();
        destroyBinding();
    }


}