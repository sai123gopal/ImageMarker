package com.saigopal.imagemarker.adapters;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.saigopal.imagemarker.R;
import com.saigopal.imagemarker.ViewImageActivity;
import com.saigopal.imagemarker.databinding.ImageItemsBinding;
import com.saigopal.imagemarker.models.ImageItemsModel;

import java.util.ArrayList;

public class ImagesRecyclerAdapter extends RecyclerView.Adapter<ImagesRecyclerAdapter.ViewHolder> {

    private final ArrayList<ImageItemsModel> list;

    public ImagesRecyclerAdapter(ArrayList<ImageItemsModel> list) {
        this.list = list;
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
