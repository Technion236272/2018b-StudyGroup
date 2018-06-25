package study.group;

import android.content.Intent;
import android.util.Log;

import com.facebook.Profile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {

        //Getting registration token
        final HashMap<String,Object> userData = new HashMap<>();
        String token_id = FirebaseInstanceId.getInstance().getToken();
        userData.put("id", Profile.getCurrentProfile().getId());
        userData.put("name",Profile.getCurrentProfile().getName());
        userData.put("token_id",token_id);
        FirebaseFirestore.getInstance().collection("Users").document(Profile.getCurrentProfile().getId()).update(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }
}
