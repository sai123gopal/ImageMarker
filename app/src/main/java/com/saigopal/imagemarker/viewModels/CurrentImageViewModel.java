package com.saigopal.imagemarker.viewModels;

import android.app.Application;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saigopal.imagemarker.models.ImageItemsModel;
import com.saigopal.imagemarker.models.Points;

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
    public MutableLiveData<Uri> imageUri;

    public CurrentImageViewModel(@NonNull Application application) {
        super(application);
        currentImageModel = new MutableLiveData<>();
        markersList = new MutableLiveData<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        docId = new MutableLiveData<>();
        showMarkerDetailsDialog = new MutableLiveData<>(false);
        markerType = new MutableLiveData<>("");
        content = new MutableLiveData<>("");
        imageUri = new MutableLiveData<>();
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



    public void setMarker(int x,int y){

        showMarkerDetailsDialog.postValue(true);

        HashMap<String,Object> markerMap = new HashMap<>();
        ArrayList<Integer> points = new ArrayList<>();
        points.add(x);
        points.add(y);
        markerMap.put("Points",points);
        markerMap.put("Time", FieldValue.serverTimestamp());
        markerMap.put("ImageId",docId.getValue());

        firebaseFirestore.collection("Markers")
                .document()
                .set(markerMap)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplication(), "MarkerAdded", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    
}
