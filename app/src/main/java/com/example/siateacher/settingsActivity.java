package com.example.siateacher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class settingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDB;
    private FirebaseUser mCurrentUser;

    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;

    private Button mStatusButton;
    private Button mImageButton;

    private static final int GALLERY_PICK=1;

    //storage fb
    private StorageReference mImageStorage;

    private Uri imageUri;
    private StorageTask uploadTask;


    private ProgressDialog mProgressDiaglog;

    private androidx.appcompat.widget.Toolbar mToolbar;

    Uri imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mDisplayImage = (CircleImageView) findViewById(R.id.settingsPage_image);
        mName = (TextView) findViewById(R.id.settingsPage_name);
        mStatus = (TextView) findViewById(R.id.settingsPage_status);

        mStatusButton = (Button) findViewById(R.id.settingPage_statusBtn);
        mImageButton = (Button) findViewById(R.id.settingPage_imageBtn);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();

        mUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        //toolbar
        mToolbar = (Toolbar) findViewById(R.id.settings_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Say It App - Account Settings");

        mUserDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);

                //String name = dataSnapshot.child("name").getValue().toString();
                //String image = dataSnapshot.child("image").getValue().toString();
                //String status = dataSnapshot.child("status").getValue().toString();
                //String thumb_image = dataSnapshot.child("thumb_image").toString();

                //Users.class에 있는 정보들이라서 변경해줌
                String name = user.getName();
                String image = user.getImage();
                String status = user.getStatus();

                mName.setText(name);
                mStatus.setText(status);

                if(!image.equals("default")){
                    Picasso.get().load(image).into(mDisplayImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String status_value= mStatus.getText().toString();

                Intent status_intent = new Intent(settingsActivity.this, statusActivity.class);
                status_intent.putExtra("status value", status_value);
                startActivity(status_intent);

            }
        });

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gallery_intent = new Intent();
                gallery_intent.setType("image/*");
                gallery_intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallery_intent, "SELECT IMAGE"), GALLERY_PICK);

            }
        });

    }
    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(settingsActivity.this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null){

            final StorageReference filepath = mImageStorage.child("profile_images").child(mCurrentUser.getUid()+".jpg");//Storage 넣어줄 경로
            uploadTask = filepath.putFile(imageUri);//선택한이미지 주소를 넣어준다
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }

                    return  filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();// 업로드가 성공하면 해당 Uri 를 받아온다
                        String mUri = downloadUri.toString();

                        //Users의 image upload한 주소를 넣어준다
                        mUserDB = FirebaseDatabase.getInstance().getReference("Users").child(mCurrentUser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("image", ""+mUri);
                        mUserDB.updateChildren(map);

                        pd.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            CropImage.activity(imageUri)//갤러리에서 선택한 이미지주소를 넣어준다
                    .setCropShape( CropImageView . CropShape . OVAL )//자르기 창모양 변경(네모->원)
                    .setAspectRatio(1, 1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri =result.getUri(); //CropImage 한 이미지 주소 를 넣어준다

            uploadImage();
        }
    }


}

