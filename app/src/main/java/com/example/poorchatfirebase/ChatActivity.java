package com.example.poorchatfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {

    private String userName, otherName;
    private MessageAdapter adapter;
    private List<ModelClass> list;

    private FirebaseDatabase database;
    private DatabaseReference reference;

    @BindView(R.id.textViewChatActivity)
    TextView textViewChat;

    @BindView(R.id.editTextInputMessageChatActivity)
    EditText editTextMessage;

    @BindView(R.id.rv_chatActivity)
    RecyclerView rvChat;

    @OnClick(R.id.linearLayoutBackStepChatActivity)
    void onClickBackChatActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.fab)
    void onClickSendMessageByFab(View view) {

        String message = editTextMessage.getText().toString();

        if (!message.equals("")) {
            sendMessage(message);
            editTextMessage.setText("");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        rvChat.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();

        userName = getIntent().getStringExtra("userName");
        otherName = getIntent().getStringExtra("otherName");
        textViewChat.setText(otherName);

        getMessage();
    }

    private void getMessage() {
        reference.child("Messages").child(userName).child(otherName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                ModelClass modelClass = snapshot.getValue(ModelClass.class);
                list.add(modelClass);
                adapter.notifyDataSetChanged();
                rvChat.scrollToPosition(list.size()-1);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter = new MessageAdapter(list,userName);
        rvChat.setAdapter(adapter);
    }

    public void sendMessage(String message) {
        String key = reference.child("Messages").child(userName).child(otherName).push().getKey();
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("message", message);
        messageMap.put("from", userName);
        reference.child("Messages").child(userName).child(otherName).child(key).setValue(messageMap).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                reference.child("Messages").child(otherName).child(userName).child(key).setValue(messageMap);
            }

        });
    }
}