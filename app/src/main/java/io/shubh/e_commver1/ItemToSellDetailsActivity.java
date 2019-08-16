package io.shubh.e_commver1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
//import com.isapanah.awesomespinner.AwesomeSpinner;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class ItemToSellDetailsActivity extends AppCompatActivity {

    FirebaseFirestore db;
    //AwesomeSpinner my_spinner;
    String category_selected_from_Bottomsheet;


    Boolean[] array_whether_image_view_have_image = {false, false, false, false, false};
    String[] image_uri_S = {"default", "default", "default", "default", "default"};
    private StorageReference mStorageRef;

    ProgressDialog progressDialog;

    BottomSheetBehavior behavior;
    CoordinatorLayout coordinatorLayout;

    String TAG = "%%%%%%%%%%%";

    Boolean is_bottom_sheet_expanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_to_sell_details);

        progressDialog = new ProgressDialog(this);

        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        Button bt_fr_choose_images = (Button) findViewById(R.id.id_fr_bt_choose_images);


        bt_fr_choose_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (no_of_occupied_imageviews() != 5) {
                    Options options = Options.init()
                            .setRequestCode(100)                                                 //Request code for activity results
                            .setCount(5 - no_of_occupied_imageviews())                                                         //Number of images to restict selection count
                            .setFrontfacing(false)                                                //Front Facing camera on start
                            .setImageQuality(ImageQuality.HIGH)                                  //Image Quality
                            //        .setPreSelectedUrls(returnValue)                                     //Pre selected Image Urls
                            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)           //Orientaion
                            .setPath("/e-comm/images");                                             //Custom Path For Image Storage

                    Pix.start(ItemToSellDetailsActivity.this, options);
                } else {
                    Toast.makeText(ItemToSellDetailsActivity.this, "only 5 images can be selected. Long press on image to delete it.", Toast.LENGTH_LONG).show();

                }
            }
        });

        //===========================
        //        my_spinner = (AwesomeSpinner) findViewById(R.id.my_spinner);
        //  populate_bottom_sheet_with_firebase_data_catgr();

        setup_done_button();


        //--------------------------

        setupCategorySelectionStuff();

//==================================testing


    }

    private void setupCategorySelectionStuff() {

        Button selectCatgr = (Button) findViewById(R.id.id_fr_bt_selct_catgr);

        View bottomSheet = findViewById(R.id.id_fr_ll_bottom_sheet_container);
        behavior = BottomSheetBehavior.from(bottomSheet);

        View dim_background_of_bottom_sheet = (View) findViewById(R.id.touch_to_dismiss_bottom_sheet_dim_background);
        dim_background_of_bottom_sheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
                if (newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    dim_background_of_bottom_sheet.setVisibility(View.GONE);
                    is_bottom_sheet_expanded = false;
                } else {
                    dim_background_of_bottom_sheet.setVisibility(View.VISIBLE);
                    is_bottom_sheet_expanded = true;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });

        selectCatgr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                make_CATEGORY_layouts_dynamically_an_add_to_bottom_sheet(false);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                is_bottom_sheet_expanded = true;
            }
        });


