package com.varyam.firebaseapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements Serializable, RecycleViewGestureDetector.FirestoreGestureDetector {

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private AlertDialog alertDialog;
    private String currentPhotoPath;
    private Post_RecyclerViewAdapter adapter;
    public static final int REQUEST_IMAGE_CAPTURE = 10;
    public static final int REQUEST_IMAGE_GALLERY = 11;
    public static int REQUEST_CODE;
    public static ContentResolver contentResolver;

    public static InputStream getInputStream(Uri localPath) {
        try {
            Log.d(TAG, "getInputStream: Uri provided to content resolver is :" + localPath);
            return contentResolver.openInputStream(localPath);
        } catch (FileNotFoundException file) {
            Log.d(TAG, "getInputStream: FILE NOT FOUND EXCEPTION........." + file.getMessage());
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: started...................");
        setContentView(R.layout.activity_main);


        contentResolver = getContentResolver();

        mAuth = FirebaseAuth.getInstance();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            Toolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                actionBar = getSupportActionBar();
            }
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            ImageView imageView = findViewById(R.id.profile_pic);
            Glide.with(this).load(user.getPhotoUrl()).circleCrop().into(imageView);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogBox();
            }
        });

        Query query = FirebaseFirestore.getInstance().collection("posts").orderBy("timeOfPost", Query.Direction.DESCENDING).limit(50);
        Log.d(TAG, "onCreate: query==================================" + query.toString());

        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post.class).build();

        RecyclerView recyclerView = findViewById(R.id.recyclerView2);
        adapter = new Post_RecyclerViewAdapter(options, this, this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    public void OnViewTouch(View view, String postID) {
        Log.d(TAG, "OnViewTouch: called////////////////////////");
        switch (view.getId()) {
            case R.id.like_image:
                final DocumentReference document = FirebaseFirestore.getInstance().collection("posts").document(postID);
                document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d(TAG, "onSuccess: called?????????????????????");
                        Post likedPost = documentSnapshot.toObject(Post.class);
                        if (likedPost != null) {
                            List<String> set = likedPost.getLikes();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                if (set.contains(user.getUid())) {
                                    document.update("likes", FieldValue.arrayRemove(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                                    Log.d(TAG, "onSuccess: Post unliked//////////////////");
                                } else {
                                    document.update("likes", FieldValue.arrayUnion(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                                    Log.d(TAG, "onSuccess: Post liked?????????????");
                                }
                            }
                        }
                    }
                });
                break;
            case R.id.comment:
                Intent commentIntent = new Intent(this, com.varyam.firebaseapp.AddComment.class);
                Bundle bundle = new Bundle();
                bundle.putString("PostID", postID);
                commentIntent.putExtras(bundle);
                startActivity(commentIntent);
                break;

            case R.id.comment_image:
                Intent intent = new Intent(this, View_Comments.class);
                Bundle commentBundle = new Bundle();
                commentBundle.putString("PostID", postID);
                intent.putExtras(commentBundle);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void showDialogBox() {
        View view = getLayoutInflater().inflate(R.layout.dialogbox, null, false);
        Button via_camera = view.findViewById(R.id.via_camera);
        Button via_gallery = view.findViewById(R.id.via_gallery);
        via_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
                dispatchImageIntent();
            }
        });
        via_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
                dispatchGalleryIntent();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle("Choose an option");
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }

    private void dispatchGalleryIntent() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, REQUEST_IMAGE_GALLERY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            mAuth.signOut();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mAuth.signOut();
    }


    private File createImagePath() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File externalFileDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", externalFileDir);

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void dispatchImageIntent() {
        Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePhoto.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImagePath();
            } catch (IOException e) {
                Log.d(TAG, "dispatchImageIntent: " + e.getMessage());
            }
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                startActivityForResult(takePhoto, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called with requestCode ============================================" + requestCode);

        REQUEST_CODE = requestCode;
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null)
                currentPhotoPath = uri.toString();
        }

        if ((requestCode == REQUEST_IMAGE_CAPTURE || requestCode == REQUEST_IMAGE_GALLERY) && resultCode == RESULT_OK) {
            if (currentPhotoPath != null) {
                Intent intent = new Intent(this, com.varyam.firebaseapp.AddCaption.class);
                Bundle bundle = new Bundle();
                bundle.putString("currentPhotoPath", currentPhotoPath);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    }


}