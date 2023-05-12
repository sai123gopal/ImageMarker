package com.saigopal.imagemarker.utils;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.saigopal.imagemarker.R;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

public class DataBindingUtils {

    @BindingAdapter({"setTime"})
    public static void time(TextView textView, Timestamp timestamp){
           Date time = timestamp.toDate();
           String dateTime = new PrettiedFormat().Ago(time.toString());
           textView.setText(dateTime);
    }

    @BindingAdapter({"loadImage"})
    public static void loadImage(ImageView imageView, String url){
        Glide.with(imageView.getContext())
                .load(url)
                .placeholder(R.drawable.ic_outline_image_24)
                .into(imageView);
    }
    
}
