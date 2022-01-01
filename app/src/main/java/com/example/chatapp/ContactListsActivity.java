package com.example.chatapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.chatapp.databinding.ActivityContactListsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import models.UserModel;

public class ContactListsActivity extends AppCompatActivity {

    ActivityContactListsBinding activityContactListsBinding;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    ArrayList<UserModel> searchedUser = new ArrayList<>(1);


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }

        activityContactListsBinding = ActivityContactListsBinding.inflate(getLayoutInflater());
        setContentView(activityContactListsBinding.getRoot());
        activityContactListsBinding.newUserDisplay.setVisibility(View.GONE);


        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        activityContactListsBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                activityContactListsBinding.searchView.clearFocus();
                searchedUser.clear();

              firebaseDatabase.getReference("Users").addValueEventListener(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot snapshot) {

                      boolean flag = false;

                      for(DataSnapshot e : snapshot.getChildren()){

                                flag = e.getKey().equals(firebaseAuth.getCurrentUser().getUid());


                          if(!flag && e.child("userMail").getValue().equals(query.trim()) ) {


                              Log.d("testcontact"," "+query+"  name= "+e.child("userName").getValue().toString());
                              UserModel userModel = new UserModel();
                              userModel.setUserName(e.child("userName").getValue().toString());

                              userModel.setUserId(e.getKey());
                              searchedUser.add(userModel);

                              activityContactListsBinding.userName.setText(e.child("userName").getValue().toString());
                              activityContactListsBinding.usermail.setText(e.child("userMail").getValue().toString());
                              String pic = e.child("profilePic").getValue().toString();
                              Picasso.get().load(pic)
                                      .fit()
                                      .centerCrop()
                                      .error(R.drawable.user)
                                      .placeholder(R.drawable.user)
                                      .into(activityContactListsBinding.profilePicImageview);

                              activityContactListsBinding.newUserDisplay.setVisibility(View.VISIBLE);

                              activityContactListsBinding.addContactBtn.setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {

                                      Toast.makeText(ContactListsActivity.this, "Contact added", Toast.LENGTH_SHORT).show();

                                      String userId = searchedUser.get(0).getUserId();

                                      firebaseDatabase.getReference("Users").child(firebaseAuth.getCurrentUser().getUid())
                                              .child("Contacts").child(userId).setValue("Chats");
                                      searchedUser.clear();

                                      firebaseDatabase.getReference("Users").child(firebaseAuth.getUid()).child("Contacts").child(userId)
                                              .child("interactionTime").setValue(new Date().getTime());
                                      firebaseDatabase.getReference("Users").child(firebaseAuth.getUid()).child("Contacts").child(userId)
                                              .child("recentMessage").setValue("");



                                  }
                              });

                          }
                      }

                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError error) {
                      searchedUser.clear();
                  }
              });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });





    }


}