package com.example.communiclean;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ImageView profilePicture;
    TextView username, userEmail;
    RecyclerView postRecycler;
    FloatingActionButton editProfileData;
    List<ModelPost> posts;
    AdapterPosts adapterPosts;
    String uid;
    ProgressDialog pd;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        profilePicture = view.findViewById(R.id.profile_picture);
        username = view.findViewById(R.id.username);
        userEmail = view.findViewById(R.id.email);
        uid = FirebaseAuth.getInstance().getUid();
        editProfileData = view.findViewById(R.id.edit_profile_data);
        postRecycler = view.findViewById(R.id.recycler_posts);
        posts = new ArrayList<>();
        pd = new ProgressDialog(getActivity());
        loadMyPosts();
        pd.setCanceledOnTouchOutside(false);

        // Retrieving user email data from firebase
        Query query = databaseReference.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String username_s = "" + dataSnapshot1.child("name").getValue();
                    String userEmail_s = "" + dataSnapshot1.child("email").getValue();
                    String image = "" + dataSnapshot1.child("image").getValue();
                    username.setText(username_s);
                    userEmail.setText(userEmail_s);
                    try {
                        Glide.with(getActivity()).load(image).into(profilePicture);
                    }
                    catch (Exception e) {
                        Log.d("ProfileFragment", "Failed to load " + username_s + "'s profile picture");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("big time error", "This should never happen, check ProfileFragment OnCreateView OnCancelled");
            }
        });
        editProfileData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfilePage.class));
            }
        });
        return view;
    }

    private void loadMyPosts() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        postRecycler.setLayoutManager(layoutManager);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = databaseReference.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ModelPost modelPost = dataSnapshot1.getValue(ModelPost.class);
                    fetchUserDataAndRenderPost(modelPost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchUserDataAndRenderPost(final ModelPost modelPost) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(modelPost.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("name").getValue(String.class);
                    String userProfilePicture = dataSnapshot.child("image").getValue(String.class);
                    modelPost.setUname(username);
                    modelPost.setUdp(userProfilePicture);
                }
                posts.add(modelPost);
                adapterPosts = new AdapterPosts(getActivity(), posts);
                postRecycler.setAdapter(adapterPosts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
}