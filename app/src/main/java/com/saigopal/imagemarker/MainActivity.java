package com.saigopal.imagemarker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.saigopal.imagemarker.adapters.ImagesRecyclerAdapter;
import com.saigopal.imagemarker.databinding.ActivityMainBinding;
import com.saigopal.imagemarker.models.ImageItemsModel;
import com.saigopal.imagemarker.viewModels.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ImagesRecyclerAdapter adapter;
    private ArrayList<ImageItemsModel> imagesList;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        binding.setLifecycleOwner(this);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        imagesList = new ArrayList<>();

        binding.setClick(new Click());

        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ImagesRecyclerAdapter(imagesList);

        binding.recycler.setAdapter(adapter);

        viewModel.getImagesData();

        observeLiveData();


    }

    private void observeLiveData() {
        viewModel.status.observe(this, s -> {
            if (!s.isEmpty() && !s.equals("Success")){
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.imagesList.observe(this, imageItemsModels -> {
            if(imageItemsModels.size()>0) {
                imagesList.addAll(imageItemsModels);
                adapter.notifyDataSetChanged();
            }
        });
    }


    public class Click{
        public void addNewImage(View view){
            startActivity(new Intent(MainActivity.this,ImageSelectActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}