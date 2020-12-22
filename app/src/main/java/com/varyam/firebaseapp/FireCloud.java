package com.varyam.firebaseapp;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FireCloud {

    private FirebaseFirestore db;
    private static com.varyam.firebaseapp.user user;
    private static final String TAG = "FireCloud";

    public FireCloud() {
        db = FirebaseFirestore.getInstance();
    }

    public void addUser(user user) {
        db.collection("users").document(user.getUserID()).set(user);
    }

    private void addPost(String currentPhotoPath, String caption) {
        String timeOfPost = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US).format(new Date());
        timeOfPost = timeOfPost.substring(0, timeOfPost.length() - 5);
        String postID = "";
        if (user == null)
            throw new NullPointerException();

        postID += user.getUserName();
        postID += timeOfPost;

        Post post = new Post(user, postID, timeOfPost, currentPhotoPath, caption);
        addPostToDB(currentPhotoPath, post);
    }

    public void createPost(final String currentPhotoPath, final String caption) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        String userID = "";
        if (firebaseUser != null) {
            userID = firebaseUser.getUid();
        }

        db.collection("users")
                .document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        user = documentSnapshot.toObject(user.class);
                        addPost(currentPhotoPath, caption);
                    }
                });
    }

    private void addPostToDB(String localPath, final Post post) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference().child("images/");

        Uri uri = Uri.fromFile(new File(localPath));
        final StorageReference storageReference = reference.child(user.getUserID() + "/" + uri.getLastPathSegment());

        UploadTask uploadTask = null;
        if (MainActivity.REQUEST_CODE == MainActivity.REQUEST_IMAGE_CAPTURE) {
            uploadTask = storageReference.putFile(uri);
        } else if (MainActivity.REQUEST_CODE == MainActivity.REQUEST_IMAGE_GALLERY) {
            try {
                InputStream inputStream = MainActivity.getInputStream(Uri.parse(localPath));
                assert inputStream != null;
                uploadTask = storageReference.putStream(inputStream);
            } catch (Exception e) {
                Log.d(TAG, "addPostToDB: Exception thrown :" + e.getMessage());
            }
        }
        assert uploadTask != null;
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: Uploading Photo Successful.............................................");
                Task<Uri> uriTask = storageReference.getDownloadUrl();
                uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        post.setDownloadUri(uri.toString());
                        Log.d(TAG, "addPostToDB: Download Uri is =============" + post.getDownloadUri());
                        FirebaseFirestore.getInstance().collection("posts").document(post.getPostId()).set(post);
                    }
                });
            }
        });
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Uploading Photo Failed........................." + e.getMessage());
            }
        });
    }
}
