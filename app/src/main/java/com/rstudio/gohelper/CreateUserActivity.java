package com.rstudio.gohelper;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateUserActivity extends AppCompatActivity {

    private EditText etName,etPhone,etPinCode,etAddress;
    private EditText etAge,etAadharNo,etEmail,etPass1,etPass2;
    private Spinner spinner;
    private Button btCreateAccount;
    private String name,address,district,pass1,pass2,email;
    private int age=0,pincode=0;
    private long aadharno=0,phone=0;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        setToolbar();
        setValues();
        setSpinner();
        btCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etName.getText().toString();
                address = etAddress.getText().toString();
                district = spinner.getSelectedItem().toString();
                phone = Long.parseLong(etPhone.getText().toString());
                aadharno = Long.parseLong(etAadharNo.getText().toString());
                age = Integer.parseInt(etAge.getText().toString());
                pincode = Integer.parseInt(etPinCode.getText().toString());

                email = etEmail.getText().toString();
                pass1 = etPass1.getText().toString();
                pass2 = etPass2.getText().toString();

                if(checkUserInput()){
                    mAuth.createUserWithEmailAndPassword(email,pass1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                uploadUserDetails();
                            }else{
                                Toast.makeText(CreateUserActivity.this, "Error 403", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if(e instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(getApplicationContext(),"Email Already Registered",Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    });
                }
            }
        });
    }


    private void uploadUserDetails(){
        String userId = mAuth.getUid();
        DatabaseReference userRef = dbRef.child(userId);
        User user = new User();
        user.setAadharno(aadharno);
        user.setPhone(phone);
        user.setPincode(pincode);
        user.setAge(age);
        user.setName(name);
        user.setAddress(address);
        userRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Account Created",Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(CreateUserActivity.this, "Data Upload Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean checkUserInput(){

        if(name.isEmpty()){
            etName.setError("Enter Name");
        }else if(age==0){
            etAge.setError("Enter Age");
        }else if(phone ==0 ){
            etPhone.setError("Enter Phone");
        }else if(aadharno == 0 ){
            etAadharNo.setError("Enter Aadhar No");
        }else if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("Enter Valid Email");
        }else if(pass1.isEmpty()){
            etPass1.setError("Enter Password");
        }else if(pass2.isEmpty()){
            etPass2.setError("Confirm Password");
        }else if(!pass1.equals(pass2)){
            etPass2.setError("Passwords Do not Match");
        }else if(address.isEmpty()){
            etAddress.setError("Enter Address");
        }else if(pincode ==0 ){
            etPinCode.setError("Enter Pincode");
        }else{
            return true;
        }
        return false;
    }

    private void setValues(){

        etName = findViewById(R.id.et_nameCreateUser);
        etAadharNo = findViewById(R.id.et_aadharnoCreateUser);
        etPinCode = findViewById(R.id.et_pincodeCreateUser);
        etAge = findViewById(R.id.et_ageCreateUser);
        etAddress = findViewById(R.id.et_addressCreateUser);
        etPhone = findViewById(R.id.et_phoneCreateUser);
        etEmail  = findViewById(R.id.et_emailCreateUser);
        etPass1 = findViewById(R.id.et_pass1CreateUser);
        etPass2 = findViewById(R.id.et_pass2CreateUser);

        btCreateAccount = findViewById(R.id.bt_createUserAccount);
        spinner = findViewById(R.id.spinner_createUserAccount);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("users");


    }
    private void setSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.district_array,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_createUserAccount);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        TextView tv = findViewById(R.id.tv_toolbarHeading);
        tv.setText("Create New Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}

