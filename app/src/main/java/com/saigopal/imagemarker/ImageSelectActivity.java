package com.saigopal.imagemarker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.saigopal.imagemarker.viewModels.ImageSelectionViewModel;
import com.saigopal.imagemarker.databinding.ActivityImageSelectBinding;


public class ImageSelectActivity extends AppCompatActivity {

    private ActivityImageSelectBinding binding;
    private ImageSelectionViewModel viewModel;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_image_select);
        binding.setLifecycleOwner(this);

        viewModel = new ViewModelProvider(this).get(ImageSelectionViewModel.class);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        binding.setClick(new Click());
        binding.setViewModel(viewModel);

        observeLiveData();


    }

    public void observeLiveData(){

        viewModel.uploadImageStatus.observe(this, s -> {
            if(!s.isEmpty()) {
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                if (s.equals("Success")) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
            }
        });

    }

    public class Click{
        public void selectImage(View view){
            ImagePicker.with(ImageSelectActivity.this)
                    .crop()
                    .galleryOnly()
                    .start(0);
        }

        public void uploadImage(View view){
            progressDialog.show();
            viewModel.uploadImageToFirebase();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}