package com.saigopal.imagemarker.viewModels;

import static com.saigopal.imagemarker.viewModels.ImageSelectionViewModel.getBytes;

import android.app.Application;
import android.net.Uri;

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
    public MutableLiveData<Boolean> hideMarkersBottomSheet;
    public MutableLiveData<String> markerType;
    public MutableLiveData<String> content;
    public MutableLiveData<String> status;
    public MutableLiveData<Uri> imageUri;
    public MutableLiveData<Uri> markerImageUri;
    private  ArrayList<Integer> points;
    public TouchImageView touchImageView;
    public MutableLiveData<List<MarkerModel>> markerModelArrayList;

    public CurrentImageViewModel(@NonNull Application application) {
        super(application);
        currentImageModel = new MutableLiveData<>();
        markersList = new MutableLiveData<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        docId = new MutableLiveData<>();
        showMarkerDetailsDialog = new MutableLiveData<>(false);
        hideMarkersBottomSheet = new MutableLiveData<>(false);
        markerType = new MutableLiveData<>("");
        content = new MutableLiveData<>("");
        status = new MutableLiveData<>("");
        imageUri = new MutableLiveData<>();
        markerImageUri = new MutableLiveData<>();
        points = new ArrayList<>();
        markerModelArrayList = new MutableLiveData<>();
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
        List<MarkerModel> modelList = new ArrayList<>();
        firebaseFirestore.collection("Markers")
                .whereEqualTo("ImageId",docId.getValue())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){

                        task.getResult().forEach(queryDocumentSnapshot -> {
                            String type = queryDocumentSnapshot.getString("markerType");
                            String content = queryDocumentSnapshot.getString("markerContent");
                            String id = queryDocumentSnapshot.getId();
                            String imageId = queryDocumentSnapshot.getString("ImageId");
                            Timestamp timestamp = queryDocumentSnapshot.getTimestamp("Time");
                            ArrayList<Long> points = (ArrayList<Long>) queryDocumentSnapshot.get("Points");


                            modelList.add(new MarkerModel(type,id,content,timestamp,points,imageId) );

                            Long x = Long.valueOf(points.get(0));
                            Long y = Long.valueOf(points.get(1));
                            touchImageView.addMarker(x.intValue(),y.intValue());
                        });
                        markerModelArrayList.postValue(modelList);
                    }else {
                        status.setValue(task.getException().toString());
                    }
                });
    }

    public void submitMarker(){

        if(markerType.getValue().equals("image")){
            if(content.getValue()==null || content.getValue().isEmpty()){
                status.postValue("Please select image");
                return;
            }
            getImageUrl();
        }else {
            postMarker();
        }

    }

    public void postMarker(){
        if(content.getValue()==null || content.getValue().isEmpty()){
            status.postValue("Please enter details");
            return;
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
                        markerType.postValue("");
                        markerImageUri.postValue(null);
                        content.postValue("");
                        touchImageView.addMarker(points.get(0),points.get(1));
                    }
                });
    }

    private void getImageUrl() {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        String userId= FirebaseAuth.getInstance().getCurrentUser().getUid();


        byte[] data = new byte[0];
        try {
            data = getBytes(getApplication(),markerImageUri.getValue());
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
                    content.setValue(uri.toString());
                    postMarker();
                }));

    }


    public void setMarker(int x,int y) {

        showMarkerDetailsDialog.postValue(true);
        points.add(x);
        points.add(y);

    }

    
}
