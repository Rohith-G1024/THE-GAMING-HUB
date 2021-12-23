package com.android.androidproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.Objects;

public class ProfileFragment extends Fragment {


    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;
    TextView pname,pemail,pphone;
    String userID;
    ImageView ppic;
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.profile_fragment, container, false);


        fAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        final long ONE_MB = 1024*1024;

        pname = root.findViewById(R.id.pname);
        pemail = root.findViewById(R.id.pemail);
        pphone = root.findViewById(R.id.pphone);
        ppic = root.findViewById(R.id.ppic);
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(requireActivity(), (value, error) -> {
            if(value!=null && pname!=null){
                pname.setText(value.getString("name"));
                pemail.setText(value.getString("email"));
                pphone.setText(value.getString("phone"));
                if(value.getString("pic")!=null){
                    storageReference=storage.getReferenceFromUrl(Objects.requireNonNull(value.getString("pic")));
                    storageReference.getBytes(ONE_MB).addOnSuccessListener(bytes -> {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        ppic.setImageBitmap(bitmap);

                    });
                }
            }

        });
        return root;
    }



}