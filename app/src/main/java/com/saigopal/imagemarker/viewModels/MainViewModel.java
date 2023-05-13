package com.saigopal.imagemarker.viewModels;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.saigopal.imagemarker.models.ImageItemsModel;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    public MutableLiveData<List<ImageItemsModel>> imagesList;
    public MutableLiveData<String> status;

    public MainViewModel(@NonNull Application application) {
        super(application);
        imagesList = new MutableLiveData<>();
        status = new MutableLiveData<>("");
    }

    public void getImagesData(){
        List<ImageItemsModel> itemsModelList = new ArrayList<>();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("Images")
                .whereEqualTo("UserId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        status.setValue("Success");
                    }
                })
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    queryDocumentSnapshots.forEach(queryDocumentSnapshot -> {
                        String name = queryDocumentSnapshot.get("Name").toString();
                        String userId1 = queryDocumentSnapshot.get("UserId").toString();
                        String imageUrl = queryDocumentSnapshot.get("ImageUrl").toString();
                        Timestamp timestamp = queryDocumentSnapshot.getTimestamp("Time");
                        Long markers = queryDocumentSnapshot.getLong("Markers");
                        itemsModelList.add(new ImageItemsModel(name,imageUrl, userId1,timestamp,markers,queryDocumentSnapshot.getId()));
                    });
                    status.postValue("Success");
                    imagesList.postValue(itemsModelList);
                })
                .addOnFailureListener(e -> status.postValue("Error"));

    }
}
