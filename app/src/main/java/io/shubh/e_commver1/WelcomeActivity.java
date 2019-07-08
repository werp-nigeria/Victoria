package io.shubh.e_commver1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class WelcomeActivity extends AppCompatActivity {

    private String TAG = "WelcomeActivity";
    private FirebaseAuth mAuth;
    // Access a Cloud Firestore instance from your Activity


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mAuth = FirebaseAuth.getInstance();


        if (GoogleSignIn.getLastSignedInAccount(WelcomeActivity.this) != null) {
            // signed in. Show the "sign out" button and explanation.

            // try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(WelcomeActivity.this);
            firebaseAuthWithGoogle(account);
            /*} catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }*/

            Toast.makeText(this, "true", Toast.LENGTH_SHORT).show();
            // ...
            Toast.makeText(WelcomeActivity.this, "you are already signed in", Toast.LENGTH_LONG).show();


        } else {
            // not signed in. Show the "sign in" button and explanation.
            Toast.makeText(this, "false", Toast.LENGTH_SHORT).show();
            // ...
        }


        Button bt_for_login = (Button) findViewById(R.id.id_fr_bt_login);
        Button bt_for_signup = (Button) findViewById(R.id.id_fr_bt_signup);


        bt_for_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(in);
            }
        });

        bt_for_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(WelcomeActivity.this, SignupActivity.class);
                startActivity(in);
            }
        });
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
                            //   updateUI(user);

                            Toast.makeText(WelcomeActivity.this, "you are already signed in", Toast.LENGTH_LONG).show();

                            Intent in = new Intent(WelcomeActivity.this, MainActivity.class);
                            startActivity(in);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(WelcomeActivity.this, "you are not able to login to google", Toast.LENGTH_LONG).show();
                            //   updateUI(null);
                        }

                        // ...
                    }
                });
    }
}
