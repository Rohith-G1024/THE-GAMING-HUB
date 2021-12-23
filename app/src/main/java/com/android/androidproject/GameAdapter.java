package com.android.androidproject;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class GameAdapter extends ArrayAdapter<GameModel> {

    FirebaseStorage storage;
    StorageReference storageReference;
    final long ONE_MB = 1024*1024;

    public GameAdapter(@NonNull Context context, ArrayList<GameModel> gameModelArrayList) {
        super(context, 0, gameModelArrayList);
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.game_card, parent, false);
        }
        GameModel gameModel = getItem(position);
        TextView gameName = listitemView.findViewById(R.id.game_name);
        ImageView gamePic = listitemView.findViewById(R.id.game_pic);
        gameName.setText(gameModel.getGame_name());
        String pic = gameModel.getGame_pic();
        storageReference=storage.getReferenceFromUrl(Objects.requireNonNull(pic));
        storageReference.getBytes(ONE_MB).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            gamePic.setImageBitmap(bitmap);
        });
        return listitemView;
    }
}

