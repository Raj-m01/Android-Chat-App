package com.example.chatapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chatapp.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import adapters.chatPageAdapter;
import models.UserModel;


public class MainActivity extends AppCompatActivity {


    FirebaseAuth myAuth;
    ActivityMainBinding activityMainBinding;
    FirebaseDatabase firebaseDatabase;
    SharedPreferences sharedPreferences;
    final ExecutorService executorService = Executors.newSingleThreadExecutor();
    chatPageAdapter chatPageAdapter;
    ArrayList<UserModel> userData = new ArrayList<>();
    Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        firebaseDatabase = FirebaseDatabase.getInstance();
        myAuth = FirebaseAuth.getInstance();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        myToolbar = activityMainBinding.myToolbar;
        myToolbar.inflateMenu(R.menu.main_menu);

        sharedPreferences = getSharedPreferences("SavedToken",MODE_PRIVATE);
        String tokenInMain =  sharedPreferences.getString("ntoken","mynull");

        firebaseDatabase.getReference("Users").child(myAuth.getUid()).child("Token for notifications").setValue(tokenInMain);

        setContentView(activityMainBinding.getRoot());

        activityMainBinding.tutorial.setVisibility(View.GONE);

        if(!isOnline()){
            Toast.makeText(MainActivity.this, "Check Internet Connection", Toast.LENGTH_LONG).show();
        }

        myToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

               Intent intent  = new Intent(MainActivity.this,SettingActivity.class);
               startActivity(intent);

                return true;
            }
        });


        activityMainBinding.moveToContactlistFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, ContactListsActivity.class);
                startActivity(intent);

            }
        });


        chatPageAdapter = new chatPageAdapter(userData, MainActivity.this);
        executorService.execute(new Runnable() {
            @Override
            public void run() {



                firebaseDatabase.getReference("Users").addValueEventListener(new ValueEventListener() {



                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        userData.clear();
                        ArrayList<String> contactIds = new ArrayList<>();
                        ArrayList<Long> recentMsgTimes = new ArrayList<>();
                        ArrayList<String> recentMsg = new ArrayList<>();



                        for (DataSnapshot e : snapshot.child(myAuth.getUid()).child("Contacts").getChildren()){
                            contactIds.add(e.getKey());


                            if(e.hasChild("interactionTime")) {
                                recentMsgTimes.add((long)e.child("interactionTime").getValue());
                            }

                            if(e.hasChild("recentMessage")){
                                recentMsg.add(e.child("recentMessage").getValue().toString());
                            }

                        }

                        if(contactIds.isEmpty()){
                            activityMainBinding.tutorial.setVisibility(View.VISIBLE);
                        }else{
                            activityMainBinding.tutorial.setVisibility(View.GONE);

                        }


                        for(int i=0;i<contactIds.size();i++) {

                            String e = contactIds.get(i);
                            long time = 0;
                            String recentmsg = "";

                            try{
                                if(!recentMsgTimes.isEmpty()){time = recentMsgTimes.get(i);}
                                if(!recentMsg.isEmpty()){recentmsg = recentMsg.get(i);}
                            }catch (IndexOutOfBoundsException err){

                            }




                            String uName = snapshot.child(e).child("userName").getValue().toString();
                            String uMail = snapshot.child(e).child("userMail").getValue().toString();
                            String uPic = snapshot.child(e).child("profilePic").getValue().toString();
                            String token = snapshot.child(e).child("Token for notifications").getValue().toString();

                            UserModel model = new UserModel(uName, uMail, uPic);
                            model.setUserId(e);
                            model.setRecentMsgTime(time);
                            model.setToken(token);
                            model.setRecentMessage(recentmsg);
                            userData.add(model);
                            chatPageAdapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

//        Drawable drawable =  ContextCompat.getDrawable(MainActivity.this,R.drawable.divider);
        DividerItemDecoration decoration = new DividerItemDecoration(activityMainBinding.chatsRecyclerview.getContext(), DividerItemDecoration.VERTICAL);
        activityMainBinding.chatsRecyclerview.addItemDecoration(decoration);
        activityMainBinding.chatsRecyclerview.setLayoutManager(new LinearLayoutManager(MainActivity.this));


        activityMainBinding.chatsRecyclerview.setAdapter(chatPageAdapter);


        chatPageAdapter.setOnItemClickListener(new chatPageAdapter.OnClickListener() {
            @Override
            public void onItemClick(UserModel userdata) {


                Intent intent = new Intent(MainActivity.this, MessagingActivity.class);
                intent.putExtra("USERNAME", userdata.getUserName());
                intent.putExtra("PROFILEIMAGE", userdata.getProfilePic());
                intent.putExtra("USERID", userdata.getUserId());
                intent.putExtra("TOKEN", userdata.getToken());
                startActivity(intent);


            }
        });


    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}
