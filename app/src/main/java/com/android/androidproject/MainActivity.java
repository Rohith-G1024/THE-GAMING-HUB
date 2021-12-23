package com.android.androidproject;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import static android.widget.Toast.*;


public class MainActivity extends AppCompatActivity {

    Button loginBtn;
    EditText email,password;
    TextView signUpLink;
    FirebaseAuth fAuth;
    ProgressBar progress;
    FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //layout adjustments
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
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        loginBtn=findViewById(R.id.signup);
        signUpLink=findViewById(R.id.signuplink);
        progress = findViewById(R.id.progress1);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        loginBtn.setOnClickListener(view -> {
            //extract
            if(email.getText().toString().isEmpty()){
                email.setError("Email is blank.");
                return;
            }

            if(password.getText().toString().isEmpty()){
                password.setError("Password is blank.");
                return;
            }

            progress.setVisibility(View.VISIBLE);

            //validate
            fAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText()
                    .toString()).addOnSuccessListener(authResult -> {
                        //on successful login
                        FirebaseUser user2 = fAuth.getCurrentUser();
                        assert user2 != null;
                        makeText(MainActivity.this, "Signed in as "
                                +user2.getEmail(), LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),DashboardActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                    }).addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                        progress.setVisibility(View.INVISIBLE);
                    });

        });

        signUpLink.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(),SignupActivity.class)));

    }



    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user  = fAuth.getCurrentUser();

        // check if user has already signed in
        if(user != null)
        {
            System.out.println(user);
            //String name = user.getDisplayName();
            makeText(this, "Signed in as "+user.getEmail(), LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),DashboardActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

}

