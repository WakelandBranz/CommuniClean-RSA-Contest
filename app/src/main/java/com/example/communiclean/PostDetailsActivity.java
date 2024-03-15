package com.example.communiclean;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PostDetailsActivity extends AppCompatActivity {


    String posterUid, postCreationTime, myUid, myUsername, myEmail, myProfilePicture, postImage, postId, postLikes, posterProfilePicture, posterUsername;
    ImageView picture, image;
    TextView name, time, title, description, like, tcomment;
    ImageButton more;
    Button likebtn, share;
    LinearLayout profile;
    EditText comment;
    ImageButton sendb;
    RecyclerView recyclerView;
    List<ModelComment> commentList;
    AdapterComment adapterComment;
    ImageView imagep;
    boolean mlike = false;
    ActionBar actionBar;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Post Details");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        postId = getIntent().getStringExtra("pid");
        recyclerView = findViewById(R.id.recyclecomment);
        picture = findViewById(R.id.pictureco);
        image = findViewById(R.id.pimagetvco);
        name = findViewById(R.id.unameco);
        time = findViewById(R.id.utimeco);
        more = findViewById(R.id.morebtn);
        title = findViewById(R.id.ptitleco);
        myEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        description = findViewById(R.id.descriptco);
        tcomment = findViewById(R.id.pcommenttv);
        like = findViewById(R.id.plikebco);
        likebtn = findViewById(R.id.like);
        comment = findViewById(R.id.typecommet);
        sendb = findViewById(R.id.sendcomment);
        imagep = findViewById(R.id.commentimge);
        share = findViewById(R.id.share);
        profile = findViewById(R.id.profilelayoutco);
        progressDialog = new ProgressDialog(this);
        loadPostInfo();

        loadUserInfo();
        setLikes();
        actionBar.setSubtitle("SignedInAs:" + myEmail);
        loadComments();
        sendb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });
        likebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likePost();
            }
        });
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetailsActivity.this, PostLikedByActivity.class);
                intent.putExtra("pid", postId);
                startActivity(intent);
            }
        });
    }

    private void loadComments() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        commentList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ModelComment modelComment = dataSnapshot1.getValue(ModelComment.class);
                    commentList.add(modelComment);
                    adapterComment = new AdapterComment(getApplicationContext(), commentList, myUid, postId);
                    recyclerView.setAdapter(adapterComment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setLikes() {
        final DatabaseReference liekeref = FirebaseDatabase.getInstance().getReference().child("Likes");
        liekeref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(postId).hasChild(myUid)) {
                    likebtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked, 0, 0, 0);
                    likebtn.setText("Liked");
                }
                else {
                    likebtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0);
                    likebtn.setText("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void likePost() {

        mlike = true;
        final DatabaseReference liekeref = FirebaseDatabase.getInstance().getReference().child("Likes");
        final DatabaseReference postref = FirebaseDatabase.getInstance().getReference().child("Posts");
        liekeref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (mlike) {
                    if (dataSnapshot.child(postId).hasChild(myUid)) {
                        postref.child(postId).child("plike").setValue("" + (Integer.parseInt(postLikes) - 1));
                        liekeref.child(postId).child(myUid).removeValue();
                        mlike = false;

                    } else {
                        postref.child(postId).child("plike").setValue("" + (Integer.parseInt(postLikes) + 1));
                        liekeref.child(postId).child(myUid).setValue("Liked");
                        mlike = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void postComment() {
        progressDialog.setMessage("Adding Comment");

        final String commentss = comment.getText().toString().trim();
        if (TextUtils.isEmpty(commentss)) {
            Toast.makeText(PostDetailsActivity.this, "Empty comment", Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog.show();
        String timestamp = String.valueOf(System.currentTimeMillis());
        DatabaseReference datarf = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("cId", timestamp);
        hashMap.put("comment", commentss);
        hashMap.put("ptime", timestamp);
        hashMap.put("uid", myUid);
        hashMap.put("uemail", myEmail);
        hashMap.put("udp", myProfilePicture);
        hashMap.put("uname", myUsername);
        datarf.child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(PostDetailsActivity.this, "Added", Toast.LENGTH_LONG).show();
                comment.setText("");
                updateCommentCount();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(PostDetailsActivity.this, "Failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    boolean count = false;

    private void updateCommentCount() {
        count = true;
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (count) {
                    String comments = "" + dataSnapshot.child("pcomments").getValue();
                    int newcomment = Integer.parseInt(comments) + 1;
                    reference.child("pcomments").setValue("" + newcomment);
                    count = false;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadUserInfo() {

        Query myref = FirebaseDatabase.getInstance().getReference("Users");
        myref.orderByChild("uid").equalTo(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    myUsername = dataSnapshot1.child("name").getValue().toString();
                    myProfilePicture = dataSnapshot1.child("image").getValue().toString();
                    try {
                        Glide.with(PostDetailsActivity.this).load(myProfilePicture).into(imagep);
                    }
                    catch (Exception e) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void logDataSnapshot(@NonNull DataSnapshot dataSnapshot) {

    }


    private void loadPostInfo() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = databaseReference.orderByChild("ptime").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    // If we couldn't properly retrieve a profile picture
                    if (dataSnapshot1.child("udp").getValue() == null) {
                        Log.d("PostDetailsActivity", "DataSnapshot poster profile picture was null, setting to anonymous profile picture.");
                        posterProfilePicture = "https://static.vecteezy.com/system/resources/thumbnails/009/292/244/small/default-avatar-icon-of-social-media-user-vector.jpg";
                    }
                    else {
                        posterProfilePicture = dataSnapshot1.child("udp").getValue().toString();
                    }

                    // for future reference, this is the structure of our
                    // datasnapshot: https://media.geeksforgeeks.org/wp-content/uploads/20210323183202/Blog.PNG
                    String ptitle = dataSnapshot1.child("title").getValue().toString();
                    String descriptions = dataSnapshot1.child("description").getValue().toString();
                    postImage = dataSnapshot1.child("uimage").getValue().toString();
                    //posterUid = dataSnapshot1.child("uid").getValue().toString();
                    //String uemail = dataSnapshot1.child("uemail").getValue().toString();
                    //posterUsername = dataSnapshot1.child("uname").getValue().toString();
                    postCreationTime = dataSnapshot1.child("ptime").getValue().toString();
                    postLikes = dataSnapshot1.child("plike").getValue().toString();
                    String commentcount = dataSnapshot1.child("pcomments").getValue().toString();
                    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                    calendar.setTimeInMillis(Long.parseLong(postCreationTime));
                    String timedate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
                    name.setText(posterUsername);
                    title.setText(ptitle);
                    description.setText(descriptions);
                    like.setText(postLikes + " Likes");
                    time.setText(timedate);
                    tcomment.setText(commentcount + " Comments");
                    if (postImage.equals("noImage")) {
                        image.setVisibility(View.GONE);
                    }
                    else {
                        image.setVisibility(View.VISIBLE);
                        // The image the user posted

                        try {
                            Glide.with(PostDetailsActivity.this).load(postImage).into(image);
                        }
                        catch (Exception e) {
                            Log.d("PostDetailsActivity", "Couldn't load post image");
                        }
                    }
                    // Load poster's profile picture
                    try {
                        Glide.with(PostDetailsActivity.this).load(posterProfilePicture).into(picture);
                    }
                    catch (Exception e) {
                        Log.d("PostDetailsActivity", "Couldn't load poster's profile picture");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}