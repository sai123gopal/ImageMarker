package com.saigopal.imagemarker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.saigopal.imagemarker.databinding.ActivityViewImageBinding;
import com.saigopal.imagemarker.databinding.MarkerDetailsDialogBinding;
import com.saigopal.imagemarker.utils.TouchImageView;
import com.saigopal.imagemarker.viewModels.CurrentImageViewModel;

public class ViewImageActivity extends AppCompatActivity {

    private ActivityViewImageBinding binding;
    private CurrentImageViewModel viewModel;

    boolean isDoubleClicked=false;

    Handler handler=new Handler();
    Runnable r= () -> {
        isDoubleClicked=false;
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_view_image);
        binding.setLifecycleOwner(this);

        viewModel = new ViewModelProvider(this).get(CurrentImageViewModel.class);
        binding.setViewModel(viewModel);

        String  id = getIntent().getStringExtra("id");

        viewModel.docId.setValue(id);

        viewModel.getDetails();

        TouchImageView touchImageView = binding.image;

        touchImageView.setViewModel(viewModel);

        observeLiveData();


    }

    private void observeLiveData() {
        viewModel.showMarkerDetailsDialog.observe(this, aBoolean -> {
            if(aBoolean){
                showAlert();
            }
        });
    }

    public void showAlert(){
        Dialog dialog = new Dialog(this);
        MarkerDetailsDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(this),R.layout.marker_details_dialog,null
        ,false);

        dialog.setContentView(dialogBinding.getRoot());
        dialog.setCancelable(false);

        dialogBinding.setVieModel(viewModel);




        dialog.show();

    }

    public class DialogClick{
        public void submit(View view){

        }

        public void selectImage(View view){
            ImagePicker.with(ViewImageActivity.this)
                    .crop()
                    .galleryOnly()
                    .start(0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            assert data != null;
            Uri uri = data.getData();
            if (requestCode == 0) {
                binding.image.setImageURI(uri);
                viewModel.imageUri.postValue(uri);
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show();
        }

    }


}