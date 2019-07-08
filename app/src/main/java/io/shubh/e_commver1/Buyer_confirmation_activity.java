package io.shubh.e_commver1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Buyer_confirmation_activity extends AppCompatActivity {

    FirebaseFirestore db ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_confirmation_activity);

        db = FirebaseFirestore.getInstance();
        final EditText et_fr_phnno = (EditText)findViewById(R.id.id_fr_et_phn_no);

        //do phone no validation later


        Button bt_fr_next =(Button)findViewById(R.id.id_fr_bt_next);
        bt_fr_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(et_fr_phnno.getText().toString().length()==10){

                    int no =Integer.parseInt(et_fr_phnno.getText().toString());

//add the no and user status of being a seller now
                    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

                    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                    // Create a new user with a first and last name


                        Map<String, Object> user_node_data = new HashMap<>();
                    //    user_node_data.put("uid", currentFirebaseUser.getUid());
                 //       user_node_data.put("name", acct.getDisplayName());
                //        user_node_data.put("email", acct.getEmail());
                        //below value is false by default
                        user_node_data.put("is a seller also ?", true);
                    user_node_data.put("phone no", no);

                        db.collection("users").document(currentFirebaseUser.getUid())
                                .set(user_node_data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("tag", "DocumentSnapshot successfully written!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("tag", "Error writing document", e);
                                    }
                                });






                    Intent in = new Intent(Buyer_confirmation_activity.this, Buyer_items_list.class);
                    startActivity(in);


                }else{

                    Toast.makeText(Buyer_confirmation_activity.this, "enter valid no", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
