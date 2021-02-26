package com.example.tictactoe;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentBackgrounds extends Fragment {
    fragInterface listener;
    RecyclerView recyclerView;
    com.example.tictactoe.ContactsAdapter adapter;

    @Override
    public void onAttach(@NonNull Context context) {
        this.listener = (fragInterface)context;
        super.onAttach(context);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout backgrounds for this fragment
        return inflater.inflate(R.layout.activity_recycler_view, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view);
        adapter = new com.example.tictactoe.ContactsAdapter(getContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }
    public interface fragInterface{
        void createActivity(String name);
    }
}


