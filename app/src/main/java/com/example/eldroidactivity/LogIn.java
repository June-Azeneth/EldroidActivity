package com.example.eldroidactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogIn extends AppCompatActivity {

    final String TAG = "FIRESTORE";

    FirebaseFirestore db;
    FirebaseAuth auth;

    Intent gotoHomePage,navToSignUp;

    Button login;
    TextView toSignUp;
    TextInputEditText email,password;
    TextInputLayout emailLO,passwordLO;
    ProgressBar progressBar_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        login = findViewById(R.id.loginbtn);
        toSignUp = findViewById(R.id.navToSignUp);
        email = findViewById(R.id.txtEditUsername_LogIn);
        password = findViewById(R.id.txtEditPass_login);

        emailLO = findViewById(R.id.txtInputUsername_LogIn);

        progressBar_login = findViewById(R.id.progressBar_login);

        //Nav to Sign Up page
        toSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigation();
            }
        });

        //Login User
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailInput = email.getText().toString();
                String passInput = password.getText().toString();

                if(TextUtils.isEmpty(emailInput) || TextUtils.isEmpty(passInput)){
                    Toast.makeText(getApplicationContext(),"Fill in all the required feilds", Toast.LENGTH_SHORT).show();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
                    emailLO.setErrorEnabled(true);
                    emailLO.setError("Invalid email");
                    return;
                }
                else {
                    emailLO.setErrorEnabled(false);

                    progressBar_login.setVisibility(View.VISIBLE);
                    logIn(emailInput, passInput);
                }
            }
        });

    }

    protected void navigation(){
        navToSignUp = new Intent(LogIn.this,SignUp.class);
        startActivity(navToSignUp);
    }

    protected void logIn(String email,String password){
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LogIn.this, "User Logged In", Toast.LENGTH_SHORT).show();
                    gotoHomePage = new Intent(LogIn.this,dashboard.class);
                    startActivity(gotoHomePage);
                    progressBar_login.setVisibility(View.GONE);
                } else {
                    Toast.makeText(LogIn.this, "Error Occurred Logging In" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar_login.setVisibility(View.GONE);
                }
            }
        });
    }
}