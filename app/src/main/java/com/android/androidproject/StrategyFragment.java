package com.android.androidproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class StrategyFragment extends Fragment {
    GridView gridView;
    FirebaseFirestore fStore;
    String gname,gpic,gid,path;
    ArrayList<GameModel> gameModelArrayList;
    public StrategyFragment() {
        // Required empty public constructor
    }

    public static StrategyFragment newInstance() {
        return new StrategyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_strategy, container, false);
        gridView=root.findViewById(R.id.strategyFragment);
        fStore = FirebaseFirestore.getInstance();
        gameModelArrayList = new ArrayList<>();
        path = "/content/games/Strategy";
        GameAdapter adapter = new GameAdapter(requireContext(),gameModelArrayList);
        gridView.setAdapter(adapter);
        CollectionReference cRef = fStore.collection(path);
        cRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                gname=documentSnapshot.getString("name");
                gpic=documentSnapshot.getString("pic");
                gid = documentSnapshot.getId();
                gameModelArrayList.add(new GameModel(gname,gpic,gid));
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