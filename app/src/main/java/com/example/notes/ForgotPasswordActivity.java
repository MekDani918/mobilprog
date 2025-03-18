package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    Button sendLinkBtn;
    ProgressBar loginProgressBar;
    EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        sendLinkBtn = findViewById(R.id.sendLinkBtn);
        loginProgressBar = findViewById(R.id.loginProgressBar);
        emailEditText = findViewById(R.id.emailEditText);

        sendLinkBtn.setOnClickListener(v->sendPwResetLink());
    }

    void sendPwResetLink() {
        String emailString = emailEditText.getText().toString();

        boolean isValid = validateData(emailString);
        if(!isValid) {
            return;
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.sendPasswordResetEmail(emailString).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                changeInProgress(false);
                if(task.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Check your emails", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                    finish();
                }
                else {
                    Toast.makeText(ForgotPasswordActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    void changeInProgress(boolean inProgress)
    {
        if(inProgress) {
            loginProgressBar.setVisibility(View.VISIBLE);
            sendLinkBtn.setVisibility(View.GONE);
        }
        else {
            loginProgressBar.setVisibility(View.GONE);
            sendLinkBtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String emailText)
    {
        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            emailEditText.setError("E-mail is invalid");
            return false;
        }
        return true;
    }
}