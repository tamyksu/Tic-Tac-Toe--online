package com.example.tictactoe;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder>{
    private ArrayList<String> array;
    private Context context;
    private FragmentBackgrounds frag;


    public ContactsAdapter(Context context, FragmentBackgrounds frag){
        this.context = context;
        this.frag = frag;

        array = new ArrayList<>();//create array of items backgrounds
        array.add("beach");
        array.add("airplane");
        array.add("water-park");
        array.add("forest");
        array.add("ski");
        array.add("picnic");
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(contactView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String background = array.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frag.listener.createActivity(background);
            }

        });
        holder.bindData(background, this.context);

    }


    @Override
    public int getItemCount() {
        return array.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView background;

        public MyViewHolder(View itemView){
            super(itemView);
            background = (ImageView)itemView.findViewById(R.id.backgroundItem);
        }

        public void bindData(String chosenArena, Context context){
            int id = context.getResources().getIdentifier(chosenArena, "drawable",
                    context.getPackageName());

            background.setImageResource(id);
        }
    }

}
