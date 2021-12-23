package com.android.androidproject;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    public static final String TAG = "TAG" ;
    EditText mFullName, mEmail, mPassword, mPhone;
    Button mSignUpBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;
    String userID;
    Uri filePath,picUri;
    ImageView signupimage;
    Intent data1;
    final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        this.getWindow().getDecorView().setSystemUiVisibility(flags);
        final View decorView = getWindow().getDecorView();
        decorView
                .setOnSystemUiVisibilityChangeListener(visibility -> {
                    if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                    {
                        decorView.setSystemUiVisibility(flags);
                    }
                });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        mFullName = findViewById(R.id.name);
        mEmail = findViewById(R.id.email1);
        mPassword = findViewById(R.id.password);
        mPhone = findViewById(R.id.phone);
        mSignUpBtn = findViewById(R.id.signup);
        signupimage = findViewById(R.id.signupimage);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        progressBar = findViewById(R.id.progressBar);

        //if user already exists...
        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),DashboardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
        }

        signupimage.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });

        mSignUpBtn.setOnClickListener(view -> {
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            String name = mFullName.getText().toString();
            String phone = mPhone.getText().toString();

            if(TextUtils.isEmpty(email)){
                mEmail.setError("Email is blank.");
                return;
            }

            if(TextUtils.isEmpty(password)){
                mPassword.setError("Password is blank.");
                return;
            }

            if(password.length() < 5){
                mPassword.setError("Password should be minimum 5 chars.");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            String uripic = "gs://android-project-e1e56.appspot.com/profiles/"+email+".jpg";

            //if everything looks fine
            fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(SignupActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                    userID = fAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("users").document(userID);
                    Map<String,Object> user = new HashMap<>();
                    user.put("name", name);
                    user.put("email", email);
                    user.put("phone", phone);
                    user.put("password", password);
                    user.put("pic",uripic);

                    //if user has uploaded profile pic

                    if(filePath != null)
                    {
                        final ProgressBar progressDialog = new ProgressBar(getApplicationContext());
                        progressDialog.setVisibility(View.VISIBLE);

                        StorageReference ref = storageReference.child("profiles/"+ email +".jpg");
                        filePath = data1.getData();
                        Bitmap bmp = null;
                        try {
                            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        assert bmp != null;


                        Bitmap resizedBitmap = null;
                        try {
                            resizedBitmap = rotate(bmp,filePath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        assert resizedBitmap != null;
                        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                        byte[] data2 = baos.toByteArray();
                        signupimage.setScaleType(ImageView.ScaleType.CENTER);
                        signupimage.setImageBitmap(resizedBitmap);
                        ref.putBytes(data2)
                                .addOnSuccessListener(taskSnapshot -> {
                                    progressDialog.setVisibility(View.INVISIBLE);

                                    Toast.makeText(getApplicationContext(), "Uploaded"+picUri.toString(), Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    progressDialog.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                })
                                .addOnProgressListener(taskSnapshot -> {
                                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                            .getTotalByteCount());
                                    progressDialog.setProgress((int)progress);
                                });
                        ref.getDownloadUrl().addOnSuccessListener(uri -> picUri = uri);
                    }

                    //upload user details..
                    documentReference.set(user).addOnSuccessListener(aVoid -> Log.d(TAG , "onSuccess: user profile is created for "+userID));
                    startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
                }
                else{
                    Toast.makeText(SignupActivity.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });

        });
    }
    public Bitmap rotate(Bitmap bmp,Uri selectedImage) throws IOException {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int newWidth = 200;
        int newHeight  = 200;
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        InputStream input = getApplicationContext().getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        ei = new ExifInterface(input);

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(bmp, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(bmp, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(bmp, 270);
            default:
                return bmp;
        }
    }
    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            data1=data;
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                signupimage.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}