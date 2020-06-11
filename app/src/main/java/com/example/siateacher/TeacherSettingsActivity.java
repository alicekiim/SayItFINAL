// Code adapted from tutorial 'Lapit Chat tutorial' by Arathi Singh.
// Tutorial found at: https://www.youtube.com/watch?v=fECOH4w_Kl4&list=PLGCjwl1RrtcQ3o2jmZtwu2wXEA4OIIq53&index=9&t=0s

package com.example.siateacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

public class TeacherSettingsActivity extends AppCompatActivity {

    private DatabaseReference dbRef;
    private FirebaseUser currentUser;

    private CircleImageView profileImage;
    private TextView aName;

    private Button profileImageButton;

    private static final int GALLERY_PICK=1;

    //storage fb
    private StorageReference imageStorage;

    private Uri imageUri;
    private StorageTask storageTask;

    private androidx.appcompat.widget.Toolbar toolB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_settings);

        //initialise the variables
        profileImage = (CircleImageView) findViewById(R.id.settingsPage_image);
        aName = (TextView) findViewById(R.id.settingsPage_name);

        profileImageButton = (Button) findViewById(R.id.settingPage_imageBtn);

        //store the database dbRef for image
        imageStorage = FirebaseStorage.getInstance().getReference();

        //get current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //store current users uid as string variable
        String current_uid = currentUser.getUid();

        //store the database dbRef of the current user
        dbRef = FirebaseDatabase.getInstance().getReference().child("Teachers").child(current_uid);

        //toolbar
        toolB = (Toolbar) findViewById(R.id.settings_bar);
        setSupportActionBar(toolB);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("SayIt! - Account Settings");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Teachers user = dataSnapshot.getValue(Teachers.class);

                //get the name, image and status
                String name = user.getName();
                String image = user.getImage();


                //and set the name as name variable
                aName.setText(name);


                //and if the image does not equal default
                if(!image.equals("default")){
                    //load the unique image the user is using instead
                    Picasso.get().load(image).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        //set an onclicklistener for the image button
        profileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //new intent to pick image from gallery
                Intent gallery_intent = new Intent();
                //define type as images
                gallery_intent.setType("image/*");
                //this is to make sure to get content
                gallery_intent.setAction(Intent.ACTION_GET_CONTENT);

                //createChooser opens gallery. set title as "select image"
                startActivityForResult(Intent.createChooser(gallery_intent, "SELECT IMAGE"), GALLERY_PICK);

            }
        });

    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    };

    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(TeacherSettingsActivity.this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null){

            final StorageReference filepath = imageStorage.child("profile_images").child(currentUser.getUid()+".jpg");//Storage path
            storageTask = filepath.putFile(imageUri);//put file in selected image address/path

            storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                    if (task.isSuccessful()){//If the upload is successful..
                        //..the corresponding Uri is received
                        //URI(Uniform resource identifier) as its name suggests is used to identify resource(whether it be a page of text, a video or sound clip, a still or animated image, or a program)
                        Uri downloadUri = task.getResult();
                        //..then converted to string
                        String mUri = downloadUri.toString();

                        //storing the database dbRef of the current user to store the users image
                        dbRef = FirebaseDatabase.getInstance().getReference("Teachers").child(currentUser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("image", ""+mUri);
                        dbRef.updateChildren(map);

                        //and dismiss the progress dialog once upload is complete
                        pd.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //if image upload fails, show an error message
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    //and dismiss the progress dialog
                    pd.dismiss();
                }
            });
        } else {
            //else is image is not selected, show error message
            Toast.makeText(getApplicationContext(), "No image selected, try again.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if image is selected successfully
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            CropImage.activity(imageUri)//Enter the image address, selected from the gallery
                    .setCropShape( CropImageView . CropShape . OVAL )//Change crop window shape to oval
                    .setAspectRatio(1, 1) //1:1 so its a perfect circle
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri =result.getUri(); //enter the image path of the cropped image

            uploadImage(); //and upload the cropped image
        }
    }




}

