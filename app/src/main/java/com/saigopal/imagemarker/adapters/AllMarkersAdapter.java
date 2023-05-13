package com.saigopal.imagemarker.adapters;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.saigopal.imagemarker.R;
import com.saigopal.imagemarker.databinding.AllMarkersItemsBinding;
import com.saigopal.imagemarker.models.MarkerModel;
import com.saigopal.imagemarker.utils.ShowImage;
import com.saigopal.imagemarker.utils.TouchImageView;
import com.saigopal.imagemarker.viewModels.CurrentImageViewModel;

import java.util.ArrayList;

public class AllMarkersAdapter extends RecyclerView.Adapter<AllMarkersAdapter.ViewHolder> {

    ArrayList<MarkerModel> list;
    TouchImageView touchImageView;
    CurrentImageViewModel viewModel;

    public AllMarkersAdapter(ArrayList<MarkerModel> list, TouchImageView touchImageView,CurrentImageViewModel viewModel) {
        this.touchImageView = touchImageView;
        this.list = list;
        this.viewModel= viewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AllMarkersItemsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.all_markers_items,parent,false);
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

    public class ViewHolder extends RecyclerView.ViewHolder{
        AllMarkersItemsBinding binding;
        public ViewHolder(@NonNull AllMarkersItemsBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.setClick(new Click());
        }
    }

    public class Click{
        public void locateMarker(View view,MarkerModel model){
            viewModel.hideMarkersBottomSheet.postValue(true);
            touchImageView.addRoundTo(Math.toIntExact(model.getPoints().get(0)), Math.toIntExact(model.getPoints().get(1)));
        }
        public void viewImage(View view){
            Bitmap bitmap = ((BitmapDrawable) ((ImageView) view).getDrawable()).getBitmap();
            new ShowImage(view.getContext(), bitmap);
        }
    }

}
