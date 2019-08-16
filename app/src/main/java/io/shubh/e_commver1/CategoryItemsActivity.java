package io.shubh.e_commver1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoryItemsActivity extends AppCompatActivity {

    String category;
    TextView tv_header;

    TextView tv_catgr_dierctory;
    TextView tv_sub_catgr_dierctory;
    TextView tv_sub_sub_catgr_dierctory;

    TextView tv_first_slash_directry;
    TextView tv_second_slash_directry;

    List<String> itemID = new ArrayList<String>();


    RecyclerView recyclerView;
    FirebaseFirestore db;

    String TAG = "********";
    int temp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_items);

        db = FirebaseFirestore.getInstance();

        //category = StaticClassForGlobalInfo.categories_names.get(1);
        Intent intent = getIntent();
        category= intent.getStringExtra("name");



        handle_the_category_verticle_bar_and_top_banners();

        get_the_category_data_and_load_into_grid_view(true,false,false ,category);
    }

    private void get_the_category_data_and_load_into_grid_view(Boolean load_only_root_ctgr,Boolean load_sub_ctgr ,boolean load_sub_sub_ctgr ,String value) {

        recyclerView = (RecyclerView) findViewById(R.id.id_fr_recycler_view_ctgr_items_list);
        recyclerView.removeAllViewsInLayout();

        final int[] size_of_items = new int[1];

        String key="root category";
        if(load_only_root_ctgr==true){

            ArrayList<ClassForCategoryItemReclrDATAObject> list_of_data_objects__for_adapter = new ArrayList<>();

//getting all the documents whose have a feild 'seller id' as this seller id
            db.collection("items for sale")
                    .whereEqualTo(key,value ) // <-- This line
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                int i = 0;
                                size_of_items[0] = task.getResult().size();
                                if( size_of_items[0]==0){
                                    Toast.makeText(CategoryItemsActivity.this, "No Items Found", Toast.LENGTH_SHORT).show();
                                }

                                for (DocumentSnapshot document : task.getResult()) {

                                    ClassForCategoryItemReclrDATAObject data_object = new ClassForCategoryItemReclrDATAObject();

                                    Log.d(TAG, document.getId() + " => " + document.getData());

                                    itemID.add(document.getId());

                                    //retriving only these three things for now...later more feilds will be retrived specifically
                                    data_object.setItem_id(document.getId());
                                    data_object.setItem_title((String) document.get("name"));
                                    data_object.setItem_price((String) document.get("item price"));

                                    Log.d("&&&&&&&&&&1", (String) document.get("description"));
                                    // Log.d("&&&&&&&&&&2",data_object.getItem_Descrp());

                                    //adding dummy data into this position in master list...later this will be updated with correct data
                                    list_of_data_objects__for_adapter.add(i, new ClassForCategoryItemReclrDATAObject());

                                    //     this below line is amibigiious because function called willr un on sepatrate thread ,,thus I will Add urlt to below list inside the called function when data is retrived completely
                                    //    data_object.setItem_image_url((retrive_all_the_item_image_url_in_a_list(document.getId()));

                                    retrive_all_the_item_image_url_in_a_list(document.getId(), i, data_object, size_of_items[0] , list_of_data_objects__for_adapter);

                                    i++;
                                }


//TODO- I should also order the item list as per the order no(as they are in increasing order as per their timing).......Do later


                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });


        }else if(load_sub_ctgr ==true){
           String key2="sub category";

            ArrayList<ClassForCategoryItemReclrDATAObject> list_of_data_objects__for_adapter = new ArrayList<>();

//getting all the documents whose have a feild 'seller id' as this seller id
            db.collection("items for sale")
                    .whereEqualTo(key,category )
                    .whereEqualTo(key2 ,value)// <-- This line
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                int i = 0;
                                size_of_items[0] = task.getResult().size();
                                if( size_of_items[0]==0){
                                    Toast.makeText(CategoryItemsActivity.this, "No Items Found", Toast.LENGTH_SHORT).show();
                                }

                                for (DocumentSnapshot document : task.getResult()) {

                                    ClassForCategoryItemReclrDATAObject data_object = new ClassForCategoryItemReclrDATAObject();

                                    Log.d(TAG, document.getId() + " => " + document.getData());

                                    itemID.add(document.getId());

                                    //retriving only these three things for now...later more feilds will be retrived specifically
                                    data_object.setItem_id(document.getId());
                                    data_object.setItem_title((String) document.get("name"));
                                    data_object.setItem_price((String) document.get("item price"));

                                    Log.d("&&&&&&&&&&1", (String) document.get("description"));
                                    // Log.d("&&&&&&&&&&2",data_object.getItem_Descrp());

                                    //adding dummy data into this position in master list...later this will be updated with correct data
                                    list_of_data_objects__for_adapter.add(i, new ClassForCategoryItemReclrDATAObject());

                                    //     this below line is amibigiious because function called willr un on sepatrate thread ,,thus I will Add urlt to below list inside the called function when data is retrived completely
                                    //    data_object.setItem_image_url((retrive_all_the_item_image_url_in_a_list(document.getId()));

                                    retrive_all_the_item_image_url_in_a_list(document.getId(), i, data_object, size_of_items[0] , list_of_data_objects__for_adapter);

                                    i++;
                                }


//TODO- I should also order the item list as per the order no(as they are in increasing order as per their timing).......Do later


                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });


        }else{
            String key2="sub sub category";

            ArrayList<ClassForCategoryItemReclrDATAObject> list_of_data_objects__for_adapter = new ArrayList<>();

//getting all the documents whose have a feild 'seller id' as this seller id
            db.collection("items for sale")
                    .whereEqualTo(key,category )
                    .whereEqualTo(key2 ,value)// <-- This line
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                int i = 0;
                                size_of_items[0] = task.getResult().size();
                                if( size_of_items[0]==0){
                                    Toast.makeText(CategoryItemsActivity.this, "No Items Found", Toast.LENGTH_SHORT).show();
                                }

                                for (DocumentSnapshot document : task.getResult()) {

                                    ClassForCategoryItemReclrDATAObject data_object = new ClassForCategoryItemReclrDATAObject();

                                    Log.d(TAG, document.getId() + " => " + document.getData());

                                    itemID.add(document.getId());

                                    //retriving only these three things for now...later more feilds will be retrived specifically
                                    data_object.setItem_id(document.getId());
                                    data_object.setItem_title((String) document.get("name"));
                                    data_object.setItem_price((String) document.get("item price"));

                                    Log.d("&&&&&&&&&&1", (String) document.get("description"));
                                    // Log.d("&&&&&&&&&&2",data_object.getItem_Descrp());

                                    //adding dummy data into this position in master list...later this will be updated with correct data
                                    list_of_data_objects__for_adapter.add(i, new ClassForCategoryItemReclrDATAObject());

                                    //     this below line is amibigiious because function called willr un on sepatrate thread ,,thus I will Add urlt to below list inside the called function when data is retrived completely
                                    //    data_object.setItem_image_url((retrive_all_the_item_image_url_in_a_list(document.getId()));

                                    retrive_all_the_item_image_url_in_a_list(document.getId(), i, data_object, size_of_items[0] , list_of_data_objects__for_adapter);

                                    i++;
                                }


//TODO- I should also order the item list as per the order no(as they are in increasing order as per their timing).......Do later


                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });

        }



    }

    private void retrive_all_the_item_image_url_in_a_list(String document_id, int index_for_list, ClassForCategoryItemReclrDATAObject data_object, int size_of_items, ArrayList<ClassForCategoryItemReclrDATAObject> list_of_data_objects__for_adapter) {

        //HERE I AM RETRIVNG ALL THE IMAGES

        //getting all the documents whose have a feild 'item id' as this item's called from

        final ArrayList<String>[] images_URLs = new ArrayList[]{new ArrayList<>()};
        ArrayList<Integer> imagesorder = new ArrayList<>();

        db.collection("items for sale").document(document_id).collection("uploaded images urls")
                .whereEqualTo("order no", Integer.valueOf(document_id)) // <-- This line
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                images_URLs[0].add((String) document.get("url"));
                                imagesorder.add(((Long) document.get("order no")).intValue());
                            }


                            //TODO-sort the Image url according to their order here
                            images_URLs[0] = sort_the_url_into_order(images_URLs[0], imagesorder);

                            //now adding the first Image url
                            data_object.setItem_image_url(images_URLs[0].get(0));

                            data_object.setItem_all_images_list(images_URLs);

                            //all the work related to this specific item's data object is complete adding it to final list at correct place
                            list_of_data_objects__for_adapter.set(index_for_list, data_object);

                            //if this index is 1 less then the total size
                            //that will mean that this is the last thread ans
                            //now all the data is retrived Completely thus calling for setting up the list
                            if (index_for_list == size_of_items - 1) {
                                set_up_the_recycler_grid_view(list_of_data_objects__for_adapter);

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void set_up_the_recycler_grid_view(ArrayList<ClassForCategoryItemReclrDATAObject> list_of_data_objects__for_adapter) {
        //now executing the UI part
        recyclerView = (RecyclerView) findViewById(R.id.id_fr_recycler_view_ctgr_items_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.removeAllViews();

      /*  RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CategoryItemsActivity.this);
        recyclerView.setLayoutManager(layoutManager);*/

        // set a GridLayoutManager with 2 number of columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL); // set Horizontal Orientation
        recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView

        //data_list_for_adapter = list_of_data_objects__for_adapter;
        reclr_adapter_class_for_ctgr_items adapter = new reclr_adapter_class_for_ctgr_items(CategoryItemsActivity.this, list_of_data_objects__for_adapter);
        recyclerView.setAdapter(adapter);


        if (list_of_data_objects__for_adapter.size() == 0) {
            Toast.makeText(this, "No Items found", Toast.LENGTH_SHORT).show();
        }

    }

    private ArrayList<String> sort_the_url_into_order(ArrayList<String> images_url, ArrayList<Integer> imagesorder) {
        int i, key, j;
        String key2;
        for (i = 1; i < imagesorder.size(); i++) {
            key = imagesorder.get(i);
            key2 = images_url.get(i);
            j = i - 1;

        /* Move elements of arr[0..i-1], that are
        greater than key, to one position ahead
        of their current position */
            while (j >= 0 && imagesorder.get(j) > key) {
                imagesorder.set(j + 1, imagesorder.get(j));
                images_url.set(j + 1, images_url.get(j));
                j = j - 1;
            }
            imagesorder.set(j + 1, key);
            images_url.set(j + 1, key2);
        }
        return images_url;
    }


    private void handle_the_category_verticle_bar_and_top_banners() {

        //  container_for_directory_tvs =(LinearLayout)findViewById(R.id.id_fr_ll_container_fr_categories);
        tv_catgr_dierctory = (TextView) findViewById(R.id.id_fr_tv_catgr_directory);
        tv_sub_catgr_dierctory = (TextView) findViewById(R.id.id_fr_tv_sub_catgr_directory);
        tv_sub_sub_catgr_dierctory = (TextView) findViewById(R.id.id_fr_tv_sub_sub_catgr_directory);
        tv_first_slash_directry = (TextView) findViewById(R.id.id_fr_tv_first_slash);
        tv_second_slash_directry = (TextView) findViewById(R.id.id_fr_tv_scnd_slash);

        tv_catgr_dierctory.setPaintFlags(tv_sub_catgr_dierctory.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); //making tv underlined
        tv_sub_catgr_dierctory.setVisibility(View.GONE);
        tv_sub_catgr_dierctory.setPaintFlags(tv_sub_catgr_dierctory.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); //making tv underlined
        tv_sub_sub_catgr_dierctory.setVisibility(View.GONE);
        tv_sub_sub_catgr_dierctory.setPaintFlags(tv_sub_catgr_dierctory.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); //making tv underlined
        tv_first_slash_directry.setVisibility(View.GONE);
        tv_second_slash_directry.setVisibility(View.GONE);

        tv_header = (TextView) findViewById(R.id.id_fr_tv_header);
        tv_header.setText("Category : " + category);
        tv_catgr_dierctory.setText(category);

        int i =StaticClassForGlobalInfo.categories_names.indexOf(category);

        tv_catgr_dierctory.setBackgroundResource(R.drawable.ripple_effect_green_on_dynamicallly_craeted_views_of_ctgr_actviyt);
        tv_catgr_dierctory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                get_the_category_data_and_load_into_grid_view(true,false,false,category);
                make_SUB_CATEGORY_layouts_dynamically_an_add_to_bottom_sheet(i, false);

                tv_sub_catgr_dierctory.setVisibility(View.GONE);
                tv_sub_sub_catgr_dierctory.setVisibility(View.GONE);
                tv_first_slash_directry.setVisibility(View.GONE);
                tv_second_slash_directry.setVisibility(View.GONE);

                tv_header.setText("Category : " + category);
            }
        });

        tv_sub_catgr_dierctory.setBackgroundResource(R.drawable.ripple_effect_green_on_dynamicallly_craeted_views_of_ctgr_actviyt);
        tv_sub_catgr_dierctory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_the_category_data_and_load_into_grid_view(false,true,false, String.valueOf(tv_sub_catgr_dierctory.getText()));

                tv_sub_sub_catgr_dierctory.setVisibility(View.GONE);
                tv_second_slash_directry.setVisibility(View.GONE);

                tv_header.setText("Sub-Category : " + tv_sub_catgr_dierctory.getText());
            }
        });

        tv_sub_sub_catgr_dierctory.setBackgroundResource(R.drawable.ripple_effect_green_on_dynamicallly_craeted_views_of_ctgr_actviyt);
        tv_sub_sub_catgr_dierctory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_the_category_data_and_load_into_grid_view(false,false,true, String.valueOf(tv_sub_sub_catgr_dierctory.getText()));
            }
        });


        make_SUB_CATEGORY_layouts_dynamically_an_add_to_bottom_sheet(i, false);
    }

    private void make_SUB_CATEGORY_layouts_dynamically_an_add_to_bottom_sheet(int index_root, Boolean is_it_called_from_sub_sub_catgr_fun) {

        int sub_categories_size = StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).size();

        //now listing alll the bookmarked url in the list//but mkiang view for them programmattically
        final TextView[] tv_of_sub_category = new TextView[sub_categories_size];
        //   final ImageView[] iv = new ImageView[sub_categories_size];
        final RelativeLayout[] rl_row_container_sub_categories = new RelativeLayout[sub_categories_size];

        final LinearLayout ll_container_parent = (LinearLayout) findViewById(R.id.container_fr_ctgr);
        //clearing all the previous textvies with animation
        ll_container_parent.removeAllViews();


        final float scale = getResources().getDisplayMetrics().density;

        final RelativeLayout.LayoutParams lp_for_rl_row = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(int) (50 * scale + 0.5f));
             lp_for_rl_row.setMargins((int) (0 * scale + 0.5f), (int) (0 * scale + 0.5f), (int) (0 * scale + 0.5f), (int) (20 * scale + 0.5f));


        final RelativeLayout.LayoutParams lp_for_tv = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp_for_rl_row.addRule(RelativeLayout.CENTER_VERTICAL);

      /*  final RelativeLayout.LayoutParams lp_for_iv = new RelativeLayout.LayoutParams((int) (25 * scale + 0.5f), (int) (25 * scale + 0.5f));
        lp_for_iv.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);*/

        for (int i = 0; i < sub_categories_size; i++) {

            rl_row_container_sub_categories[i] = new RelativeLayout(this);
            tv_of_sub_category[i] = new TextView(this);
            //        iv[i] = new ImageView(this);

            rl_row_container_sub_categories[i].setLayoutParams(lp_for_rl_row);
            //   rl_row_container_sub_categories[i].setBackgroundResource(R.drawable.ripple_effect_white_on_dynamicallly_craeted_views_of_bottom_sheet);

            // ll_row_container[i].setPadding((int) (3 * scale + 0.5f),(int) (3 * scale + 0.5f),(int) (3 * scale + 0.5f),(int) (3 * scale + 0.5f));
            // rl_row_container_sub_categories[i].setOrientation(LinearLayout.HORIZONTAL);
            rl_row_container_sub_categories[i].setBackgroundResource(R.drawable.ripple_effect_green_on_dynamicallly_craeted_views_of_ctgr_actviyt);


            //  tv_of_bookmarked_url[i].setLayoutParams(lp_for_tv_for_url);
            tv_of_sub_category[i].setLayoutParams(lp_for_tv);
            tv_of_sub_category[i].setText(StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(i).categoryNmae);
            tv_of_sub_category[i].setTextSize((int) (17));
            tv_of_sub_category[i].setTextColor(Color.parseColor("#A60C0C0C"));
            tv_of_sub_category[i].setMaxLines(2);


            //  iv[i].setLayoutParams(lp_for_iv);
            // iv[i].setPadding((int) (1.5 * scale + 0.5f), (int) (1.5 * scale + 0.5f), (int) (1.5 * scale + 0.5f), (int) (1.5 * scale + 0.5f));

            rl_row_container_sub_categories[i].addView(tv_of_sub_category[i]);
            //     rl_row_container_sub_categories[i].addView(iv[i]);
            ll_container_parent.addView(rl_row_container_sub_categories[i]);


            int finalI = i;
            if (StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(i).getListOfsubCategory() != null) {

                //     iv[i].setImageResource(R.drawable.forward);
                tv_of_sub_category[finalI].setPaintFlags(tv_sub_catgr_dierctory.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); //making tv underlined

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


                        TranslateAnimation animate2 = new TranslateAnimation(0, 0, 0, -ll_container_parent.getHeight());
                        animate2.setDuration(500);
                        tv_header.startAnimation(animate2);

                        //doing header work
                        tv_header.setText("Sub-Category : " + StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(finalI).categoryNmae);
                        tv_first_slash_directry.setVisibility(View.VISIBLE);
                        tv_sub_catgr_dierctory.setVisibility(View.VISIBLE);
                        tv_sub_catgr_dierctory.setText(StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(finalI).categoryNmae);

                        get_the_category_data_and_load_into_grid_view(false,true,false,StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(finalI).categoryNmae);

                        tv_sub_sub_catgr_dierctory.setVisibility(View.GONE);
                        tv_second_slash_directry.setVisibility(View.GONE);
                    }
                });
            } else {
                //       iv[finalI].setImageResource(R.drawable.radio_bt);
                rl_row_container_sub_categories[i].setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {

                        String category_selected_from_Bottomsheet = StaticClassForGlobalInfo.categories_names.get(index_root) + "/" + StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(finalI).categoryNmae;
                        //     Toast.makeText(ItemToSellDetailsActivity.this, StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(finalI).categoryNmae + " is selected as category", Toast.LENGTH_SHORT).show();
                        Toast.makeText(CategoryItemsActivity.this, category_selected_from_Bottomsheet, Toast.LENGTH_SHORT).show();

                        //doing header work
                        tv_header.setText("Sub-Category : " + StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(finalI).categoryNmae);
                        tv_first_slash_directry.setVisibility(View.VISIBLE);
                        tv_sub_catgr_dierctory.setVisibility(View.VISIBLE);
                        tv_sub_catgr_dierctory.setText(StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(finalI).categoryNmae);

                        tv_sub_sub_catgr_dierctory.setVisibility(View.GONE);
                        tv_second_slash_directry.setVisibility(View.GONE);



                        get_the_category_data_and_load_into_grid_view(false,true,false,StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(finalI).categoryNmae);
                      /*  rl_row_container_sub_categories[finalI].setBackgroundColor(Color.parseColor("#A88BC34A"));
                        //removing the green backgrnd clolr if anyone other had
                        for(int j=0;j<sub_categories_size;j++){
                            if(j!=finalI){
                                rl_row_container_sub_categories[finalI].setBackgroundColor(Color.parseColor("#ffffff"));

                            }
                        }*/

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

  /*      //--------------------------handling Bottom sheet header work
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
        });*/


        //adding a godamn animation to tv as well

        TranslateAnimation animate2 = new TranslateAnimation(0, 0, ll_container_parent.getHeight(), 0);
        animate2.setDuration(500);
        tv_header.startAnimation(animate2);
    }

    private void make_SUB_SUB_CATEGORY_layouts_dynamically_an_add_to_bottom_sheet(int index_root, int index_root_sub) {
        int sub_sub_categories_size = StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(index_root_sub).getListOfsubCategory().size();

        //now listing alll the bookmarked url in the list//but mkiang view for them programmattically
        final TextView[] tv_of_sub_sub_category = new TextView[sub_sub_categories_size];
        //  final ImageView[] iv = new ImageView[sub_sub_categories_size];
        final RelativeLayout[] rl_row_container_sub_sub_categories = new RelativeLayout[sub_sub_categories_size];

        final LinearLayout ll_container_parent = (LinearLayout) findViewById(R.id.container_fr_ctgr);
        //clearing all the previous textvies
        ll_container_parent.removeAllViews();

        final float scale = getResources().getDisplayMetrics().density;

        final RelativeLayout.LayoutParams lp_for_rl_row = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (50 * scale + 0.5f));
          lp_for_rl_row.setMargins((int) (0 * scale + 0.5f), (int) (0 * scale + 0.5f), (int) (0 * scale + 0.5f), (int) (20 * scale + 0.5f));

        final RelativeLayout.LayoutParams lp_for_tv = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp_for_rl_row.addRule(RelativeLayout.CENTER_IN_PARENT);

      /*  final RelativeLayout.LayoutParams lp_for_iv = new RelativeLayout.LayoutParams((int) (25 * scale + 0.5f), (int) (25 * scale + 0.5f));
        lp_for_iv.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);*/

        for (int i = 0; i < sub_sub_categories_size; i++) {

            rl_row_container_sub_sub_categories[i] = new RelativeLayout(this);
            tv_of_sub_sub_category[i] = new TextView(this);
            //iv[i] = new ImageView(this);

            rl_row_container_sub_sub_categories[i].setLayoutParams(lp_for_rl_row);
            //  rl_row_container_sub_sub_categories[i].setBackgroundResource(R.drawable.ripple_effect_white_on_dynamicallly_craeted_views_of_bottom_sheet);

            // ll_row_container[i].setPadding((int) (3 * scale + 0.5f),(int) (3 * scale + 0.5f),(int) (3 * scale + 0.5f),(int) (3 * scale + 0.5f));
            // rl_row_container_sub_sub_categories[i].setOrientation(LinearLayout.HORIZONTAL);
            rl_row_container_sub_sub_categories[i].setBackgroundResource(R.drawable.ripple_effect_green_on_dynamicallly_craeted_views_of_ctgr_actviyt);


            //  tv_of_bookmarked_url[i].setLayoutParams(lp_for_tv_for_url);
            tv_of_sub_sub_category[i].setLayoutParams(lp_for_tv);
            tv_of_sub_sub_category[i].setText(StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(index_root_sub).getListOfsubCategory().get(i));
            tv_of_sub_sub_category[i].setTextSize((int) (17));
            tv_of_sub_sub_category[i].setTextColor(Color.parseColor("#A60C0C0C"));
            tv_of_sub_sub_category[i].setMaxLines(2);

            //  iv[i].setLayoutParams(lp_for_iv);
            //  iv[i].setPadding((int) (1.5 * scale + 0.5f), (int) (1.5 * scale + 0.5f), (int) (1.5 * scale + 0.5f), (int) (1.5 * scale + 0.5f));

            rl_row_container_sub_sub_categories[i].addView(tv_of_sub_sub_category[i]);
            //   rl_row_container_sub_sub_categories[i].addView(iv[i]);
            ll_container_parent.addView(rl_row_container_sub_sub_categories[i]);


            int finalI = i;
            if (StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(index_root_sub).getListOfsubCategory() != null) {

                int finalI1 = i;
                rl_row_container_sub_sub_categories[i].setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {

                        String category_selected_from_Bottomsheet = StaticClassForGlobalInfo.categories_names.get(index_root) + "/" + StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(index_root_sub).categoryNmae + "//" + StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(index_root_sub).getListOfsubCategory().get(finalI1);
//                        Toast.makeText(ItemToSellDetailsActivity.this,  StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(index_root_sub).getListOfsubCategory().get(finalI1) + " is selected as category", Toast.LENGTH_SHORT).show();
                        Toast.makeText(CategoryItemsActivity.this, category_selected_from_Bottomsheet, Toast.LENGTH_SHORT).show();

                        //doing header work
                        tv_header.setText("Sub-Sub-Category : " + StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(index_root_sub).getListOfsubCategory().get(finalI));
                        tv_second_slash_directry.setVisibility(View.VISIBLE);
                        tv_sub_sub_catgr_dierctory.setVisibility(View.VISIBLE);
                        tv_sub_sub_catgr_dierctory.setText(StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(index_root_sub).getListOfsubCategory().get(finalI));


                        get_the_category_data_and_load_into_grid_view(false,false,true,StaticClassForGlobalInfo.super_nested_list_of_categories.get(index_root).get(index_root_sub).getListOfsubCategory().get(finalI));
                      /*  tv_of_sub_sub_category[finalI].setTextColor(Color.parseColor("#8BC34A"));
                        //removing the green backgrnd clolr if anyone other had
                        for(int j=0;j<sub_sub_categories_size;j++){
                            if(j!=finalI){
                                tv_of_sub_sub_category[finalI].setTextColor(Color.parseColor("#A60C0C0C"));

                            }
                        }*/
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

     /*   //--------------------------handling Bottom sheet header work
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
        });*/
    }
}