//TODO --make an object of scrollview in bottom sheet here====>make a ontouch listener for that object and ..when that is touched ..make the bottomsheet non clickable
        //TODo -this is to solve the isssue that on scrolling up inside bottom sheet ,the bottom sheet gets swiped down insted

        //TODo - or I can do that is register a clallback to on bottomsheet swiped.....and make the scrollview scrolled to top ...when the bottomshett gets moved uninteionally
    }

    private void make_CATEGORY_layouts_dynamically_an_add_to_bottom_sheet(Boolean is_it_called_from_sub_ctgr) {

        TextView tv_bottom_sheet_header = (TextView) findViewById(R.id.id_br_bottom_sheet_tv_header);
        tv_bottom_sheet_header.setText("Select a Category");


        int categories_size = StaticClassForGlobalInfo.categories_names.size();

        //now listing alll the bookmarked url in the list//but mkiang view for them programmattically
        final TextView[] tv_of_category = new TextView[categories_size];
        final ImageView[] iv = new ImageView[categories_size];
        final RelativeLayout[] rl_row_container_categories = new RelativeLayout[categories_size];

        final LinearLayout ll_container_parent = (LinearLayout) findViewById(R.id.id_fr_ll_bottom_sheet_list_items_container);
        //clearing all the previous textvies
        ll_container_parent.removeAllViews();

        final float scale = getResources().getDisplayMetrics().density;

        final RelativeLayout.LayoutParams lp_for_rl_row = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (30 * scale + 0.5f));
        lp_for_rl_row.setMargins((int) (5 * scale + 0.5f), (int) (12 * scale + 0.5f), (int) (10 * scale + 0.5f), (int) (0 * scale + 0.5f));

        final RelativeLayout.LayoutParams lp_for_tv = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp_for_rl_row.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        final RelativeLayout.LayoutParams lp_for_iv = new RelativeLayout.LayoutParams((int) (25 * scale + 0.5f), (int) (25 * scale + 0.5f));
        lp_for_iv.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        for (int i = 0; i < categories_size; i++) {

            rl_row_container_categories[i] = new RelativeLayout(this);
            tv_of_category[i] = new TextView(this);
            iv[i] = new ImageView(this);

            rl_row_container_categories[i].setLayoutParams(lp_for_rl_row);
            // ll_row_container[i].setPadding((int) (3 * scale + 0.5f),(int) (3 * scale + 0.5f),(int) (3 * scale + 0.5f),(int) (3 * scale + 0.5f));
            // rl_row_container_categories[i].setOrientation(LinearLayout.HORIZONTAL);
                rl_row_container_categories[i].setBackgroundResource(R.drawable.ripple_effect_white_on_dynamicallly_craeted_views_of_bottom_sheet);


            //  tv_of_bookmarked_url[i].setLayoutParams(lp_for_tv_for_url);
            tv_of_category[i].setLayoutParams(lp_for_tv);
            tv_of_category[i].setText(StaticClassForGlobalInfo.categories_names.get(i));
            tv_of_category[i].setTextSize((int) (18));
            tv_of_category[i].setTextColor(Color.parseColor("#ffffff"));

            iv[i].setLayoutParams(lp_for_iv);
            iv[i].setPadding((int) (1.5 * scale + 0.5f), (int) (1.5 * scale + 0.5f), (int) (1.5 * scale + 0.5f), (int) (1.5 * scale + 0.5f));

            rl_row_container_categories[i].addView(tv_of_category[i]);
            rl_row_container_categories[i].addView(iv[i]);
            ll_container_parent.addView(rl_row_container_categories[i]);


            int finalI = i;
            if (StaticClassForGlobalInfo.super_nested_list_of_categories.get(i).get(0).getCategoryNmae() != null && StaticClassForGlobalInfo.super_nested_list_of_categories.get(i).get(0).getListOfsubCategory() != null) {
                iv[i].setImageResource(R.drawable.forward);

                rl_row_container_categories[i].setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {


                        //doing an animation from right to left..but the below animatoin method doesnt actually moves it..

                        TranslateAnimation animate = new TranslateAnimation(0, -ll_container_parent.getWidth(), 0, 0);
                        animate.setDuration(400);
                        ll_container_parent.startAnimation(animate);
                        animate.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                //Since the animation has ended ,, doing the MIAN task now
                                make_SUB_CATEGORY_layouts_dynamically_an_add_to_bottom_sheet(finalI, false);



                                TranslateAnimation animate2 = new TranslateAnimation(0, 0,0,  -ll_container_parent.getHeight());
                                animate2.setDuration(500);
                                tv_bottom_sheet_header.startAnimation(animate2);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });


                    }
                });
            } else {
                iv[finalI].setImageResource(R.drawable.radio_bt);
                rl_row_container_categories[i].setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {

                        category_selected_from_Bottomsheet = StaticClassForGlobalInfo.categories_names.get(finalI);

                        // Toast.makeText(ItemToSellDetailsActivity.this, StaticClassForGlobalInfo.categories_names.get(finalI) + " is selected as category", Toast.LENGTH_SHORT).show();
                        Toast.makeText(ItemToSellDetailsActivity.this, category_selected_from_Bottomsheet, Toast.LENGTH_SHORT).show();


                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        is_bottom_sheet_expanded = false;

                    }
                });

            }

//----------------------------------------------------------------------------
            //   add_press_effect_button_to_dialog_elements(ll_row_container[i] ,iv_[i],tv_of_bookmarked_url[i]);

        }


        //--------------------------handling Bottom sheet header work------------------------------------------



        ImageButton bt_for_back = (ImageButton) findViewById(R.id.id_fr_bt_bottom_sheet_back);
        bt_for_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //doing an animation from right to left..but the below animatoin method doesnt actually moves it..

                TranslateAnimation animate = new TranslateAnimation(0, ll_container_parent.getWidth(), 0, 0);
                animate.setDuration(400);
                ll_container_parent.startAnimation(animate);
                animate.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        //Since the animation has ended ,, closingg the bottom sheet now
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        is_bottom_sheet_expanded = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });


             /*ll_container_parent.animate()
                     .translationX(ll_container_parent.getWidth())
                     .alpha(0.0f)
                     .setDuration(450)
                     .setListener(new AnimatorListenerAdapter() {
                         @Override
                         public void onAnimationEnd(Animator animation) {
                             super.onAnimationEnd(animation);


                             //closingg the bottom sheet now
                             behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                             is_bottom_sheet_expanded =false;

                             //now the bottomsheet is closed and the postion of ll_container is permanently moved ..so placing it back on its original postion
                          //  ll_container_parent.clearAnimation();
                             view.animate().setListener(null);
                             ll_container_parent.animate().translationX(0);

                         }
                     });*/

            }
        });

        //since all the layouts are added by now ...doing an animation work foe the time when this function loads
        //doing an animation from right to left..but the below animatoin method doesnt actually moves it..
