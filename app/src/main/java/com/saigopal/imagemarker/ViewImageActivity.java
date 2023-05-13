package com.saigopal.imagemarker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.saigopal.imagemarker.adapters.AllMarkersAdapter;
import com.saigopal.imagemarker.databinding.ActivityViewImageBinding;
import com.saigopal.imagemarker.databinding.AllMarkersDialogBinding;
import com.saigopal.imagemarker.databinding.MarkerDetailsDialogBinding;
import com.saigopal.imagemarker.models.MarkerModel;
import com.saigopal.imagemarker.utils.TouchImageView;
import com.saigopal.imagemarker.viewModels.CurrentImageViewModel;

import java.util.ArrayList;

public class ViewImageActivity extends AppCompatActivity {

    private ActivityViewImageBinding binding;
    private CurrentImageViewModel viewModel;
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private BottomSheetDialog bottomSheerDialog;
    private AllMarkersAdapter allMarkersAdapter;
    private ArrayList<MarkerModel> markerModelArrayList;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_view_image);
        binding.setLifecycleOwner(this);

        viewModel = new ViewModelProvider(this).get(CurrentImageViewModel.class);
        binding.setViewModel(viewModel);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        String  id = getIntent().getStringExtra("id");


        viewModel.docId.setValue(id);
        binding.setClick(new DialogClick());

        viewModel.getDetails();
        viewModel.getAllMarkers();

        TouchImageView touchImageView = binding.image;
        touchImageView.setViewModel(viewModel);
        viewModel.setTouchImageView(touchImageView);

        markerModelArrayList = new ArrayList<>();
        allMarkersAdapter = new AllMarkersAdapter(markerModelArrayList,touchImageView,viewModel);


        observeLiveData();


    }

    private void observeLiveData() {

        viewModel.status.observe(this, s -> {
            if(s.equals("Success"))  {
                progressDialog.dismiss();
                Toast.makeText(this, "Marker Added", Toast.LENGTH_SHORT).show();
            }else if (s.equals("Error")){
                progressDialog.dismiss();
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
            }else if(!s.isEmpty()){
                progressDialog.dismiss();
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.showMarkerDetailsDialog.observe(this, aBoolean -> {
            if(aBoolean){
                showAlert();
            }else {
                if(dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        viewModel.hideMarkersBottomSheet.observe(this, aBoolean -> {
            if(aBoolean){
                bottomSheerDialog.dismiss();
            }
        });

        viewModel.markerModelArrayList.observe(this, markerModels -> {
            if(markerModels.size()>0){
                markerModelArrayList.addAll(markerModels);
                allMarkersAdapter.notifyDataSetChanged();
            }
        });

    }

    public void showAlert(){
        dialog = new Dialog(this);
        MarkerDetailsDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(this),R.layout.marker_details_dialog,null,false);
        dialogBinding.setLifecycleOwner(this);
        dialogBinding.setClick(new DialogClick());
        dialog.setContentView(dialogBinding.getRoot());
        dialog.setCancelable(false);

        dialogBinding.setVieModel(viewModel);

        dialogBinding.makerTypeRadio.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i){
                case R.id.img:
                    viewModel.markerType.setValue("image");
                    break;
                case R.id.txt:
                    viewModel.markerType.setValue("text");
                    break;
                case R.id.person:
                    viewModel.markerType.setValue("person");
                    break;
            }
        });

        dialog.show();

    }

    public class DialogClick{
        public void submit(View view){
            progressDialog.show();
            viewModel.submitMarker();
        }

        public void selectImage(View view){
            ImagePicker.with(ViewImageActivity.this)
                    .crop()
                    .galleryOnly()
                    .start(0);
        }
        public void showBottomDialog(View view){
            showAllMarkersDialog();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            assert data != null;
            Uri uri = data.getData();
            if (requestCode == 0) {
//                binding.image.setImageURI(uri);
                viewModel.markerImageUri.postValue(uri);
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show();
        }

    }

    public void showAllMarkersDialog(){

        bottomSheerDialog = new BottomSheetDialog(ViewImageActivity.this);

        AllMarkersDialogBinding allMarkersDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(this),R.layout.all_markers_dialog,null,false);
        bottomSheerDialog.setContentView(allMarkersDialogBinding.getRoot());
        allMarkersDialogBinding.setLifecycleOwner(this);



        allMarkersDialogBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        allMarkersDialogBinding.recycler.setAdapter(allMarkersAdapter);


        viewModel.hideMarkersBottomSheet.postValue(false);

        bottomSheerDialog.show();
    }

}