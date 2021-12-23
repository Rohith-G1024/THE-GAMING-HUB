package com.android.androidproject;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class ActionFragment extends Fragment {

    GridView gridView;
    FirebaseFirestore fStore;
    String gname, gpic, gid, path;
    ArrayList<GameModel> gameModelArrayList;


    public static ActionFragment newInstance() {
        return new ActionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.action_fragment, container, false);

        gridView = root.findViewById(R.id.actionFragment);
        fStore = FirebaseFirestore.getInstance();

        gameModelArrayList = new ArrayList<>();
        path = "/content/games/action";
        GameAdapter adapter = new GameAdapter(requireContext(), gameModelArrayList);
        gridView.setAdapter(adapter);
        CollectionReference cRef = fStore.collection(path);
        cRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                gname = documentSnapshot.getString("name");
                gpic = documentSnapshot.getString("pic");
                gid = documentSnapshot.getId();
                gameModelArrayList.add(new GameModel(gname, gpic, gid));
            }
            adapter.notifyDataSetChanged();
        });
        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(getContext(), GamePage.class);
            String gpath = path+"/"+gameModelArrayList.get(i).getGame_id();
            intent.putExtra("path", gpath);
            startActivity(intent);
        });
    }
}
