package com.example.commutingapp.views.ui.activities;

import static com.example.commutingapp.R.string.resendEmailFailedMessage;
import static com.example.commutingapp.R.string.resendEmailSuccessMessage;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import com.example.commutingapp.R;
import com.example.commutingapp.databinding.CircularProgressbarBinding;
import com.example.commutingapp.databinding.CustomEmailsentDialogBinding;
import com.example.commutingapp.utils.ui_utilities.ActivitySwitch;
import com.example.commutingapp.viewmodels.EmailSentViewModel;
import com.example.commutingapp.views.dialogs.DialogDirector;
import com.example.commutingapp.views.menubuttons.NavigationButton;



public class EmailSent extends AppCompatActivity {


    private DialogDirector dialogDirector;
    private CustomEmailsentDialogBinding emailDialogBinding;
    private CircularProgressbarBinding progressbarBinding;
    private EmailSentViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        initializeAttributes();
        displayUserEmailToTextView();
        subscribeToObservers();
        viewModel.refreshEmailSynchronously();



    }

    private void initializeAttributes() {
        emailDialogBinding = CustomEmailsentDialogBinding.inflate(getLayoutInflater());
        progressbarBinding = CircularProgressbarBinding.bind(emailDialogBinding.getRoot());
        setContentView(emailDialogBinding.getRoot());
        dialogDirector = new DialogDirector(this);
        viewModel = new ViewModelProvider(this).get(EmailSentViewModel.class);

    }
    private void subscribeToObservers() {
        observeNoInternetActivityTransition();
        observeMainActivityTransition();
        observeEmailVerification();
    }

    private void observeMainActivityTransition() {
        viewModel.getMainScreenTransitionStatus().observe(this, transition -> {
            if (transition.getContentIfNotHandled() != null) {
                showMainScreenActivity();
            }
        });
    }

    private void observeNoInternetActivityTransition() {
        viewModel.getInternetConnectionStatus().observe(this, transition -> showNoInternetActivity());
    }

    private void observeEmailVerification() {
        viewModel.sendEmailOnSuccess().observe(this, task -> {
            dialogDirector.showSuccessDialog("New email sent", getString(resendEmailSuccessMessage));
        });
        viewModel.sendEmailOnFail().observe(this, task -> {
            dialogDirector.showWarningDialog("Please check your inbox", getString(resendEmailFailedMessage));
        });
    }




    private void destroyBinding() {
        emailDialogBinding = null;
        progressbarBinding = null;
    }






    @Override
    protected void onStart() {
        super.onStart();
    }





    private void showNoInternetActivity() {
        progressbarBinding.circularProgressBar.setVisibility(View.VISIBLE);
        long DELAY_INTERVAL_FOR_NO_INTERNET_DIALOG = 2000;
        new Handler().postDelayed(() -> {
            dialogDirector.constructNoInternetDialog().setOnDismissListener(T->viewModel.refreshEmailSynchronously());
            progressbarBinding.circularProgressBar.setVisibility(View.INVISIBLE);
        }, DELAY_INTERVAL_FOR_NO_INTERNET_DIALOG);
    }

    private void showMainScreenActivity() {
        progressbarBinding.circularProgressBar.setVisibility(View.VISIBLE);
        long DELAY_INTERVAL_FOR_MAIN_SCREEN_DIALOG = 2150;
        new Handler().postDelayed(() -> {
            ActivitySwitch.INSTANCE.startActivityOf(this, MainScreen.class);
        }, DELAY_INTERVAL_FOR_MAIN_SCREEN_DIALOG);

    }

    @Override
    public void onBackPressed() {
         NavigationButton.applyDoubleClickToExit(this);
    }

    private void displayUserEmailToTextView() {
        viewModel.displayUserEmailToTextView().observe(this, userEmail -> emailDialogBinding.textViewEmail.setText(userEmail));
    }

    public void resendEmailIsClicked(View view) {

        startVerificationTimer();
        viewModel.sendEmailVerification();
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
        viewModel.getTimerOnRunningStatus().observe(this, this::displayWhenVerificationTimerStarted);
        viewModel.getTimerOnFinishedStatus().observe(this, timer -> displayWhenVerificationTimerIsFinished());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyBinding();
    }


}
