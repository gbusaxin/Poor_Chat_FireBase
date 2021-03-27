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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.imageViewCircleUserProfile)
    CircleImageView imageViewUser;
    @BindView(R.id.editTextUsernameProfileUpdate)
    EditText editTextUsername;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String image;

    private boolean imageControl = false;
    private Uri imageUri;

    @OnClick(R.id.linearLayoutUpdateProfile)
    void onClickUpdateProfileActivity(View view) {
        updateProfile();
    }

    @OnClick(R.id.imageViewCircleUserProfile)
    void onClickUserProfile(View view) {
        imageChooser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        getUserInfo();
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

    public void updateProfile(){
        String username = editTextUsername.getText().toString();
        reference.child("Users").child(firebaseUser.getUid()).child("userName").setValue(username);

        if (imageControl) {
            UUID randomID = UUID.randomUUID();
            String imageName = "images/" + randomID + ".jpg";
            storageReference.child(imageName).putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                StorageReference myStorageReference = storage.getReference(imageName);
                myStorageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String filePath = uri.toString();
                    reference.child("Users").child(mAuth.getUid()).child("image").setValue(filePath).addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Database is OK", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show();
                    });
                });
            });
        } else {
            reference.child("Users").child(mAuth.getUid()).child("image").setValue(image);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("userName", username);
        startActivity(intent);
        finish();
    }

    public void getUserInfo() {
        reference.child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String name = snapshot.child("userName").getValue().toString();
                image = snapshot.child("image").getValue().toString();
                editTextUsername.setText(name);

                if (image.equals("null")) {
                    imageViewUser.setImageResource(R.drawable.user);
                } else {
                    Picasso.get().load(image).into(imageViewUser);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void imageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }
}