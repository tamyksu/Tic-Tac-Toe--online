package com.example.tictactoe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public  class Dialog1 extends DialogFragment {

    ImageButton bird, bug, hum, falafel, panda, bee, circle, x;
    Button cancel;
    int player_id = 0;


    public Dialog1() {
        // Empty constructor required for DialogFragment
    }
    public Dialog1(int player_id) {
        this.player_id = player_id;
        // Empty constructor required for DialogFragment
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.icons, null);
        return view;
    }


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog builder = new AlertDialog.Builder(getContext()).create();
        View settings_layout = getActivity().getLayoutInflater().inflate(R.layout.icons, null);
        builder.setView(settings_layout);
        falafel = (ImageButton) settings_layout.findViewById(R.id.falafel);
        panda = (ImageButton) settings_layout.findViewById(R.id.panda);
        bee = (ImageButton) settings_layout.findViewById(R.id.bee);
        bird = (ImageButton) settings_layout.findViewById(R.id.bird);
        hum = (ImageButton) settings_layout.findViewById(R.id.hum);
        bug = (ImageButton) settings_layout.findViewById(R.id.bug);
        cancel = (Button) settings_layout.findViewById(R.id.cancel);
        x = (ImageButton) settings_layout.findViewById(R.id.x);
        circle = (ImageButton) settings_layout.findViewById(R.id.circle);

        falafel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyDialogFragmentListener activity = (MyDialogFragmentListener) getActivity();
                activity.onReturnValue("falafel", player_id);
                dismiss();
            }
        });
        bee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyDialogFragmentListener activity = (MyDialogFragmentListener) getActivity();
                activity.onReturnValue("bee", player_id);
                dismiss();
            }
        });
        panda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyDialogFragmentListener activity = (MyDialogFragmentListener) getActivity();
                activity.onReturnValue("panda", player_id);
                dismiss();
            }
        });
        bug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyDialogFragmentListener activity = (MyDialogFragmentListener) getActivity();
                activity.onReturnValue("bug", player_id);
                dismiss();
            }
        });
        hum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyDialogFragmentListener activity = (MyDialogFragmentListener) getActivity();
                activity.onReturnValue("hum", player_id);
                dismiss();
            }
        });
        bird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyDialogFragmentListener activity = (MyDialogFragmentListener) getActivity();
                activity.onReturnValue("bird", player_id);
                dismiss();
            }
        });
        circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyDialogFragmentListener activity = (MyDialogFragmentListener) getActivity();
                activity.onReturnValue("circle", player_id);
                dismiss();
            }
        });
        x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyDialogFragmentListener activity = (MyDialogFragmentListener) getActivity();
                activity.onReturnValue("x", player_id);
                dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builder.dismiss();
            }
        });
        return builder;
    }


    public interface MyDialogFragmentListener {
        public void onReturnValue(String foo, int i);
    }
}



