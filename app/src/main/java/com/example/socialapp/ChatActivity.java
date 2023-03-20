package com.example.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialapp.Adapter.MessageAdapter;
import com.example.socialapp.Adapter.StoryAdapter;
import com.example.socialapp.Model.Chat;
import com.example.socialapp.Model.Post;
import com.example.socialapp.Model.Story;
import com.example.socialapp.Model.UserStories;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseAuth auth;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_message2);
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        mchat = new ArrayList<>();


        String receiver = getIntent().getStringExtra("receiver");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String username = getIntent().getStringExtra("username");
        TextView user_name_title = (TextView) findViewById(R.id.user_name2);
        CircleImageView user_avatar = (CircleImageView) findViewById(R.id.profileImage2);
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .into(user_avatar);

        user_name_title.setText(username);



        database.getReference()
                .child("Chats").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            mchat.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                Chat chat = dataSnapshot.getValue(Chat.class);
                                if (chat.getReceiver().equals(auth.getUid()) && chat.getSender().equals(receiver) ||
                                        chat.getReceiver().equals(receiver) && chat.getSender().equals(auth.getUid())){
                                    mchat.add(chat);
                                }
                            }
                            // Initialize adapter and set up recyclerView
                            MessageAdapter adapter = new MessageAdapter(ChatActivity.this, mchat, imageUrl, username);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false);
                            recyclerView = findViewById(R.id.messages_view);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            layoutManager.scrollToPositionWithOffset(mchat.size()-1, 0);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        EditText editText = (EditText) findViewById(R.id.text_send2);
        ImageButton btnSend = (ImageButton) findViewById(R.id.btn_send2);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(FirebaseAuth.getInstance().getUid(), receiver, editText.getText().toString());
                Toast.makeText(getApplicationContext(), "Message sent: " + editText.getText(), Toast.LENGTH_SHORT).show();
            }
        });








        //readMesagges(FirebaseAuth.getInstance().getUid(), "GcKPiLJjG1X9VxMPwPx1qj0gCZx2", "");
    }

    /*private void readMesagges(final String myid, final String userid, final String imageurl){
        mchat = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        mchat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(ChatActivity.this, mchat, imageurl);
                    //recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

    private void sendMessage(String sender, final String receiver, String message){

        DatabaseReference reference = database.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);

        reference.child("Chats").push().setValue(hashMap);


        // add user to chat fragment
        /*final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid())
                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(userid)
                .child(fuser.getUid());
        chatRefReceiver.child("id").setValue(fuser.getUid());

        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    sendNotifiaction(receiver, user.getUsername(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }
}