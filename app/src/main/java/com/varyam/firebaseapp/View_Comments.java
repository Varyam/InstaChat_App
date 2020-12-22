package com.varyam.firebaseapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class View_Comments extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__comments);

        final RecyclerView recyclerView = findViewById(R.id.comment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            final String postId = bundle.getString("PostID");
            if (postId != null) {
                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("posts").document(postId);
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        com.varyam.firebaseapp.Post post = documentSnapshot.toObject(com.varyam.firebaseapp.Post.class);
                        if (post != null) {
                            List<Comment> list = post.getComments();
                            com.varyam.firebaseapp.Comment_Adapter comment_adapter = new com.varyam.firebaseapp.Comment_Adapter(getApplicationContext(), list);
                            recyclerView.setAdapter(comment_adapter);
                        }
                    }
                });
            }
        }
    }
}