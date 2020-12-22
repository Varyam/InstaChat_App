package com.varyam.firebaseapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class Comment_Adapter extends RecyclerView.Adapter<Comment_Adapter.Comment_ViewHolder> {

    private Context context;
    private List<Comment> list;

    public Comment_Adapter(Context context, List<Comment> list) {
        super();
        this.context = context;
        this.list = list;
    }

    @Override
    public void onBindViewHolder(@NonNull final Comment_ViewHolder holder, int position) {
        if (list.isEmpty())
            return;
        holder.comment.setText(list.get(position).getCommentString());
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(list.get(position).getUser());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user commentUser = documentSnapshot.toObject(user.class);
                if (commentUser != null) {
                    String profilePicUri = commentUser.getProfileUri();
                    Glide.with(context).load(Uri.parse(profilePicUri)).circleCrop().into(holder.userImage);
                    holder.userName.setText(commentUser.getUserName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null || list.size() == 0 ? 1 : list.size();
    }

    @NonNull
    @Override
    public Comment_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list, parent, false);
        return new Comment_ViewHolder(view);
    }

    static class Comment_ViewHolder extends RecyclerView.ViewHolder {

        public ImageView userImage;
        public TextView comment;
        public TextView userName;

        public Comment_ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.comment_user_pic);
            comment = itemView.findViewById(R.id.comment_textView);
            userName = itemView.findViewById(R.id.userName);
        }
    }
}