//here no need of animation listener to be attached

        TranslateAnimation animate = new TranslateAnimation(-ll_container_parent.getWidth(), 0, 0, 0);
        animate.setDuration(400);
        ll_container_parent.startAnimation(animate);

//adding a godamn animation to tv as well

        TranslateAnimation animate2 = new TranslateAnimation(0, 0, ll_container_parent.getHeight(), 0);
        animate2.setDuration(500);
        tv_bottom_sheet_header.startAnimation(animate2);


    }


    private void make_SUB_CATEGORY_layouts_dynamically_an_add_to_bottom_sheet(int index_root, Boolean is_it_called_from_sub_sub_catgr_fun) {
        TextView tv_bottom_sheet_header = (TextView) findViewById(R.id.id_br_bottom_sheet_tv_header);
        tv_bottom_sheet_header.setText("Select a Sub-Category");


        int sub_categories_size = StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).size();

        //now listing alll the bookmarked url in the list//but mkiang view for them programmattically
        final TextView[] tv_of_sub_category = new TextView[sub_categories_size];
        final ImageView[] iv = new ImageView[sub_categories_size];
        final RelativeLayout[] rl_row_container_sub_categories = new RelativeLayout[sub_categories_size];

        final LinearLayout ll_container_parent = (LinearLayout) findViewById(R.id.id_fr_ll_bottom_sheet_list_items_container);
        //clearing all the previous textvies with animation
        ll_container_parent.removeAllViews();


        final float scale = getResources().getDisplayMetrics().density;

        final RelativeLayout.LayoutParams lp_for_rl_row = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (30 * scale + 0.5f));
        lp_for_rl_row.setMargins((int) (5 * scale + 0.5f), (int) (12 * scale + 0.5f), (int) (10 * scale + 0.5f), (int) (0 * scale + 0.5f));

        final RelativeLayout.LayoutParams lp_for_tv = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp_for_rl_row.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        final RelativeLayout.LayoutParams lp_for_iv = new RelativeLayout.LayoutParams((int) (25 * scale + 0.5f), (int) (25 * scale + 0.5f));
        lp_for_iv.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        for (int i = 0; i < sub_categories_size; i++) {

            rl_row_container_sub_categories[i] = new RelativeLayout(this);
            tv_of_sub_category[i] = new TextView(this);
            iv[i] = new ImageView(this);

            rl_row_container_sub_categories[i].setLayoutParams(lp_for_rl_row);
            rl_row_container_sub_categories[i].setBackgroundResource(R.drawable.ripple_effect_white_on_dynamicallly_craeted_views_of_bottom_sheet);

            // ll_row_container[i].setPadding((int) (3 * scale + 0.5f),(int) (3 * scale + 0.5f),(int) (3 * scale + 0.5f),(int) (3 * scale + 0.5f));
            // rl_row_container_sub_categories[i].setOrientation(LinearLayout.HORIZONTAL);
            //TODO  //    ll_row_container[i].setBackgroundResource(R.drawable.ripple_for_dialog_box_elements);


            //  tv_of_bookmarked_url[i].setLayoutParams(lp_for_tv_for_url);
            tv_of_sub_category[i].setLayoutParams(lp_for_tv);
            tv_of_sub_category[i].setText(StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(i).categoryNmae);
            tv_of_sub_category[i].setTextSize((int) (18));
            tv_of_sub_category[i].setTextColor(Color.parseColor("#ffffff"));

            iv[i].setLayoutParams(lp_for_iv);
            iv[i].setPadding((int) (1.5 * scale + 0.5f), (int) (1.5 * scale + 0.5f), (int) (1.5 * scale + 0.5f), (int) (1.5 * scale + 0.5f));

            rl_row_container_sub_categories[i].addView(tv_of_sub_category[i]);
            rl_row_container_sub_categories[i].addView(iv[i]);
            ll_container_parent.addView(rl_row_container_sub_categories[i]);



            int finalI = i;
            if (StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(i).getListOfsubCategory() != null) {

                iv[i].setImageResource(R.drawable.forward);

                rl_row_container_sub_categories[i].setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {

                        //doing an animation from right to left..but the below animatoin method doesnt actually moves it..

                        TranslateAnimation animate = new TranslateAnimation(0, -ll_container_parent.getWidth(), 0, 0);
                        animate.setDuration(400);
                        ll_container_parent.startAnimation(animate);
                        animate.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                //Since the animation has ended ,, closingg the bottom sheet now
                                make_SUB_SUB_CATEGORY_layouts_dynamically_an_add_to_bottom_sheet(index_root, finalI);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });


                        TranslateAnimation animate2 = new TranslateAnimation(0, 0,0,  -ll_container_parent.getHeight());
                        animate2.setDuration(500);
                        tv_bottom_sheet_header.startAnimation(animate2);
                    }
                });
            } else {
                iv[finalI].setImageResource(R.drawable.radio_bt);
                rl_row_container_sub_categories[i].setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {

                        category_selected_from_Bottomsheet = StaticClassForGlobalInfo.categories_names.get(index_root) + "/" + StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(finalI).categoryNmae;
                        //     Toast.makeText(ItemToSellDetailsActivity.this, StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(finalI).categoryNmae + " is selected as category", Toast.LENGTH_SHORT).show();
                        Toast.makeText(ItemToSellDetailsActivity.this, category_selected_from_Bottomsheet, Toast.LENGTH_SHORT).show();


                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        is_bottom_sheet_expanded = false;
                    }
                });
            }

            //since all the layouts are added by now ...doing an animation work so
            if (is_it_called_from_sub_sub_catgr_fun == true) {
                TranslateAnimation animate = new TranslateAnimation(-ll_container_parent.getWidth(), 0, 0, 0);
                animate.setDuration(400);
                ll_container_parent.startAnimation(animate);
            } else {
                TranslateAnimation animate = new TranslateAnimation(ll_container_parent.getWidth(), 0, 0, 0);
                animate.setDuration(400);
                ll_container_parent.startAnimation(animate);
            }
