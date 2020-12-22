package com.varyam.firebaseapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class AddCaption extends AppCompatActivity {

    private static final String TAG = "AddCaption";
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_caption);

        final EditText caption = findViewById(R.id.caption_editText);
        Button save = findViewById(R.id.caption_save_button);
        Button cancel = findViewById(R.id.cancel_caption_button);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentPhotoPath = bundle.getString("currentPhotoPath");
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (caption.getText() == null || caption.getText().toString().trim().length() == 0) {
                    caption.setHint("Caption is necessary for post. Please enter a character.");
                    return;
                }
                addGalleryPhoto(caption.getText().toString());
                finish();
            }
        });
    }

    public void addGalleryPhoto(String caption) {
        Intent addGallery = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        addGallery.setData(contentUri);
        this.sendBroadcast(addGallery);
        Log.d(TAG, "addGalleryPhoto: photo added to gallery");
        FireCloud cloud = new FireCloud();
        cloud.createPost(currentPhotoPath, caption);
    }
}