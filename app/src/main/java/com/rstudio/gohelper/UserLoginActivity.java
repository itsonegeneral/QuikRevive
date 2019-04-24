package com.rstudio.gohelper;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserLoginActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private TextView tvForgotPass, tvCreateAccount, tvAdminogin;
    private Button btLogin;
    private EditText etUserName, etPassword;
    private ProgressBar pgBarLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        setValues();
        setListeners();
        setToolbar();

        //Auto login if already signed in
        if(mUser!= null){
            Toast.makeText(this,"Logged in",Toast.LENGTH_SHORT).show();
        }

    }

    private void setListeners() {
        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserLoginActivity.this, ForgotPasswordActivity.class));
                overridePendingTransition(0, 0);
            }
        });
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUserName.getText().toString();
                String password = etPassword.getText().toString();
                if (checkUserInput()) {
                    btLogin.setEnabled(false);
                    pgBarLogin.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(UserLoginActivity.this, "Welcome !", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(UserLoginActivity.this, UserHomeActivity.class));
                                finish();
                                btLogin.setEnabled(true);
                                pgBarLogin.setVisibility(View.GONE);
                            } else {
                                TextView forgot = findViewById(R.id.tv_forgotPasswordUser);
                                forgot.setVisibility(View.VISIBLE);
                                Toast.makeText(UserLoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                btLogin.setEnabled(true);
                                pgBarLogin.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

        tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserLoginActivity.this,CreateUserActivity.class));
            }
        });
    }

    private boolean checkUserInput() {
        String user = etUserName.getText().toString();
        String pass = etPassword.getText().toString();
        if (user.isEmpty()) {
            etUserName.setError("Enter Email");
        } else if (pass.isEmpty()) {
            etPassword.setError("Enter Password");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(user).matches()) {
            etUserName.setError("Enter Valid Email");
        } else if (pass.length() < 6) {
            etPassword.setError("Min 6 Chars");
        } else {
            return true;
        }
        return false;
    }

    private void setValues() {
        tvForgotPass = findViewById(R.id.tv_forgotPasswordUser);
        tvCreateAccount = findViewById(R.id.tv_createNewAccountUser);
        tvAdminogin = findViewById(R.id.tv_adminLogin);
        btLogin = findViewById(R.id.bt_loginUser);
        etPassword = findViewById(R.id.et_passwordUser);
        etUserName = findViewById(R.id.et_usernameUser);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        pgBarLogin = findViewById(R.id.pgBar_UserLogin);

    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_userLogin);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        TextView tv = findViewById(R.id.tv_toolbarHeading);
        tv.setText("Quik Revive");
    }
}
