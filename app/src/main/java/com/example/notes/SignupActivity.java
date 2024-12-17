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

import org.w3c.dom.Text;

public class SignupActivity extends AppCompatActivity {

    EditText email, pw, confirmPw;
    Button createAccountBtn;
    ProgressBar progressBar;
    TextView alreadyLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email=findViewById(R.id.emailEditText);
        pw=findViewById(R.id.pwEditText);
        confirmPw=findViewById(R.id.confirmPwEditText);
        createAccountBtn=findViewById(R.id.createAccountBtn);
        progressBar=findViewById(R.id.signupProgressBar);
        alreadyLogIn=findViewById(R.id.alreadyLogIn);

        createAccountBtn.setOnClickListener(v->createAccount());
        alreadyLogIn.setOnClickListener(v->finish());
    }

    void createAccount()
    {
        String emailText=email.getText().toString();
        String pwText=pw.getText().toString();
        String confirmPwText=confirmPw.getText().toString();

        boolean isValid=validateData(emailText, pwText, confirmPwText);
        if(!isValid)
        {
            return;
        }

        createFirebaseAccount(emailText, pwText);
    }

    void createFirebaseAccount(String emailText, String pwText)
    {
        changeInProgress(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(emailText, pwText).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if(task.isSuccessful())
                {
                    Toast.makeText(SignupActivity.this, "Account created, check e-mail to verify", Toast.LENGTH_LONG).show();
                    firebaseAuth.getCurrentUser().sendEmailVerification();
                    firebaseAuth.signOut();
                    finish();
                }
                else
                {
                    Toast.makeText(SignupActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    void changeInProgress(boolean inProgress)
    {
        if(inProgress)
        {
            progressBar.setVisibility(View.VISIBLE);
            createAccountBtn.setVisibility(View.GONE);
        }
        else
        {
            progressBar.setVisibility(View.GONE);
            createAccountBtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String emailText, String pwText, String confirmPwText)
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
        if(!pwText.equals(confirmPwText))
        {
            confirmPw.setError("Passwords don't match");
            return false;
        }
        return true;
    }
}