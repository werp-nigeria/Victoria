package io.shubh.e_commver1;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        Button buyer = (Button)findViewById(R.id.button);

buyer.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        //checking if the user is becoming the buyer for the first time


        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;


        DocumentReference pg = db.collection("users").document(currentFirebaseUser.getUid());
        pg.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();

                   Boolean is_user_a_seller  = (Boolean) doc.get("is a seller also ?");

                  if(is_user_a_seller == true){

                      Intent in = new Intent(MainActivity.this, Buyer_items_list.class);
                      startActivity(in);

                  }else{

                      Intent in = new Intent(MainActivity.this, Buyer_confirmation_activity.class);
                      startActivity(in);
                  }


                }   }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });




    }
});


    }
}
