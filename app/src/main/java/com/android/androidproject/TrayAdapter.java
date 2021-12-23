package com.android.androidproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import java.util.Objects;

public class TrayAdapter extends ArrayAdapter<TrayModel> {
    FirebaseStorage storage;
    StorageReference storageReference;
    final long ONE_MB = 1024*1024;

    public TrayAdapter(@NonNull Context context, ArrayList<TrayModel> trayModelArrayList) {
        super(context, 0, trayModelArrayList);
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.trayimage, parent, false);
        }
        TrayModel trayModel = getItem(position);
        ImageView gamePic = listitemView.findViewById(R.id.traypic);

        String pic = trayModel.getGamePic();
        storageReference=storage.getReferenceFromUrl(Objects.requireNonNull(pic));
        storageReference.getBytes(ONE_MB).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            gamePic.setImageBitmap(bitmap);
        });
        return listitemView;
    }
}
