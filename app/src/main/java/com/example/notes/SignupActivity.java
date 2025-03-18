package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    EditText emailEditText, pwEditText, confirmPwEditText;
    Button createAccountBtn;
    ProgressBar signupProgressBar;
    TextView alreadyLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailEditText = findViewById(R.id.emailEditText);
        pwEditText = findViewById(R.id.pwEditText);
        confirmPwEditText = findViewById(R.id.confirmPwEditText);
        createAccountBtn = findViewById(R.id.createAccountBtn);
        signupProgressBar = findViewById(R.id.signupProgressBar);
        alreadyLogIn = findViewById(R.id.alreadyLogIn);

        createAccountBtn.setOnClickListener(v->createAccount());
        alreadyLogIn.setOnClickListener(v->finish());
    }

    void createAccount() {
        String emailString = emailEditText.getText().toString();
        String passwdString = pwEditText.getText().toString();
        String confirmString = confirmPwEditText.getText().toString();

        boolean isValid=validateData(emailString, passwdString, confirmString);
        if(!isValid) {
            return;
        }

        createFirebaseAccount(emailString, passwdString);
    }

    void createFirebaseAccount(String emailString, String passwdString) {
        changeInProgress(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(emailString, passwdString).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if(task.isSuccessful()) {
                    Toast.makeText(SignupActivity.this, "Account created, check emails to verify", Toast.LENGTH_LONG).show();
                    firebaseAuth.getCurrentUser().sendEmailVerification();
                    firebaseAuth.signOut();
                    finish();
                }
                else {
                    Toast.makeText(SignupActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    void changeInProgress(boolean inProgress) {
        if(inProgress) {
            signupProgressBar.setVisibility(View.VISIBLE);
            createAccountBtn.setVisibility(View.GONE);
        }
        else {
            signupProgressBar.setVisibility(View.GONE);
            createAccountBtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String emailString, String passwdString, String confirmString) {
        if(!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            emailEditText.setError("Email invalid");
            return false;
        }
        if(passwdString.length()<6) {
            pwEditText.setError("Password too short");
            return false;
        }
        if(!passwdString.equals(confirmString)) {
            confirmPwEditText.setError("Passwords don't match");
            return false;
        }
        return true;
    }
}