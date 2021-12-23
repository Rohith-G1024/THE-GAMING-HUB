package com.android.androidproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.Objects;

public class DashboardActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ImageView userpic;
    TextView uname;
    TextView uemail;
    FirebaseStorage storage;
    StorageReference storageReference;
    String userID;

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        //fetching user details from Firestore...
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        userpic = headerView.findViewById(R.id.userpic);
        uname = headerView.findViewById(R.id.uname);
        uemail = headerView.findViewById(R.id.uemail);

        final long ONE_MB=1024*1024;

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, (value, error) -> {
            if(value!=null){
                uname.setText(value.getString("name"));
                uemail.setText(value.getString("email"));
                if(value.getString("pic")!= null) {
                    storageReference = storage.getReferenceFromUrl(Objects.requireNonNull(value.getString("pic")));
                    storageReference.getBytes(ONE_MB).addOnSuccessListener(bytes -> {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        userpic.setImageBitmap(bitmap);
                    });
                }
            }
            else{
                fAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(),MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }

        });


        //logout
        navigationView.getMenu().findItem(R.id.logoutbar).setOnMenuItemClickListener(menuItem -> {
            fAuth.signOut();
            startActivity(new Intent(getApplicationContext(),MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            Toast.makeText(DashboardActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            return true;
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.profileFragment,
                R.id.actionFragment,
                R.id.adventureFragment,
                R.id.casualFragment,
                R.id.indieFragment,
                R.id.racingFragment,
                R.id.rpgFragment,
                R.id.simulationFragment,
                R.id.sportsFragment,
                R.id.strategyFragment,
                R.id.topsellersFragment)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();

    }
}