//----------------------------------------------------------------------------
            //   add_press_effect_button_to_dialog_elements(ll_row_container[i] ,iv_[i],tv_of_bookmarked_url[i]);
        }

        //--------------------------handling Bottom sheet header work
        ImageButton bt_for_back = (ImageButton) findViewById(R.id.id_fr_bt_bottom_sheet_back);
        bt_for_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //doing an animation from left to right..but the below animatoin method doesnt actually moves it..

                TranslateAnimation animate = new TranslateAnimation(0, ll_container_parent.getWidth(), 0, 0);
                animate.setDuration(350);
                ll_container_parent.startAnimation(animate);
                animate.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        //Since the animation has ended ,, now doing the actual work
                        make_CATEGORY_layouts_dynamically_an_add_to_bottom_sheet(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
        });


        //adding a godamn animation to tv as well

        TranslateAnimation animate2 = new TranslateAnimation(0, 0, ll_container_parent.getHeight(), 0);
        animate2.setDuration(500);
        tv_bottom_sheet_header.startAnimation(animate2);
    }

    private void make_SUB_SUB_CATEGORY_layouts_dynamically_an_add_to_bottom_sheet(int index_root, int index_root_sub) {
        int sub_sub_categories_size = StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(index_root_sub).getListOfsubCategory().size();

        //now listing alll the bookmarked url in the list//but mkiang view for them programmattically
        final TextView[] tv_of_sub_sub_category = new TextView[sub_sub_categories_size];
        final ImageView[] iv = new ImageView[sub_sub_categories_size];
        final RelativeLayout[] rl_row_container_sub_sub_categories = new RelativeLayout[sub_sub_categories_size];

        final LinearLayout ll_container_parent = (LinearLayout) findViewById(R.id.id_fr_ll_bottom_sheet_list_items_container);
        //clearing all the previous textvies
        ll_container_parent.removeAllViews();

        final float scale = getResources().getDisplayMetrics().density;

        final RelativeLayout.LayoutParams lp_for_rl_row = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (30 * scale + 0.5f));
        lp_for_rl_row.setMargins((int) (5 * scale + 0.5f), (int) (12 * scale + 0.5f), (int) (10 * scale + 0.5f), (int) (0 * scale + 0.5f));

        final RelativeLayout.LayoutParams lp_for_tv = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp_for_rl_row.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        final RelativeLayout.LayoutParams lp_for_iv = new RelativeLayout.LayoutParams((int) (25 * scale + 0.5f), (int) (25 * scale + 0.5f));
        lp_for_iv.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        for (int i = 0; i < sub_sub_categories_size; i++) {

            rl_row_container_sub_sub_categories[i] = new RelativeLayout(this);
            tv_of_sub_sub_category[i] = new TextView(this);
            iv[i] = new ImageView(this);

            rl_row_container_sub_sub_categories[i].setLayoutParams(lp_for_rl_row);
            rl_row_container_sub_sub_categories[i].setBackgroundResource(R.drawable.ripple_effect_white_on_dynamicallly_craeted_views_of_bottom_sheet);

            // ll_row_container[i].setPadding((int) (3 * scale + 0.5f),(int) (3 * scale + 0.5f),(int) (3 * scale + 0.5f),(int) (3 * scale + 0.5f));
            // rl_row_container_sub_sub_categories[i].setOrientation(LinearLayout.HORIZONTAL);
            //TODO  //    ll_row_container[i].setBackgroundResource(R.drawable.ripple_for_dialog_box_elements);


            //  tv_of_bookmarked_url[i].setLayoutParams(lp_for_tv_for_url);
            tv_of_sub_sub_category[i].setLayoutParams(lp_for_tv);
            tv_of_sub_sub_category[i].setText(StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(index_root_sub).getListOfsubCategory().get(i));
            tv_of_sub_sub_category[i].setTextSize((int) (18));
            tv_of_sub_sub_category[i].setTextColor(Color.parseColor("#ffffff"));

            iv[i].setLayoutParams(lp_for_iv);
            iv[i].setPadding((int) (1.5 * scale + 0.5f), (int) (1.5 * scale + 0.5f), (int) (1.5 * scale + 0.5f), (int) (1.5 * scale + 0.5f));

            rl_row_container_sub_sub_categories[i].addView(tv_of_sub_sub_category[i]);
            rl_row_container_sub_sub_categories[i].addView(iv[i]);
            ll_container_parent.addView(rl_row_container_sub_sub_categories[i]);


            int finalI = i;
            if (StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(index_root_sub).getListOfsubCategory() != null) {

                iv[i].setImageResource(R.drawable.radio_bt);

                int finalI1 = i;
                rl_row_container_sub_sub_categories[i].setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {

                        category_selected_from_Bottomsheet = StaticClassForGlobalInfo.categories_names.get(index_root) + "/" + StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(index_root_sub).categoryNmae + "//" + StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(index_root_sub).getListOfsubCategory().get(finalI1);
//                        Toast.makeText(ItemToSellDetailsActivity.this,  StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(index_root_sub).getListOfsubCategory().get(finalI1) + " is selected as category", Toast.LENGTH_SHORT).show();
                        Toast.makeText(ItemToSellDetailsActivity.this, category_selected_from_Bottomsheet, Toast.LENGTH_SHORT).show();


                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        is_bottom_sheet_expanded = false;
                    }
                });
            }

                TranslateAnimation animate = new TranslateAnimation(ll_container_parent.getWidth(), 0, 0, 0);
                animate.setDuration(400);
                ll_container_parent.startAnimation(animate);

