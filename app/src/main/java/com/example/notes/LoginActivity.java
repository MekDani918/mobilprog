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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText email, pw;
    Button loginBtn;
    ProgressBar progressBar;
    TextView noAccountSignup;
    TextView forgotPasswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email=findViewById(R.id.emailEditText);
        pw=findViewById(R.id.pwEditText);
        loginBtn=findViewById(R.id.loginBtn);
        progressBar=findViewById(R.id.loginProgressBar);
        noAccountSignup=findViewById(R.id.noAccountSignup);
        forgotPasswordBtn=findViewById(R.id.forgotPasswordBtn);

        loginBtn.setOnClickListener(v->loginUser());
        noAccountSignup.setOnClickListener(v->startActivity(new Intent(LoginActivity.this, SignupActivity.class)));
        forgotPasswordBtn.setOnClickListener(v->startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));
    }

    void loginUser()
    {
        String emailText=email.getText().toString();
        String pwText=pw.getText().toString();

        boolean isValid=validateData(emailText, pwText);
        if(!isValid)
        {
            return;
        }

        loginFirebase(emailText, pwText);
    }

    void loginFirebase(String emailText, String pwText)
    {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(emailText, pwText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if(task.isSuccessful())
                {
                    if(firebaseAuth.getCurrentUser().isEmailVerified())
                    {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "E-mail address not verified", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    void changeInProgress(boolean inProgress)
    {
        if(inProgress)
        {
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        }
        else
        {
            progressBar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String emailText, String pwText)
    {
        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches())
        {
            email.setError("E-mail is invalid");
            return false;
        }
        if(pwText.length()<6)
        {
            pw.setError("Password too short");
            return false;
        }
        return true;
    }
}