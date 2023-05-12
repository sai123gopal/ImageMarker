package com.saigopal.imagemarker.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.ImageButton;
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.saigopal.imagemarker.R;

public class ShowImage {
    Context context;
    Bitmap bitmap;
    @SuppressLint("ClickableViewAccessibility")
    public ShowImage(Context context, Bitmap bitmap) {
        this.context = context;
        this.bitmap = bitmap;

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.image_dialog);

        android.widget.ImageView imageView = dialog.findViewById(R.id.image);
        ImageButton CloseButton = dialog.findViewById(R.id.close);
        CloseButton.setOnClickListener(v -> dialog.dismiss());
        imageView.setImageBitmap(bitmap);
        imageView.setOnTouchListener(new ImageMatrixTouchHandler(context));
        dialog.show();
    }
}