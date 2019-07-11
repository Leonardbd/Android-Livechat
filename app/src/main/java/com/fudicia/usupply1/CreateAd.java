package com.fudicia.usupply1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateAd extends AppCompatActivity {

    private ConstraintLayout activity_create_ad;

    private Button picBtn;
    private ImageView adPic;

    private EditText programEditText;
    private TextView channelTextView;

    private TimePicker timePicker1;
    private NumberPicker numberPicker;

    private TextView infoTextView;

    private Button createBtn;
    private Button backBtn;

    private String[] channel_list;

    private FirebaseAuth mAuth;

    private DatabaseReference mRootRef;
    private StorageReference mStorageRef;
    public Uri imgUri;
    private final String SAMPLE_CROPPED_IMG_NAME = "SampleCrop";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ad);
        activity_create_ad = findViewById(R.id.activity_create_ad);

        mRootRef = FirebaseDatabase.getInstance().getReference("Chats");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        adPic = findViewById(R.id.annonsbild);
        picBtn = findViewById(R.id.btnbild);

        programEditText = findViewById(R.id.titeltxt);
        channelTextView = findViewById(R.id.channelTextView);
        infoTextView = findViewById(R.id.info_text_view);
        createBtn = findViewById(R.id.skapabtn);
        backBtn = findViewById(R.id.backBtn);

        timePicker1 = findViewById(R.id.timePicker1);
        timePicker1.setIs24HourView(true);

        numberPicker = findViewById(R.id.numberPicker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(10);

        channel_list = new String[] {"SVT1", "SVT2", "TV3", "TV4", "Kanal 5", "TV6", "Sjuan", "TV8", "Viasat", "C More", "Annat"};

        numberPicker.setDisplayedValues(channel_list);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                if(mAuth.getCurrentUser() != null) {
                    if(programEditText.getText().toString().trim().length() > 0) {
                        HomeFragment.updateData = true;
                        NewHomeFragment.updateData = true;
                        fileUploader();
                        finish();

                    }else{
                        Snackbar.make(activity_create_ad, "Du måste ange ett programnamn", Snackbar.LENGTH_SHORT).show();
                    }
                }else{
                    Snackbar.make(activity_create_ad, "Du måste vara inloggad för att skapa en chatt", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        picBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openGallery();

            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void fileUploader()
    {
        final String program = programEditText.getText().toString();
        final String channel = channel_list[numberPicker.getValue()];

        final String time = timePicker1.getHour() + ":" + getTimestamp(timePicker1.getMinute());

        final String uploadId = mRootRef.push().getKey();

        final String userId = mAuth.getCurrentUser().getUid();

        if(imgUri == null){
            UploadAnnons upload = new UploadAnnons(uploadId, program, channel, time, "0", "", userId);

            ChatMessage chat = new ChatMessage("Välkommen! Skriv i chatten..", "");
            chat.setMessageTime("");

            mRootRef.child(uploadId).setValue(upload);
            mRootRef.child(uploadId).child("messages").push().setValue(chat);
            mRootRef.child(uploadId).child("saved").child("---").setValue("---");
            mRootRef.child(uploadId).child("hidden").child("---").setValue("---");

        }else {

            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE);
            final String formattedD = sdf.format(c);
            final String dateTimeId = formattedD + userId;

            final StorageReference storageReference = mStorageRef.child(dateTimeId);

            storageReference.putFile(imgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            mStorageRef.child(dateTimeId).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    task.getResult();

                                    UploadAnnons upload = new UploadAnnons(uploadId, program, channel, time, "0", task.getResult().toString(), userId);

                                    ChatMessage chat = new ChatMessage("Välkommen! Skriv i chatten..", "");
                                    chat.setMessageTime("");

                                    mRootRef.child(uploadId).setValue(upload);
                                    mRootRef.child(uploadId).child("messages").push().setValue(chat);
                                    mRootRef.child(uploadId).child("saved").child("---").setValue("---");
                                    mRootRef.child(uploadId).child("hidden").child("---").setValue("---");

                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                        }
                    });
        }

        Toast.makeText(CreateAd.this, "Chatt skapad", Toast.LENGTH_LONG).show();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            imgUri = data.getData();
            adPic.setImageURI(imgUri);
            //adPic.setImageURI(imgUri);
            //System.out.println(imgUri);
            if (imgUri != null)
            {
                startCrop(imgUri);
            }
        }
        else if(requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK)
        {
            imgUri = UCrop.getOutput(data);

            if(imgUri != null)
            {
                adPic.setImageURI(imgUri);
            }
        }
    }

    private void startCrop(@NonNull Uri uri)
    {
        String destination = SAMPLE_CROPPED_IMG_NAME;
        destination +=".jpg";
        UCrop uCrop = UCrop.of(uri,Uri.fromFile(new File(getCacheDir(),destination)));
        uCrop.withAspectRatio(1,1);
        //uCrop.useSourceImageAspectRatio()
        //uCrop.withMaxResultSize(450,450);
        uCrop.withOptions(getCropOptions());
        uCrop.start(CreateAd.this);

    }

    private UCrop.Options getCropOptions()
    {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(70);
        //options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false);
        options.setStatusBarColor(Color.parseColor("#94DCFF"));
        options.setToolbarColor(Color.parseColor("#94DCFF"));
        options.setToolbarTitle("Beskär bilden kvadratiskt");

        return options;
    }

    private String getTimestamp(int min){

        if (min < 10){
            return "0" +  min;
        }
        else{
            return "" + min;
        }
    }

}
