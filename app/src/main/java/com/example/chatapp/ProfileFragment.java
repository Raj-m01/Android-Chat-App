package com.example.chatapp;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.chatapp.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


public class ProfileFragment extends Fragment {

   FirebaseAuth firebaseAuth;
   FirebaseDatabase firebaseDatabase;
   FragmentProfileBinding binding;
   FragmentManager fragmentManager;
   FirebaseStorage firebaseStorage;

    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        fragmentManager = getChildFragmentManager();

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();



        String uid = firebaseAuth.getUid();

        binding.uid.setText(uid);

        binding.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.editFragContainer.setVisibility(View.VISIBLE);
                binding.editFragContainer.bringToFront();
                binding.uid.setVisibility(View.GONE);

                binding.text.setText("Enter new name");

                binding.edittext.requestFocus();
                binding.saveEditBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String ss = binding.edittext.getText().toString().trim();




                        firebaseDatabase.getReference("Users").child(firebaseAuth.getUid()).child("userName").setValue(ss).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                Toast.makeText(getContext(), "Username editted ", Toast.LENGTH_SHORT).show();
                                binding.edittext.setText("");
                                binding.editFragContainer.setVisibility(View.GONE);
                            }
                        });



                    }
                });

            }
        });

        binding.about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.editFragContainer.setVisibility(View.VISIBLE);
                binding.editFragContainer.bringToFront();
                binding.uid.setVisibility(View.GONE);


                binding.text.setText("Enter about");
                binding.edittext.setHint("about");

                binding.edittext.requestFocus();
                binding.saveEditBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String ss = binding.edittext.getText().toString().trim();

                        firebaseDatabase.getReference("Users").child(firebaseAuth.getUid()).child("about").setValue(ss);
                        binding.edittext.setText("");
                        binding.editFragContainer.setVisibility(View.GONE);

                    }
                });

            }
        });
        



        binding.newPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 CropImage.activity()
                         .setAspectRatio(3,3)
                         .setGuidelines(CropImageView.Guidelines.ON)
                         .setFixAspectRatio(true).setOutputCompressQuality(60)
                        .start(getContext(),ProfileFragment.this);


            }
        });




                firebaseDatabase.getReference("Users").child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String uName = snapshot.child("userName").getValue().toString();
                        String uMail = snapshot.child("userMail").getValue().toString();
                        String uPic = snapshot.child("profilePic").getValue().toString();
                        String uAbout = snapshot.child("about").getValue().toString();

                        Picasso.get().load(uPic).error(R.drawable.user)
                                .placeholder(R.drawable.user).centerCrop().fit()
                                .into(binding.profilePicImageview);

                        binding.username.setText(uName);
                        binding.usermail.setText(uMail);
                        binding.about.setText(uAbout);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                return binding.getRoot();
            }



        @Override
         public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();


                    Picasso.get().load(resultUri).fit().centerCrop().into(binding.profilePicImageview);

                    final StorageReference storageRef = firebaseStorage.getReference().child("Profile pictures").child(firebaseAuth.getUid());
                    storageRef.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    firebaseDatabase.getReference("Users").child(firebaseAuth.getUid()).child("profilePic").setValue(uri.toString());

                                }
                            });

                        }
                    });


                }
            }

        }
}