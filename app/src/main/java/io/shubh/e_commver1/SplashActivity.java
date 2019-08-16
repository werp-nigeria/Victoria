package io.shubh.e_commver1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.*;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;

public class SplashActivity extends AppCompatActivity {

    ImageView splashimageicon;

ProgressBar progressBar;

Boolean isInternetConnected ;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        DoUiWork();


    }

    private void DoUiWork() {
        splashimageicon=(ImageView)findViewById(R.id.imagesplash);

        splashimageicon.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.simpleanimation);

         progressBar =(ProgressBar)findViewById(R.id.id_fr_prggrs_bar_splash_screen);

        splashimageicon.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                //no code her cause it delays the animation start

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                          doThePrerequisiteLogic();
                    }
                }, 250);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //----------------------------------
       // changing the status bar and system navigation color
                //system key color change
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#8BC34A"));
        }

            if (Build.VERSION.SDK_INT >= 21) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor("#8BC34A"));
            }
    }

    private void doThePrerequisiteLogic() {
        progressBar.setVisibility(View.VISIBLE);

        isInternetConnected = haveNetworkConnection();

        if(isInternetConnected){
            Toast.makeText(SplashActivity.this, "internet connected", Toast.LENGTH_SHORT).show    ();

            //since internet is connected check if a user was already logged in or not..else send to the welcome page containg login /signuop option
            LoginRelatedWork();




            //in future also check that whether the user had already loogged in as guest..so that he can be send too the home activity as a guest...but for now each time he wants to enter as guest ...then he would have to enter while going through the welcome activity
        }else{
            Toast.makeText(SplashActivity.this, "internet not connected", Toast.LENGTH_SHORT).show    ();

            //throw an intent for an activity notifying ..that no internet connection found  and having a button to try again


        }

                      /*  finish();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        finish();
*/
    }

    private void LoginRelatedWork() {

       FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user!=null) {
            // User is signed in.
            Toast.makeText(SplashActivity.this, "u are looged in already", Toast.LENGTH_SHORT).show    ();

            boolean googleSignIn =false;

            //now to find out whther the method of login is using google sign in or email with filrebase
            for (UserInfo userinfo: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                if (userinfo.getProviderId().equals("google.com")) {
                    googleSignIn =true;
                }
            }

            if (googleSignIn == true) {
             //means user had logged in using google sign in

               // so use google code and method to extract user data
                googleSignInExtractInformationAndStoreGlobally();
                Intent in = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(in);

                //animation for sliding activity
                overridePendingTransition(R.anim.right_in, R.anim.left_out);



            }else{
                //user had logged in using other signin option ..so use other methods to extract data...or maybe we can extraxt data from user table in firebase database using the Uid provided by firebase ...for that we need to ask for esssential info at the time of sign up using other method ...if the firebase email authentication is nt able to get the infromation himself

            }



        } else {
            // No user is signed in.
            Toast.makeText(SplashActivity.this, "not logged in already", Toast.LENGTH_SHORT).show();

            //redirecting him to welcome activity
            Intent in = new Intent(SplashActivity.this, WelcomeActivity.class);
            startActivity(in);

            //animation for sliding activity
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }


    }

    private void googleSignInExtractInformationAndStoreGlobally() {

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
           // String personName = acct.getDisplayName();
           /* String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();*/
       //     String personId = acct.getId();
         //   Uri personPhoto = acct.getPhotoUrl();

            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
       //     currentFirebaseUser.getUid();


            Log.d("&&&&&&&&&&&&&&&&&&&", "name -"+acct.getDisplayName() +" email - " +acct.getId());


           StaticClassForGlobalInfo.UId  = currentFirebaseUser.getUid();
            StaticClassForGlobalInfo.UserEmail  = acct.getEmail();
            StaticClassForGlobalInfo.UserName  =  acct.getDisplayName();
            StaticClassForGlobalInfo.isLoggedIn  =  true;
        }

    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}