//----------------------------------------------------------------------------
            //   add_press_effect_button_to_dialog_elements(ll_row_container[i] ,iv_[i],tv_of_bookmarked_url[i]);
        }
/*
        ImageButton back_bt =(ImageButton) findViewById(R.id.id_fr_bt_bottom_sheet_back);

        back_bt.setVisibility(View.GONE);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);*/

        //--------------------------handling Bottom sheet header work
        ImageButton bt_for_back = (ImageButton) findViewById(R.id.id_fr_bt_bottom_sheet_back);
        bt_for_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //doing an animation from left to right..but the below animatoin method doesnt actually moves it..

                TranslateAnimation animate = new TranslateAnimation(0, ll_container_parent.getWidth(), 0, 0);
                animate.setDuration(350);
                ll_container_parent.startAnimation(animate);
                animate.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        //Since the animation has ended ,, now doing the actual work
                        make_SUB_CATEGORY_layouts_dynamically_an_add_to_bottom_sheet(0, true);

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
        });

        TextView tv_bottom_sheet_header = (TextView) findViewById(R.id.id_br_bottom_sheet_tv_header);
        tv_bottom_sheet_header.setText("Select a Sub-Sub-Category");


    }


    private void setup_done_button() {
        Button bt_fr_done = (Button) findViewById(R.id.id_fr_bt_upload_data);
        bt_fr_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validate_data();

            }
        });


    }

    private void validate_data() {

        final EditText et_fr_item_name = (EditText) findViewById(R.id.ed_fr_Item_name);
        final EditText et_fr_item_decription = (EditText) findViewById(R.id.ed_fr_item_descrip);
        final EditText et_fr_item_price = (EditText) findViewById(R.id.ed_fr_Item_price);

        String itemName = et_fr_item_name.getText().toString();
        String itemDescription = et_fr_item_decription.getText().toString();
        //  int itemPrice = -1 ;        //default price
        String itemPrice = et_fr_item_price.getText().toString();


        if (itemName.length() == 0 || itemName == null || itemDescription.length() == 0 || itemDescription == null || itemPrice == null || no_of_occupied_imageviews() == 0) {
            Toast.makeText(ItemToSellDetailsActivity.this, "Fill all the feilds first and select atleat one photho of item", Toast.LENGTH_SHORT).show();
        } else if (category_selected_from_Bottomsheet == null) {
            Toast.makeText(ItemToSellDetailsActivity.this, "Selct a ctaegory for your Item first", Toast.LENGTH_SHORT).show();
        } else {

            progressDialog.setTitle("Uploading");
            progressDialog.show();

            //getting id no of the last item so that it can be imncrmented and set for this item details to be uploading
            DocumentReference pg = db.collection("variables").document("id no for items for sale(last item uploaded))");
            //   int finalItemPrice = itemPrice;
            pg.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        //adding the information of the user to the static class
                        //  int Order_id = ( doc.get("id no")).intValue();
                        int Order_id = ((Long) (doc.get("id no"))).intValue();

                        /*   Log.i("######################", String.valueOf(Order_id));*/
                        Order_id++;

                        //since id is retrieved and incremented ..now uploading all the details

                        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                        Map<String, Object> user_node_data = new HashMap<>();
                        user_node_data.put("order id", Order_id);
                        user_node_data.put("seller id", currentFirebaseUser.getUid());
                        user_node_data.put("name", itemName);
                        user_node_data.put("description", itemDescription);
                        user_node_data.put("item price", itemPrice);
                        user_node_data.put("category", category_selected_from_Bottomsheet);
                        user_node_data.put("time of upload", System.currentTimeMillis());
                        user_node_data.put("root category", extract_the_ctgr(category_selected_from_Bottomsheet));
                        user_node_data.put("sub category", extract_the_sub_ctgr(category_selected_from_Bottomsheet));
                        user_node_data.put("sub sub category", extract_the_sub_sub_subctgr(category_selected_from_Bottomsheet));
                        Log.d("&&&&&&&&&&&&", extract_the_ctgr(category_selected_from_Bottomsheet) + "--" + extract_the_sub_ctgr(category_selected_from_Bottomsheet)+"--"+extract_the_sub_sub_subctgr(category_selected_from_Bottomsheet) );

                        int finalOrder_id = Order_id;
                        db.collection("items for sale").document(String.valueOf(Order_id))
                                .set(user_node_data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("TAG", "DocumentSnapshot successfully written!");


                                        uploadFile(finalOrder_id, 0);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("TAG", "Error writing document", e);
                                    }
                                });

                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });


            //-==========================


        }

    }

    private Object extract_the_sub_sub_subctgr(String category_selected_from_bottomsheet) {
        char str[] = category_selected_from_bottomsheet.toCharArray();
        if( category_selected_from_bottomsheet.indexOf("//")==-1){
            return "null";
        }
        int index =   category_selected_from_bottomsheet.indexOf("//") + 2;
        String ctgr= String.valueOf(str[index]);
        for(int i= index+1;i<str.length ;i++){
            ctgr=ctgr+String.valueOf(str[i]);

        }
        return ctgr;
    }

    private Object extract_the_sub_ctgr(String category_selected_from_bottomsheet) {


        char str[] = category_selected_from_bottomsheet.toCharArray();
        if( category_selected_from_bottomsheet.indexOf("/")==-1){
            return "null";
        }
        int index =   category_selected_from_bottomsheet.indexOf("/") + 1;
        String ctgr= String.valueOf(str[index]);
        for(int i= index+1;i<str.length ;i++){
            if(str[i] != '/'){
                ctgr=ctgr+String.valueOf(str[i]);

            }else{
                return ctgr;
            }
        }
        return ctgr;
    }

    private String extract_the_ctgr(String category_selected_from_bottomsheet) {

        char str[] = category_selected_from_bottomsheet.toCharArray();
        String ctgr= String.valueOf(str[0]);
        for(int i= 1;i<str.length ;i++){
            if(str[i] != '/'){
                ctgr=ctgr+String.valueOf(str[i]);
            }else{
                return ctgr;
            }
        }
        return ctgr;

    }


    void update_the_latest_id_no(int Order_id) {
        Map<String, Object> user_node_data = new HashMap<>();
        user_node_data.put("id no", Integer.valueOf(Order_id));


        int finalOrder_id = Order_id;
        db.collection("variables").document("id no for items for sale(last item uploaded))")
                .set(user_node_data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully written!");

                        progressDialog.dismiss();

                        Toast.makeText(ItemToSellDetailsActivity.this, "Item uploaded successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(ItemToSellDetailsActivity.this, seller_items_list.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error writing document", e);
                    }
                });
    }
    //========================================================

    //this method is RECURSIVE function
    private void uploadFile(int finalOrder_id, int i) {

        if (i >= 5) {
            //base condition..block


            //doing the last task  .....that is......uploading the new oreder id
            update_the_latest_id_no(finalOrder_id);
        } else {

            if (image_uri_S[i] != "default") {

                Uri imgUri = Uri.parse("file://" + image_uri_S[i]);
                String time_for_name_of_image = System.currentTimeMillis() + "." + "jpeg";

                StorageReference filepath = mStorageRef.child("items for sale pics folder").child(String.valueOf(finalOrder_id)).child(time_for_name_of_image);


                filepath.putFile(imgUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                //  since the image is uploaded ..now we need its download url and save that to order data ...also download url method is asynchronous so...so dont advance until i get the url .

                                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri downloadUrl) {
                                        //do something with downloadurl
                                        //now uploading the uploadesd image url
                                        Map<String, Object> new_pic = new HashMap<>();
                                        new_pic.put("order no", finalOrder_id);
                                        new_pic.put("url", (downloadUrl.toString()));
                                        new_pic.put("image order",i);

                                        db.collection("items for sale").document(String.valueOf(finalOrder_id)).collection("uploaded images urls").document(time_for_name_of_image)
                                                .set(new_pic)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("TAG", "DocumentSnapshot successfully written!");
                                                        uploadFile(finalOrder_id, i + 1);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("TAG", "Error writing document", e);
                                                    }
                                                });
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                // ...
                            }
                        });
            }
            //if there is not any file
            else {
                uploadFile(finalOrder_id, i + 1);
            }

        }
    }


    //======================================================


    private int no_of_occupied_imageviews() {
        int count = 0;
        for (int i = 0; i < 5; i++) {
            if (array_whether_image_view_have_image[i] == true) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(ItemToSellDetailsActivity.this, Options.init().setRequestCode(100));
                } else {
                    Toast.makeText(ItemToSellDetailsActivity.this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

            ImageView iv[] = new ImageView[5];

            //    for(int i=0 ; i<5;i++) {
            iv[0] = (ImageView) findViewById(R.id.id_fr_slected_image_1);
            iv[1] = (ImageView) findViewById(R.id.id_fr_slected_image_2);
            iv[2] = (ImageView) findViewById(R.id.id_fr_slected_image_3);
            iv[3] = (ImageView) findViewById(R.id.id_fr_slected_image_4);
            iv[4] = (ImageView) findViewById(R.id.id_fr_slected_image_5);
            //  }

            setonclicklisteners_and_code_to_remove_image(iv);

            if (returnValue.isEmpty()) {
                //do nothing
            } else if (returnValue.size() == 1) {

                //  array_whether_image_view_have_image[return_the_available_imageview_index()]=false;
                //Uri mImageUri = Uri.parse(returnValue.get(0));
                iv[return_the_available_imageview_index()].setVisibility(View.VISIBLE);

                Bitmap bmImg = BitmapFactory.decodeFile(returnValue.get(0));
                iv[return_the_available_imageview_index()].setImageBitmap(bmImg);
                image_uri_S[return_the_available_imageview_index()] = returnValue.get(0);
                array_whether_image_view_have_image[return_the_available_imageview_index()] = true;

                Log.i("%%%%%%%%%%%%%%%%", returnValue.get(0));
                       /* // Load the image into image view
                        Glide.with(ItemToSellDetailsActivity.this)
                                //.load(mImageUri) // Load image from assets
                                .load(mImageUri) // Image URL
                                .centerCrop() // Image scale type
                               // .crossFade()
                                .override(140,140) // Resize image
                                .placeholder(R.drawable.ic_launcher_foreground) // Place holder image
                                .error(R.drawable.ic_launcher_foreground) // On error image
                                .into(iv[0]); // ImageView to display image*/

            } else if (returnValue.size() == 2) {
                iv[return_the_available_imageview_index()].setVisibility(View.VISIBLE);
                Bitmap bmImg = BitmapFactory.decodeFile(returnValue.get(0));
                iv[return_the_available_imageview_index()].setImageBitmap(bmImg);
                image_uri_S[return_the_available_imageview_index()] = returnValue.get(0);
                array_whether_image_view_have_image[return_the_available_imageview_index()] = true;


                iv[return_the_available_imageview_index()].setVisibility(View.VISIBLE);
                Bitmap bmImg2 = BitmapFactory.decodeFile(returnValue.get(1));
                iv[return_the_available_imageview_index()].setImageBitmap(bmImg2);
                image_uri_S[return_the_available_imageview_index()] = returnValue.get(1);
                array_whether_image_view_have_image[return_the_available_imageview_index()] = true;


                //  total_no_of_pics_selected=total_no_of_pics_selected+2;
            } else if (returnValue.size() == 3) {
                iv[return_the_available_imageview_index()].setVisibility(View.VISIBLE);
                Bitmap bmImg1 = BitmapFactory.decodeFile(returnValue.get(0));
                iv[return_the_available_imageview_index()].setImageBitmap(bmImg1);
                image_uri_S[return_the_available_imageview_index()] = returnValue.get(0);
                array_whether_image_view_have_image[return_the_available_imageview_index()] = true;


                iv[return_the_available_imageview_index()].setVisibility(View.VISIBLE);
                Bitmap bmImg2 = BitmapFactory.decodeFile(returnValue.get(1));
                iv[return_the_available_imageview_index()].setImageBitmap(bmImg2);
                image_uri_S[return_the_available_imageview_index()] = returnValue.get(1);
                array_whether_image_view_have_image[return_the_available_imageview_index()] = true;


                iv[return_the_available_imageview_index()].setVisibility(View.VISIBLE);
                Bitmap bmImg3 = BitmapFactory.decodeFile(returnValue.get(2));
                iv[return_the_available_imageview_index()].setImageBitmap(bmImg3);
                image_uri_S[return_the_available_imageview_index()] = returnValue.get(2);
                array_whether_image_view_have_image[return_the_available_imageview_index()] = true;

                //   total_no_of_pics_selected=total_no_of_pics_selected+3;
            } else if (returnValue.size() == 4) {
                iv[return_the_available_imageview_index()].setVisibility(View.VISIBLE);
                Bitmap bmImg1 = BitmapFactory.decodeFile(returnValue.get(0));
                iv[return_the_available_imageview_index()].setImageBitmap(bmImg1);
                image_uri_S[return_the_available_imageview_index()] = returnValue.get(0);
                array_whether_image_view_have_image[return_the_available_imageview_index()] = true;


                iv[return_the_available_imageview_index()].setVisibility(View.VISIBLE);
                Bitmap bmImg2 = BitmapFactory.decodeFile(returnValue.get(1));
                iv[return_the_available_imageview_index()].setImageBitmap(bmImg2);
                image_uri_S[return_the_available_imageview_index()] = returnValue.get(1);
                array_whether_image_view_have_image[return_the_available_imageview_index()] = true;


                iv[return_the_available_imageview_index()].setVisibility(View.VISIBLE);
                Bitmap bmImg3 = BitmapFactory.decodeFile(returnValue.get(2));
                iv[return_the_available_imageview_index()].setImageBitmap(bmImg3);
                image_uri_S[return_the_available_imageview_index()] = returnValue.get(2);
                array_whether_image_view_have_image[return_the_available_imageview_index()] = true;


                iv[return_the_available_imageview_index()].setVisibility(View.VISIBLE);
                Bitmap bmImg4 = BitmapFactory.decodeFile(returnValue.get(3));
                iv[return_the_available_imageview_index()].setImageBitmap(bmImg4);
                image_uri_S[return_the_available_imageview_index()] = returnValue.get(3);
                array_whether_image_view_have_image[return_the_available_imageview_index()] = true;


                //   total_no_of_pics_selected=total_no_of_pics_selected+4;
            } else if (returnValue.size() == 5) {
                iv[return_the_available_imageview_index()].setVisibility(View.VISIBLE);
                Bitmap bmImg1 = BitmapFactory.decodeFile(returnValue.get(0));
                iv[return_the_available_imageview_index()].setImageBitmap(bmImg1);
                image_uri_S[return_the_available_imageview_index()] = returnValue.get(0);
                array_whether_image_view_have_image[return_the_available_imageview_index()] = true;


                iv[return_the_available_imageview_index()].setVisibility(View.VISIBLE);
                Bitmap bmImg2 = BitmapFactory.decodeFile(returnValue.get(1));
                iv[return_the_available_imageview_index()].setImageBitmap(bmImg2);
                image_uri_S[return_the_available_imageview_index()] = returnValue.get(1);
                array_whether_image_view_have_image[return_the_available_imageview_index()] = true;


                iv[return_the_available_imageview_index()].setVisibility(View.VISIBLE);
                Bitmap bmImg3 = BitmapFactory.decodeFile(returnValue.get(2));
                iv[return_the_available_imageview_index()].setImageBitmap(bmImg3);
                iv[return_the_available_imageview_index()].setVisibility(View.VISIBLE);
                image_uri_S[return_the_available_imageview_index()] = returnValue.get(2);
                array_whether_image_view_have_image[return_the_available_imageview_index()] = true;


                iv[return_the_available_imageview_index()].setVisibility(View.VISIBLE);
                Bitmap bmImg4 = BitmapFactory.decodeFile(returnValue.get(3));
                iv[return_the_available_imageview_index()].setImageBitmap(bmImg4);
                image_uri_S[return_the_available_imageview_index()] = returnValue.get(3);
                array_whether_image_view_have_image[return_the_available_imageview_index()] = true;


                iv[return_the_available_imageview_index()].setVisibility(View.VISIBLE);
                Bitmap bmImg5 = BitmapFactory.decodeFile(returnValue.get(4));
                iv[return_the_available_imageview_index()].setImageBitmap(bmImg5);
                image_uri_S[return_the_available_imageview_index()] = returnValue.get(4);
                array_whether_image_view_have_image[return_the_available_imageview_index()] = true;


                //  total_no_of_pics_selected=total_no_of_pics_selected+5;
            }
            for (int i = 0; i < 5; i++) {
                Log.e("%%%%%%%%%%%%%%%%", image_uri_S[i]);
            }
            Toast.makeText(ItemToSellDetailsActivity.this, returnValue.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setonclicklisteners_and_code_to_remove_image(ImageView[] iv) {

        for (int i = 0; i < 5; i++) {

            int finalI = i;
            iv[i].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    iv[finalI].setVisibility(View.GONE);
                    array_whether_image_view_have_image[finalI] = false;

                    return false;
                }
            });

        }
    }

    private int return_the_available_imageview_index() {
        int i;
        for (i = 0; i < 5; i++) {
            if (array_whether_image_view_have_image[i] == false) {
                break;
            }
        }
        return i;
    }


    @Override
    public void onBackPressed() {

        if (is_bottom_sheet_expanded == true) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            is_bottom_sheet_expanded = false;
        } else {
//TODo- make a dialog asking user ..is he sure ..he want to exit as ..it could have been prssed by mistake and changes would be lost

            startActivity(new Intent(ItemToSellDetailsActivity.this, seller_items_list.class));

            //just adding an animatiion here whic makes it go with animation sliding to right
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }

    }
}
