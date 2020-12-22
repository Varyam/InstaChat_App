package com.varyam.firebaseapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddComment extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_caption);

        final EditText comment = findViewById(R.id.caption_editText);
        comment.setHint("Add your comment.");
        Button save = findViewById(R.id.caption_save_button);
        Button cancel = findViewById(R.id.cancel_caption_button);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comment.getText() == null || comment.getText().toString().trim().length() == 0) {
                    comment.setHint("Comment cannot be empty. Please enter your comment.");
                    return;
                }
                AddCommentToPost(comment.getText().toString());
                finish();
            }
        });
    }

    private void AddCommentToPost(final String comment) {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String postId = bundle.getString("PostID");
            if (postId != null) {
                final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("posts").document(postId);
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (firebaseUser != null) {
                            Comment comment1 = new Comment(firebaseUser.getUid(), comment);
                            documentReference.update("comments", FieldValue.arrayUnion(comment1));
                        }
                    }
                });
            }
        }
    }
}
