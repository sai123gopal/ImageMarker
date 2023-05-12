package com.saigopal.imagemarker.viewModels;

import static com.saigopal.imagemarker.viewModels.ImageSelectionViewModel.getBytes;

import android.app.Application;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.saigopal.imagemarker.models.ImageItemsModel;
import com.saigopal.imagemarker.models.MarkerModel;
import com.saigopal.imagemarker.models.Points;
import com.saigopal.imagemarker.utils.TouchImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CurrentImageViewModel extends AndroidViewModel {

    public MutableLiveData<ImageItemsModel> currentImageModel;
    private FirebaseFirestore firebaseFirestore;
    public MutableLiveData<String> docId;
    public MutableLiveData<List<Points>> markersList;
    public MutableLiveData<Boolean> showMarkerDetailsDialog;
    public MutableLiveData<String> markerType;
    public MutableLiveData<String> content;
    public MutableLiveData<String> status;
    public MutableLiveData<Uri> imageUri;
    private  ArrayList<Integer> points;
    public TouchImageView touchImageView;
    public ArrayList<MarkerModel> markerModelArrayList;

    public CurrentImageViewModel(@NonNull Application application) {
        super(application);
        currentImageModel = new MutableLiveData<>();
        markersList = new MutableLiveData<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        docId = new MutableLiveData<>();
        showMarkerDetailsDialog = new MutableLiveData<>(false);
        markerType = new MutableLiveData<>("");
        content = new MutableLiveData<>("");
        status = new MutableLiveData<>("");
        imageUri = new MutableLiveData<>();
        points = new ArrayList<>();
        markerModelArrayList = new ArrayList<>();
    }

    public void setTouchImageView(TouchImageView imageView){
        touchImageView = imageView;
    }
    
    public void getDetails(){
        firebaseFirestore.collection("Images")
                .document(docId.getValue())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        String name = task.getResult().get("Name").toString();
                        String userId1 = task.getResult().get("UserId").toString();
                        String imageUrl = task.getResult().get("ImageUrl").toString();
                        Timestamp timestamp = task.getResult().getTimestamp("Time");
                        Long markers = task.getResult().getLong("Markers");
                        currentImageModel.postValue(new ImageItemsModel(name,imageUrl, userId1,timestamp,markers,task.getResult().getId()));
                    }
                });
    }

    public void getAllMarkers(){
        firebaseFirestore.collection("Markers")
                .whereEqualTo("ImageId",docId.getValue())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(getApplication(), "Done"+task.getResult().size(), Toast.LENGTH_SHORT).show();
                        task.getResult().forEach(queryDocumentSnapshot -> {
                            String type = queryDocumentSnapshot.getString("markerType");
                            String content = queryDocumentSnapshot.getString("markerContent");
                            String id = queryDocumentSnapshot.getId();
                            String imageId = queryDocumentSnapshot.getString("ImageId");
                            Timestamp timestamp = queryDocumentSnapshot.getTimestamp("Time");
                            ArrayList<Long> points = (ArrayList<Long>) queryDocumentSnapshot.get("Points");

                            Long x = Long.valueOf(points.get(0));
                            Long y = Long.valueOf(points.get(1));

                            markerModelArrayList.add(new MarkerModel(type,id,content,timestamp,points,imageId));
                            touchImageView.addMarker(x.intValue(),y.intValue());
                        });
                    }else {
                        status.setValue(task.getException().toString());
                    }
                });
    }

    public void submitMarker(){
        if(markerType.getValue().equals("image")){
           content.setValue(getImageUrl());
        }
        showMarkerDetailsDialog.postValue(false);
        HashMap<String,Object> markerMap = new HashMap<>();
        markerMap.put("Points",points);
        markerMap.put("markerType",markerType.getValue());
        markerMap.put("markerContent",content.getValue());
        markerMap.put("Time", FieldValue.serverTimestamp());
        markerMap.put("ImageId",docId.getValue());

        DocumentReference reference = firebaseFirestore.collection("Markers")
                .document();

        reference.set(markerMap)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        status.postValue("Success");
                        touchImageView.addMarker(points.get(0),points.get(1));
                    }
                });
    }

    private String getImageUrl() {
        final String[] url = {""};
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String userId= FirebaseAuth.getInstance().getCurrentUser().getUid();


        byte[] data = new byte[0];
        try {
            data = getBytes(getApplication(),imageUri.getValue());
        } catch (IOException e) {
            e.printStackTrace();
            status.postValue("Error");
        }

        StorageReference storageRef = storage.getReference().child(userId);
        StorageReference imagesRef = storageRef.child(String.valueOf(Math.random()));
        UploadTask uploadTask = imagesRef.putBytes(data);

        uploadTask.addOnProgressListener(snapshot -> {
            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
        });

        uploadTask.addOnFailureListener(exception -> {
            status.postValue("Error");
        }).addOnSuccessListener(taskSnapshot ->
                imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    url[0] = uri.toString();
                }));

        return url[0];
    }


    public void setMarker(int x,int y) {

        showMarkerDetailsDialog.postValue(true);
        points.add(x);
        points.add(y);

    }

    
}
