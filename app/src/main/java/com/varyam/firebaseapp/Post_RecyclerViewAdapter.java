package com.varyam.firebaseapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Post_RecyclerViewAdapter extends FirestoreRecyclerAdapter<com.varyam.firebaseapp.Post, Post_RecyclerViewAdapter.Holder> {

    private static final String TAG = "RecyclerViewAdapter";
    private Context context;
    private RecycleViewGestureDetector.FirestoreGestureDetector detector;


    public Post_RecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<com.varyam.firebaseapp.Post> options, @NonNull Context context, RecycleViewGestureDetector.FirestoreGestureDetector gestureDetector) {
        super(options);
        Log.d(TAG, "Post_RecyclerViewAdapter: called///////////////////////////////////");
        this.context = context;
        detector = gestureDetector;
    }

    @Override
    protected void onBindViewHolder(@NonNull Holder holder, int position, @NonNull com.varyam.firebaseapp.Post model) {


        Log.d(TAG, "onBindViewHolder: called..............................");
        String titleText = model.getCreator().getUserName() + "\n" + model.getTimeOfPost();
        holder.title.setText(titleText);

        holder.caption.setText(model.getCaption());
        holder.likeCount.setText(context.getString(R.string.likes, model.getLikes().size()));

        if (model.getComments().size() == 0) {
            holder.commentCount.setText(context.getString(R.string.comment));
        } else {
            holder.commentCount.setText(context.getString(R.string.viewComment, model.getComments().size()));
        }

        Glide.with(context).load(model.getDownloadUri()).into(holder.mainPost);

        Glide.with(context).load(model.getCreator().getProfileUri()).circleCrop().into(holder.follower);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Glide.with(context).load(user.getPhotoUrl()).circleCrop().into(holder.userImage);
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post, parent, false);
        final Holder holder = new Holder(view);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: called for ImageView");
                detector.OnViewTouch(v, getSnapshots().getSnapshot(holder.getAdapterPosition()).getId());
            }
        };
        holder.like.setOnClickListener(clickListener);
        holder.comment.setOnClickListener(clickListener);
        holder.commentImage.setOnClickListener(clickListener);
        return holder;
    }


    @Override
    public void onError(@NonNull FirebaseFirestoreException e) {
        super.onError(e);
        Log.d(TAG, "onError: error occurred===================" + e.getMessage());
    }


    static class Holder extends RecyclerView.ViewHolder {

        public ImageView follower;
        public TextView title;
        public ImageView mainPost;
        public TextView caption;
        public TextView likeCount;
        public TextView commentCount;
        public ImageView userImage;
        public TextView comment;
        public ImageView like;
        public ImageView commentImage;


        public Holder(@NonNull View itemView) {
            super(itemView);
            follower = itemView.findViewById(R.id.follower_image);
            title = itemView.findViewById(R.id.post_title);
            mainPost = itemView.findViewById(R.id.post_main_image);
            caption = itemView.findViewById(R.id.post_caption);
            likeCount = itemView.findViewById(R.id.like_count);
            commentCount = itemView.findViewById(R.id.comment_count);
            userImage = itemView.findViewById(R.id.user_image);
            comment = itemView.findViewById(R.id.comment);
            like = itemView.findViewById(R.id.like_image);
            commentImage = itemView.findViewById(R.id.comment_image);
        }
    }
}
