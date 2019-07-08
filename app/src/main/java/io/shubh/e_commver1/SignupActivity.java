package io.shubh.e_commver1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private SignInButton signIn;
    private Button signout;
    private int RC_SIGN_IN=7;
    GoogleSignInClient mGoogleSignInClient;
    private String TAG="LoginActivity";
    private FirebaseAuth mAuth;
    FirebaseFirestore db ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        db = FirebaseFirestore.getInstance();

        signIn=(SignInButton)findViewById(R.id.sign_in_button_2);
        signout=findViewById(R.id.sign_out);
        mAuth=FirebaseAuth.getInstance();
        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
      /*  signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                signout.setVisibility(View.GONE);
            }
        });*/
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                            push_the_uid_to_users_node();



                            Intent in = new Intent(SignupActivity.this, MainActivity.class);
                            startActivity(in);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignupActivity.this,"you are not able to login to google",Toast.LENGTH_LONG).show();
                            updateUI(null);


                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
 //       signout.setVisibility(View.VISIBLE);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            Toast.makeText(this,"Name of the user :"+personName+"user id is :"+personId,Toast.LENGTH_SHORT).show();
        }

    }



    private void push_the_uid_to_users_node() {

        //this function is executed each time user logsin  but data can only be pushed only once and that is the first time user logs in





        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> users_list = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        users_list.add(document.getId());
                    }


                    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

                    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                    // Create a new user with a first and last name


                    if(!users_list.contains(currentFirebaseUser.getUid())) {

                        Map<String, Object> user_node_data = new HashMap<>();
                        user_node_data.put("uid", currentFirebaseUser.getUid());
                        user_node_data.put("name", acct.getDisplayName());
                        user_node_data.put("email", acct.getEmail());
                        //below value is false by default
                        user_node_data.put("is a seller also ?", false);

                        db.collection("users").document(currentFirebaseUser.getUid())
                                .set(user_node_data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });
                    }


                    Log.d(TAG, users_list.toString());
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });


    }
}
