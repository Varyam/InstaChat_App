package com.varyam.firebaseapp;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class RecycleViewGestureDetector extends RecyclerView.SimpleOnItemTouchListener {

    interface FirestoreGestureDetector {
        void OnViewTouch(View view, String adapterPosition);
    }
}
