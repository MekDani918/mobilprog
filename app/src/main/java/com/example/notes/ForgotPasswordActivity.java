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

    Button sendLink;
    ProgressBar progressBar;
    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        sendLink=findViewById(R.id.sendLinkBtn);
        progressBar=findViewById(R.id.loginProgressBar);
        email=findViewById(R.id.emailEditText);

        sendLink.setOnClickListener(v->sendPwResetLink());
    }

    void sendPwResetLink()
    {
        String emailText=email.getText().toString();

        boolean isValid=validateData(emailText);
        if(!isValid)
        {
            return;
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.sendPasswordResetEmail(emailText).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                changeInProgress(false);
                if(task.isSuccessful())
                {
                    Toast.makeText(ForgotPasswordActivity.this, "E-mail sent", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                    finish();
                }
                else
                {
                    Toast.makeText(ForgotPasswordActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    void changeInProgress(boolean inProgress)
    {
        if(inProgress)
        {
            progressBar.setVisibility(View.VISIBLE);
            sendLink.setVisibility(View.GONE);
        }
        else
        {
            progressBar.setVisibility(View.GONE);
            sendLink.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String emailText)
    {
        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches())
        {
            email.setError("E-mail is invalid");
            return false;
        }
        return true;
    }
}