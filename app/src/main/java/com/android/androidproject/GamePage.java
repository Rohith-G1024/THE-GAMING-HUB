package com.android.androidproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class GamePage extends AppCompatActivity {

    TextView gpname,gpdescription,gpdate,gpcost,gpplayer;
    ArrayList<TrayModel> source;
    List<String> images,games;
    TrayAdapter trayAdapter;
    ListView gptray;
    String pic,path,userID;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    Button backbutton,purchase;
    Boolean ispurchased;
    Drawable tick;
    int cost;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_page);
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

        source = new ArrayList<>();
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        storage = FirebaseStorage.getInstance();
        gpname = findViewById(R.id.gpname);
        gpdescription=findViewById(R.id.gpdescription);
        gpdate=findViewById(R.id.gpdate);
        gpcost=findViewById(R.id.gpcost);
        gpplayer=findViewById(R.id.gpplayer);
        backbutton=findViewById(R.id.backbutton);
        purchase=findViewById(R.id.gppurchase);
        gptray =  findViewById(R.id.gptray);
        ispurchased=false;
        cost=0;
        tick= AppCompatResources.getDrawable(getApplicationContext(),R.drawable.checked);

        backbutton.setOnClickListener(view -> {
            path="";
            finish();

        });

        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        DocumentReference documentReference = fStore.document(path);
        documentReference.addSnapshotListener((value, error) -> {
            if(value != null) {
                gpdescription.setText(value.getString("description"));
                gpdate.setText(value.getString("release date"));
                gpname.setText(value.getString("name"));
                cost = Objects.requireNonNull(value.getLong("cost")).intValue();
                if (cost == 0) gpcost.setText("Free");
                else gpcost.setText("\u20B9 " + Long.toString(cost) + "/-");
                gpplayer.setText(value.getString("player"));
                pic = value.getString("pic");
                documentReference.get().addOnCompleteListener(task -> {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    images = (List<String>) documentSnapshot.get("tray");
                    source.add(new TrayModel(pic));
                    for (String i : images) {
                        source.add(new TrayModel(i));
                    }
                    trayAdapter = new TrayAdapter(getApplicationContext(), source);
                    gptray.setAdapter(trayAdapter);
                    trayAdapter.notifyDataSetChanged();

                });


                DocumentReference doc = fStore.collection("users").document(userID);
                doc.addSnapshotListener((value1, error1) -> {
                    if (value1 != null) {
                        doc.get().addOnCompleteListener(task -> {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            games = (List<String>) documentSnapshot.get("games");
                            if (games != null) {
                                for (String i : games) {
                                    if (i.equals(path)) {
                                        ispurchased = true;
                                        purchase.setCompoundDrawablesWithIntrinsicBounds(tick, null, null, null);
                                        purchase.setText("PURCHASED");
                                        break;
                                    }
                                }
                            }

                        });
                    }

                });
                purchase.setOnClickListener(view -> {
                    if (!ispurchased) {
                        if (cost > 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(GamePage.this, R.style.Dialogbox));
                            builder.setTitle("Enter Amount in \u20B9");
                            final EditText input = new EditText(this);
                            input.setInputType(InputType.TYPE_CLASS_NUMBER);
                            builder.setView(input);
                            builder.setPositiveButton("OK", (dialog, which) -> {
                                if (Integer.parseInt(input.getText().toString()) == cost) {
                                    dialog.dismiss();
                                    Toast.makeText(GamePage.this, "You purchased " + gpname.getText(), Toast.LENGTH_SHORT).show();
                                    ispurchased = true;
                                    purchase.setCompoundDrawablesWithIntrinsicBounds(tick, null, null, null);
                                    purchase.setText("PURCHASED");
                                    doc.update("games", FieldValue.arrayUnion(path));
                                } else {
                                    Toast.makeText(GamePage.this, "Wrong amount entered.", Toast.LENGTH_SHORT).show();
                                    input.setText("");
                                }
                            });
                            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                            builder.show();
                        } else {
                            Toast.makeText(GamePage.this, "You purchased " + gpname.getText(), Toast.LENGTH_SHORT).show();
                            ispurchased = true;
                            purchase.setCompoundDrawablesWithIntrinsicBounds(tick, null, null, null);
                            purchase.setText("PURCHASED");
                            doc.update("games", FieldValue.arrayUnion(path));
                        }


                    }
                });

            }
        });

    }
}