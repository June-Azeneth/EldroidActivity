package com.example.eldroidactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth auth;
    TextInputEditText username, email, phone, address, pass, confpass;
    TextInputLayout usernameLO, emailLO, phoneLO, addressLO, passLO, confpassLO;
    Button signUp;
    TextView navToLogIn,successMsg,failedMsg;
    String userID;
    Intent toLogin, toDashboard;
    ProgressBar progressBarSignUp;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //Edit Texts
        username = findViewById(R.id.txtEditUsername);
        email = findViewById(R.id.txtEditEmail);
        phone = findViewById(R.id.txtEditPhone);
        address = findViewById(R.id.txtEditAddress);
        pass = findViewById(R.id.txtEditPass);
        confpass = findViewById(R.id.txtEditConfPass);

        //Input Layouts
        usernameLO = findViewById(R.id.txtInputUsername);
        emailLO = findViewById(R.id.txtInputEmail);
        phoneLO = findViewById(R.id.txtInputPhone);
        addressLO = findViewById(R.id.txtInputAddress);
        passLO = findViewById(R.id.txtInputPass);
        confpassLO = findViewById(R.id.txtInputConfPass);

        //Navigations
        signUp = findViewById(R.id.sign_up);
        navToLogIn = findViewById(R.id.toLogIn);

        //Other
        progressBarSignUp = findViewById(R.id.progressBar_signup);

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        //Nav to Login
        navToLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toLogin = new Intent(SignUp.this, LogIn.class);
                startActivity(toLogin);
            }
        });

        //Create User
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameInput = username.getText().toString();
                String emailInput = email.getText().toString().trim();
                String phoneInput = phone.getText().toString();
                String addressInput = address.getText().toString();
                String passInput = pass.getText().toString();
                String confPassInput = confpass.getText().toString();

                if (TextUtils.isEmpty(usernameInput) || TextUtils.isEmpty(emailInput) || TextUtils.isEmpty(phoneInput) || TextUtils.isEmpty(addressInput) || TextUtils.isEmpty(passInput) || TextUtils.isEmpty(confPassInput)) {
                    Toast.makeText(getApplicationContext(), "Fill in all the required feilds", Toast.LENGTH_SHORT).show();
                } else if (usernameInput.length() < 6) {
                    usernameLO.setErrorEnabled(true);
                    usernameLO.setError("Username should be more than 6 characters");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                    emailLO.setErrorEnabled(true);
                    emailLO.setError("Invalid email");
                } else if (!passInput.contains(confPassInput)) {
                    passLO.setErrorEnabled(true);
                    confpassLO.setErrorEnabled(true);
                    confpassLO.setError("Passwords don't match");
                } else if (passInput.length() < 6) {
                    pass.setError("Password must be 6 characters long");
                } else {
                    usernameLO.setErrorEnabled(false);
                    emailLO.setErrorEnabled(false);
                    phoneLO.setErrorEnabled(false);
                    addressLO.setErrorEnabled(false);
                    passLO.setErrorEnabled(false);
                    confpassLO.setErrorEnabled(false);

                    progressBarSignUp.setVisibility(View.VISIBLE);
                    registerUser(usernameInput, emailInput, phoneInput, addressInput, confPassInput);
                }
            }
        });
    }

    protected void registerUser(String username, String email, String phone, String address, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    userID = auth.getCurrentUser().getUid();
                    Map<String, Object> user = new HashMap<>();
                    user.put("username", username);
                    user.put("password", password);
                    user.put("email", email);
                    user.put("phone", phone);
                    user.put("address", address);

                    // Add a new document with a generated ID
                    db.collection("EldroidAct").document(userID)
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(SignUp.this, "Successfully Added " + username, Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignUp.this, "Error adding user " + e, Toast.LENGTH_SHORT).show();
                                }
                            });
                    Toast.makeText(SignUp.this, "Successful Registration", Toast.LENGTH_SHORT).show();
                    toDashboard = new Intent(SignUp.this, dashboard.class);
                    startActivity(toDashboard);
                    progressBarSignUp.setVisibility(View.GONE);
                } else {
                    Toast.makeText(SignUp.this, "Error Occurred While Signing Up" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBarSignUp.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showAlertDiag(int layout){
//        builder = new AlertDialog.Builder(this);
//        View layoutView = getLayoutInflater().inflate(layout,null);
//
//        AppCompatButton diagBtn = layoutView.findViewById(R.id.successBtn);
//        builder.setView(layoutView);
//        alertDialog = builder.create();
//        alertDialog.show();
//
//        diagBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                alertDialog.dismiss();
//            }
//        });
    }
}