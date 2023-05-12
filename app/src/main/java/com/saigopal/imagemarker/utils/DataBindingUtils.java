package com.saigopal.imagemarker.utils;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.saigopal.imagemarker.R;
import com.saigopal.imagemarker.databinding.ImageItemsBinding;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

public class DataBindingUtils {

    @BindingAdapter({"setTime"})
    public static void time(TextView textView, Timestamp timestamp) {
        Date time = timestamp.toDate();
        String dateTime = new PrettiedFormat().Ago(time.toString());
        textView.setText(dateTime);
    }

    @BindingAdapter({"loadImage"})
    public static void loadImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .placeholder(R.drawable.ic_outline_image_24)
                .into(imageView);
    }

    @BindingAdapter({"getMarkersCount"})
    public static void getMarkersCount(TextView textView, String id) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query = db.collection("Markers").whereEqualTo("ImageId", id);
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AggregateQuerySnapshot snapshot = task.getResult();
                textView.setText(String.valueOf(snapshot.getCount()));
            }else {
                textView.setText(0+"");
            }
        });

    }

}
