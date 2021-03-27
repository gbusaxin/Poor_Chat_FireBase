package com.example.poorchatfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.editTextUserNameSignUpActivity)
    EditText editTextUsername;
    @BindView(R.id.editTextEmailSignUpActivity)
    EditText editTextEmail;
    @BindView(R.id.editTextPasswordSignUpActivity)
    EditText editTextPassword;
    @BindView(R.id.editTextRepeatPasswordNameSignUpActivity)
    EditText editTextRepeatPass;
    @BindView(R.id.imageViewCircleUser)
    CircleImageView imageViewUser;

    @OnClick(R.id.linearLayoutSignUpActivity)
    void onClickSignUp(View view) {
        if (editTextPassword.getText().toString().equals(editTextRepeatPass.getText().toString())
                && !editTextPassword.getText().toString().equals("")
                && !editTextPassword.getText().toString().equals(null)
                && !editTextEmail.getText().toString().equals("")
                && !editTextEmail.getText().toString().equals(null)
                && !editTextUsername.getText().toString().equals("")
                && !editTextUsername.getText().toString().equals(null)) {
            signUp(editTextEmail.getText().toString(), editTextPassword.getText().toString(), editTextUsername.getText().toString());
        } else {
            Toast.makeText(this, "One of them is empty!", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.imageViewCircleUser)
    void onClickCircleImage(View view) {
        imageChooser();
    }

    private boolean imageControl;
    private Uri imageUri;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageViewUser);
            imageControl = true;
        } else {
            imageControl = false;
        }
    }

    public void imageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    public void signUp(String email, String password, String username) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                databaseReference.child("Users").child(mAuth.getUid()).child("userName").setValue(username);

                if (imageControl) {
                    UUID randomID = UUID.randomUUID();
                    String imageName = "images/" + randomID + ".jpg";
                    storageReference.child(imageName).putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                        StorageReference myStorageReference = storage.getReference(imageName);
                        myStorageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String filePath = uri.toString();
                            databaseReference.child("Users").child(mAuth.getUid()).child("image").setValue(filePath).addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Database is OK", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show();
                            });
                        });
                    });
                } else {
                    databaseReference.child("Users").child(mAuth.getUid()).child("image").setValue("null");
                }

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}