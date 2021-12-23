package com.android.androidproject;

import android.content.Intent;
import android.os.Bundle;

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


public class SimulationFragment extends Fragment {

    GridView gridView;
    FirebaseFirestore fStore;
    String gname,gpic,gid,path;
    ArrayList<GameModel> gameModelArrayList;

    public SimulationFragment() {
        // Required empty public constructor
    }


    public static SimulationFragment newInstance() {
        return new SimulationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_simulation, container, false);
        gridView=root.findViewById(R.id.simulationFragment);
        fStore = FirebaseFirestore.getInstance();
        path="/content/games/Simulation";
        gameModelArrayList = new ArrayList<>();
        CollectionReference cRef = fStore.collection(path);
        GameAdapter adapter = new GameAdapter(requireContext(),gameModelArrayList);
        gridView.setAdapter(adapter);
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