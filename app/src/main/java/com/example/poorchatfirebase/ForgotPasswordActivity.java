package com.example.poorchatfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @BindView(R.id.editTextEmailForRestorePassword)
    EditText editTextEmail;

    @OnClick(R.id.linearLayoutResetActivity) void onClickResetPassword(View view){
        if (!editTextEmail.getText().toString().equals("")
                && !editTextEmail.getText().toString().equals(null)) {
            passwordReset(editTextEmail.getText().toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
    }

    public void passwordReset(String email ){
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Toast.makeText(this, "Check your Email!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "This Email doesn't exist!", Toast.LENGTH_SHORT).show();
                editTextEmail.setText("");
            }
        });
    }
}