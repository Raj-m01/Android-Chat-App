package com.example.chatapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.chatapp.databinding.ActivityMessagingBinding;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import adapters.messageAdapter;
import models.MessageModel;

public class MessagingActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    ActivityMessagingBinding activityMessagingBinding;
    public String receiverId;
    String receiverToken, senderName;
    String senderId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        activityMessagingBinding = ActivityMessagingBinding.inflate(getLayoutInflater());
        setContentView(activityMessagingBinding.getRoot());

        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                activityMessagingBinding.parentViewgroup.setBackground(AppCompatResources.getDrawable(MessagingActivity.this,R.drawable.wpdark));
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                activityMessagingBinding.parentViewgroup.setBackground(AppCompatResources.getDrawable(MessagingActivity.this,R.drawable.wplight));
                break;
        }

        senderId = firebaseAuth.getUid();

        Intent intent = getIntent();
        String uname = intent.getStringExtra("USERNAME");
        String profileImg = intent.getStringExtra("PROFILEIMAGE");
        receiverId = intent.getStringExtra("USERID");
        receiverToken = intent.getStringExtra("TOKEN");


        activityMessagingBinding.receiverName.setText(uname);
        Picasso.get().load(profileImg).fit().centerCrop()
                .error(R.drawable.user)
                .placeholder(R.drawable.user)
                .into(activityMessagingBinding.profilePicImageview);


        activityMessagingBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(MessagingActivity.this,MainActivity.class);
               startActivity(intent);
            }
        });


        final ArrayList<MessageModel> msgData = new ArrayList<>();
        final messageAdapter msgAdapter = new messageAdapter(msgData,MessagingActivity.this);
        activityMessagingBinding.msgRecyclerview.setAdapter(msgAdapter);
        activityMessagingBinding.msgRecyclerview.setLayoutManager(new LinearLayoutManager(this));


        firebaseDatabase.getReference("Users")
                .child(senderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){

                senderName = dataSnapshot.child("userName").getValue().toString();
                msgData.clear();

                for (DataSnapshot e : dataSnapshot.child("Contacts").child(receiverId).child("Chats").getChildren()){


                    String msg = e.child("msgText").getValue().toString();
                    
                    try {
                        decrypted = AESUtils.decrypt(msg);
                    } catch (Exception er) {
                        er.printStackTrace();
                    }

                    msgData.add(new MessageModel(e.child("uId").getValue().toString()
                            ,decrypted
                            ,(Long) Long.valueOf(e.child("msgTime").getValue().toString())));

                }

                msgAdapter.notifyDataSetChanged();
                activityMessagingBinding.msgRecyclerview.scrollToPosition(msgAdapter.getItemCount()-1);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });



//        FirebaseMessaging.getInstance().subscribeToTopic("all");

        //Messaging Mechanism
        activityMessagingBinding.sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = activityMessagingBinding.typingSpace.getText().toString().trim();
              
                String encryptedMsg = msg;
                try {
                    encryptedMsg = AESUtils.encrypt(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                long date = new Date().getTime();

                activityMessagingBinding.typingSpace.setText("");
                final MessageModel messageModel = new MessageModel(senderId, encryptedMsg, date);

                if(!msg.isEmpty()) {
                    firebaseDatabase.getReference("Users").child(senderId).child("Contacts")
                            .child(receiverId).child("Chats").push()
                            .setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            activityMessagingBinding.msgRecyclerview.scrollToPosition(msgAdapter.getItemCount()-1);

                            FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender(receiverToken,senderName
                                    ,msg,getApplicationContext(),MessagingActivity.this);
                            fcmNotificationsSender.SendNotifications();

                            firebaseDatabase.getReference("Users").child(receiverId).child("Contacts").child(senderId)
                                    .child("interactionTime").setValue(date);

                            firebaseDatabase.getReference("Users").child(senderId).child("Contacts").child(receiverId)
                                    .child("interactionTime").setValue(date);


                            firebaseDatabase.getReference("Users").child(receiverId).child("Contacts")
                                    .child(senderId).child("Chats").push()
                                    .setValue(messageModel);

                            firebaseDatabase.getReference("Users").child(senderId).child("Contacts").child(receiverId)
                                    .child("recentMessage").setValue(msg);

                            firebaseDatabase.getReference("Users").child(receiverId).child("Contacts").child(senderId)
                                    .child("recentMessage").setValue(msg);


                        }
                    });
                }


            }
        });



        activityMessagingBinding.msgRecyclerview.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override

            public void onLayoutChange(View v, int left, int top, int right,int bottom, int oldLeft, int oldTop,int oldRight, int oldBottom)
            {

                if ( bottom < oldBottom) {
                    activityMessagingBinding.msgRecyclerview.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if((msgAdapter.getItemCount()-1)>1)
                            activityMessagingBinding.msgRecyclerview.smoothScrollToPosition(msgAdapter.getItemCount()-1);
                        }
                    }, 10);
                }

            }
        });

    }




}
