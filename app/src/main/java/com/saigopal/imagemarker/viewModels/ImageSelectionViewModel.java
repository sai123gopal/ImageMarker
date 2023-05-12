package com.saigopal.imagemarker.viewModels;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageSelectionViewModel extends AndroidViewModel {

    public MutableLiveData<String> name;
    public MutableLiveData<String> uploadImageStatus;
    public MutableLiveData<Uri> imageUri;
    public MutableLiveData<String> imageUrl;
    private DocumentReference postKey;
    private final String userId;

    public ImageSelectionViewModel(@NonNull Application application) {
        super(application);
        name = new MutableLiveData<>("");
        uploadImageStatus = new MutableLiveData<>("");
        imageUrl = new MutableLiveData<>("");
        imageUri = new MutableLiveData<>();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    public void uploadImageToFirebase(){
        if(name.getValue()==null || name.getValue().isEmpty()){
            uploadImageStatus.postValue("Please enter name");
        }else if(imageUri.getValue()==null){
            uploadImageStatus.postValue("Please select image");
        }else {
            UploadImage();
        }
    }


    private void UploadImage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        postKey = FirebaseFirestore.getInstance().collection("Images").document();
        String postId = postKey.getId();

        byte[] data = new byte[0];
        try {
            data = getBytes(getApplication(),imageUri.getValue());
        } catch (IOException e) {
            e.printStackTrace();
            uploadImageStatus.postValue("Error");
        }

        StorageReference storageRef = storage.getReference().child(userId);
        StorageReference imagesRef = storageRef.child(postId);
        UploadTask uploadTask = imagesRef.putBytes(data);

        uploadTask.addOnProgressListener(snapshot -> {
            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
        });

        uploadTask.addOnFailureListener(exception -> {
            uploadImageStatus.postValue("Error");
        }).addOnSuccessListener(taskSnapshot ->
                imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    imageUrl.setValue( uri.toString());
                    uploadImageStatus.postValue(uri.toString());
                    saveDataInFireStore();
                }));
    }


    public static byte[] getBytes(Context context, Uri uri) throws IOException {
        InputStream iStream = context.getContentResolver().openInputStream(uri);
        try {
            return getBytes(iStream);
        } finally {
            try {
                iStream.close();
            } catch (IOException ignored) {  }
        }
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {

        byte[] bytesResult;
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        try {
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            bytesResult = byteBuffer.toByteArray();
        } finally {
            try{ byteBuffer.close(); } catch (IOException ignored){  }
        }
        return bytesResult;
    }

    public void saveDataInFireStore(){
        Map<String, Object> postData = new HashMap<>();

        postData.put("Name", name.getValue());
        postData.put("UserId",userId );
        postData.put("ImageUrl",imageUrl.getValue());
        postData.put("Markers",0);
        postData.put("Time", FieldValue.serverTimestamp());

        postKey.set(postData)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        uploadImageStatus.postValue("Success");
                    }else {
                        uploadImageStatus.postValue("Error");
                    }
                });

    }
}
