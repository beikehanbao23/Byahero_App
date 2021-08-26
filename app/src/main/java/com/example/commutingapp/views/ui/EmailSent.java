package com.example.commutingapp.views.ui;

import android.os.*;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.commutingapp.R;
import com.example.commutingapp.databinding.*;
import com.example.commutingapp.viewmodels.EmailSentViewModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import java.util.Objects;
import com.example.commutingapp.utils.FirebaseUserManager.*;
import com.example.commutingapp.views.Logger.*;
import com.example.commutingapp.views.MenuButtons.CustomBackButton;
import com.example.commutingapp.utils.ui_utilities.ActivitySwitcher;
import com.example.commutingapp.utils.ui_utilities.AttributesInitializer;
import com.example.commutingapp.utils.ui_utilities.BindingDestroyer;
import static com.example.commutingapp.R.string.*;


public class EmailSent extends AppCompatActivity implements BindingDestroyer, AttributesInitializer {


    private final long threadInterval = 2000;
    private DialogPresenter customPopupDialog;
    private final long DELAY_INTERVAL_FOR_NO_INTERNET_DIALOG = 1500;
    private final long DELAY_INTERVAL_FOR_MAIN_SCREEN_DIALOG = 2950;
    private CountDownTimer verificationTimer;
    private CustomToastMessage toastMessageBackButton;
    private volatile boolean exitThread;
    private CustomEmailsentDialogBinding emailDialogBinding;
    private CircularProgressbarBinding progressbarBinding;
    private EmailSentViewModel viewModel;

    @Override protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        initializeAttributes();
        FirebaseManager.initializeFirebaseApp();
        displayUserEmailToTextView();


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


    private void refreshEmailAutomatically() {
        new Thread(() -> {
            while (!exitThread) {
                reloadUserEmail();
                Log.d("THREAD STATUS: ","RUNNING");
                try { Thread.sleep(threadInterval); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }).start();
    }


    @Override protected void onStop() {
        super.onStop();
        exitThread = true;

    }
    @Override protected void onStart() {
        super.onStart();
        exitThread = false;
        refreshEmailAutomatically();
    }


    private void reloadUserEmail() {

            FirebaseManager.getFirebaseUserInstance().reload().addOnCompleteListener(emailReload -> {
                if (emailReload.isSuccessful() && FirebaseManager.getFirebaseUserInstance().isEmailVerified()) {
                    exitThread = true;
                    showMainScreenActivity();
                    return;
                }
                if (emailReload.getException() != null) {
                    handleEmailVerificationException(emailReload);
                }
            });

    }


    private void handleEmailVerificationException(Task<?> task){

        try {
            throw Objects.requireNonNull(task.getException());
        }catch (FirebaseNetworkException firebaseNetworkException) {
            exitThread = true;
            showNoInternetActivity();
        }catch (Exception ignored){}
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
            progressbarBinding.circularProgressBar.setVisibility(View.INVISIBLE);
        },DELAY_INTERVAL_FOR_MAIN_SCREEN_DIALOG);

    }

    @Override
    public void onBackPressed() {
       new CustomBackButton(this,this).applyDoubleClickToExit();
    }

    private void displayUserEmailToTextView() {
        viewModel.displayUserEmailToTextView().observe(this,userEmail->{
            emailDialogBinding.textViewEmail.setText(userEmail);
        });
    }

    public void resendEmailIsClicked(View view) {
        FirebaseManager.getFirebaseUserInstance().sendEmailVerification().addOnCompleteListener(task -> {
            startVerificationTimer();
            if (task.isSuccessful()) {
                customPopupDialog.showSuccessDialog("New email sent", getString(resendEmailSuccessMessage));
                return;
            }
            customPopupDialog.showWarningDialog("Please check your inbox", getString(resendEmailFailedMessage));
        });
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
        viewModel.timerOnRunning().observe(this, this::displayWhenVerificationTimerStarted);
        viewModel.timerOnFinished().observe(this,timer->{
            if(timer.getContentIfNotHandled()!=null) {
                displayWhenVerificationTimerIsFinished();
            }
        });
    }

    @Override
    protected void onDestroy() {
        exitThread = true;
        super.onDestroy();
        destroyBinding();
    }


}
