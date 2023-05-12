package com.saigopal.imagemarker.adapters;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.saigopal.imagemarker.R;
import com.saigopal.imagemarker.ViewImageActivity;
import com.saigopal.imagemarker.databinding.ImageItemsBinding;
import com.saigopal.imagemarker.models.ImageItemsModel;

import java.util.ArrayList;

public class ImagesRecyclerAdapter extends RecyclerView.Adapter<ImagesRecyclerAdapter.ViewHolder> {

    private final ArrayList<ImageItemsModel> list;
    private final FirebaseFirestore db;

    public ImagesRecyclerAdapter(ArrayList<ImageItemsModel> list) {
        this.list = list;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageItemsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.image_items,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setModel(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageItemsBinding binding;
        public ViewHolder(@NonNull ImageItemsBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.setClick(new Click());
        }
    }

    public static class Click{
        public void openImage(View view,ImageItemsModel model){
            Intent intent = new Intent(view.getContext(), ViewImageActivity.class);
            intent.putExtra("id",model.getId());
            intent.putExtra("imageUrl",model.getImageUrl());
            view.getContext().startActivity(intent);
        }
    }
}
