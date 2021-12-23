package com.android.androidproject.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.android.androidproject.GameAdapter;
import com.android.androidproject.GameModel;
import com.android.androidproject.GamePage;
import com.android.androidproject.R;
import com.android.androidproject.TopsellersFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HomeFragment extends Fragment {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ArrayList<GameModel> gameModelArrayList;
    GameAdapter adapter;
    GridView gridView;
    List<String> games;
    String userID;

    public HomeFragment(){}

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        fStore = FirebaseFirestore.getInstance();
        gameModelArrayList = new ArrayList<>();
        adapter = new GameAdapter(requireContext(), gameModelArrayList);
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(requireActivity(), (value, error) -> {
            if(value!=null){
                documentReference.get().addOnCompleteListener(task -> {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    games = (List<String>)documentSnapshot.get("games");
                    if(games!=null){
                        for(String i: games){
                            DocumentReference doc = fStore.document(i);
                            doc.addSnapshotListener((value1, error1) -> {
                                if(value1 !=null){
                                    String gname = value1.getString("name");
                                    String gpic = value1.getString("pic");
                                    String gid = value1.getId();
                                    gameModelArrayList.add(new GameModel(gname, gpic, gid));
                                }
                            });
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        gridView = root.findViewById(R.id.homeFragment);
        gridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return root;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState==null){
            adapter = new GameAdapter(requireContext(), gameModelArrayList);
        gridView.setAdapter(adapter);
        }
            adapter.notifyDataSetChanged();
        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(getContext(), GamePage.class);
            intent.putExtra("path", games.get(i));
            startActivity(intent);
        });
    }
    @Override
    public void onResume(){
        super.onResume();
        adapter.notifyDataSetChanged();
    }

}