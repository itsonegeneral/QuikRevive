package com.rstudio.gohelper;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPasswordActivity extends AppCompatActivity {


    private ImageButton btReset;
    private ProgressBar pgbar;
    private EditText etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.et_forgotPasswordEmail);
        btReset = findViewById(R.id.bt_ResetUserPassword);
        pgbar = findViewById(R.id.pgBar_ForgotPassword);

        btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etEmail.getText().toString();
                if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etEmail.setError("Enter Valid Email");
                } else {
                    pgbar.setVisibility(View.VISIBLE);
                    //send reset email
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ForgotPasswordActivity.this, "Reset Email Sent", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(ForgotPasswordActivity.this, "Reset Email Not Sent", Toast.LENGTH_SHORT).show();
                            }
                            pgbar.setVisibility(View.GONE);
                        }
                    });

                }
            }
        });

        setToolbar();

    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_forgotPassword);
        TextView tv = findViewById(R.id.tv_toolbarHeading);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
