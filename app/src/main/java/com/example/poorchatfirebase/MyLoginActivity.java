package com.example.poorchatfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyLoginActivity extends AppCompatActivity {

    @BindView(R.id.editTextEmailMyLoginActivity)
    EditText editTextEmail;
    @BindView(R.id.editTextPasswordMyLoginActivity)
    EditText editTextPassword;
    @BindView(R.id.textViewForgotPasswordMyLoginActivity)
    TextView textViewForgotPassword;
    @BindView(R.id.textViewSignUpMyLoginActivity)
    TextView textViewSignUp;

    private FirebaseAuth mAuth;

    @OnClick(R.id.linearLayoutNextMyLoginActivity)
    void onClickNextMyLoginActivity(View view) {

        if (!editTextEmail.getText().toString().equals("")
                && !editTextPassword.getText().toString().equals("")
                && !editTextEmail.getText().toString().equals(null)
                && !editTextPassword.getText().toString().equals(null)){
            singIn(editTextEmail.getText().toString(),editTextPassword.getText().toString());
        }else {
            Toast.makeText(this, "Wrong data!", Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick({R.id.textViewSignUpMyLoginActivity, R.id.textViewForgotPasswordMyLoginActivity})
    void onClickSignUpOrForgotMyLoginActivity(View view) {
        switch (view.getId()) {
            case R.id.textViewSignUpMyLoginActivity:
                Intent intent1 = new Intent(MyLoginActivity.this, SignUpActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.textViewForgotPasswordMyLoginActivity:
                Intent intent2 = new Intent(MyLoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent2);
                break;
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_login);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
    }

    public void singIn(String email,String password){
        Intent intent = new Intent(MyLoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                startActivity(intent);
            }else{
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}