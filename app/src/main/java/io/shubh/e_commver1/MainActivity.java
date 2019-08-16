package io.shubh.e_commver1;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import 	androidx.appcompat.app.ActionBarDrawerToggle;
import 	androidx.appcompat.widget.SearchView;
import 	androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db;
//Boolean is_nav_drawer_open =false;

//below 2 var for on back pressed
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    List<String> categories_names;
    List<List<CategoriesObjectClass>> super_nested_list_of_categories;
    List<String> category_order;

    RecyclerView recyclerView;


    ProgressBar progressBar;
    ArrayList<ClassForMainActvityItemReclrDATAObject> list_of_data_objects__for_adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        progressBar =(ProgressBar)findViewById(R.id.id_fr_prggrs_bar_main_activity);

        list_of_data_objects__for_adapter = new ArrayList<>();

        navigationDrawerSetUp();

        //setNameAndEmailInNavdrawer();



setSearchViewWork();

/*//system key color change
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#cccccc"));
        }*/

        progressBar.setVisibility(View.VISIBLE);
get_all_categories_data_from_firebase_and_store_it_in_static_global_info_class();



    }

    private void get_all_categories_data_from_firebase_and_store_it_in_static_global_info_class() {
        categories_names = new ArrayList<String>();
        category_order = new ArrayList<String>();

        //below List have each node as a string(master category name ) + a lsit (a nested list of same type --each node has a sub category name String + and a list of string(that will be for sub sub category names))
        //the detailed explaination of this code is inside CategoriesObjectClass class
        super_nested_list_of_categories = new ArrayList<List<CategoriesObjectClass>>();


        //retriving data from firebase and putting it in list
        db.collection("categories for Items for sale").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        categories_names.add(document.getId());

                    }

                    get_the_sub_category_data(categories_names, 0);


                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });


    }


    private void get_the_sub_category_data(List<String> categories_names, int index) {

        db.collection("categories for Items for sale").document(categories_names.get(index)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();

                    //first doing this recycler view task here as well
                    ClassForMainActvityItemReclrDATAObject data_object = new ClassForMainActvityItemReclrDATAObject();
                    data_object.setItem_title(categories_names.get(index));
                    data_object.setItem_image_url((String) doc.get("image url"));
                    list_of_data_objects__for_adapter.add(data_object);



                    List<String> sub_category_names;
                    if (doc.get("sub categories") != null) {
                        sub_category_names = (List<String>) doc.get("sub categories");


                    } else {
                        sub_category_names = null;
                    }
                     category_order.add((String) doc.get("order"));

                    ArrayList<CategoriesObjectClass> sub_cat = new ArrayList<CategoriesObjectClass>();

                    if (sub_category_names != null) {
                        get_the_sub_sub_category_data(categories_names, sub_category_names, 0, index, sub_cat);
                    } else {

                        //putting null at that position
                        CategoriesObjectClass nested_object = new CategoriesObjectClass();
                        sub_cat.add(nested_object);
                        super_nested_list_of_categories.add(sub_cat);


                        if (index < categories_names.size()) {
                            get_the_sub_category_data(categories_names, index + 1);

                        }
                    }


                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });

    }

    private void get_the_sub_sub_category_data(List<String> categories_names, List<String> sub_category_names, int index, int index_of_parent_function, ArrayList<CategoriesObjectClass> sub_cat) {

    //    Log.w(TAG, categories_names.get(index_of_parent_function));

        db.collection("categories for Items for sale").document(categories_names.get(index_of_parent_function)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    //    Log.w(TAG,sub_category_names.get(index) );


                    List<String> sub_sub_category_names = (List<String>) doc.get(sub_category_names.get(index));

                    //  if(sub_sub_category_names !=null) {
                    //  Log.w(TAG, sub_sub_category_names.get(index));

                    CategoriesObjectClass nested_object = new CategoriesObjectClass();
                    nested_object.setCategoryNmae(sub_category_names.get(index));
                    nested_object.setListOfsubCategory((ArrayList<String>) sub_sub_category_names);


                    sub_cat.add(nested_object);

                    //   }


                    if (index + 1 < sub_category_names.size()) {
                        get_the_sub_sub_category_data(categories_names, sub_category_names, index + 1, index_of_parent_function, sub_cat);
                    } else {
                        super_nested_list_of_categories.add(sub_cat);

                        if (index_of_parent_function + 1 < categories_names.size()) {
                            get_the_sub_category_data(categories_names, index_of_parent_function + 1);

                        } else {

                            //At this point data is retrived completely and stored structurally just the sorting of it is remaining
//the category series need to be in order as given in their feilds so doing it in below function

                            //now the data is retrived .stop the progressview
                            progressBar.setVisibility(View.GONE);
                            sortTheCategoryData();
                        }

                    }

                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void sortTheCategoryData() {

        Log.d("%%%%%%%%%%", list_of_data_objects__for_adapter.toString());

        List<Integer>  order_list_in_int = new ArrayList<>();

        //coversion of string list to int list
        for(String s : category_order) order_list_in_int.add(Integer.valueOf(s));

        //sorting  Category data  accordind to order
            int i, temp, j;
        String tempString ;
        List<CategoriesObjectClass> objectTemp;
        ClassForMainActvityItemReclrDATAObject temp_adapter_item;


            for (i = 1; i < order_list_in_int.size(); i++)
            {
                temp = order_list_in_int.get(i);
                tempString = categories_names.get(i);
                objectTemp = super_nested_list_of_categories.get(i);
                temp_adapter_item=list_of_data_objects__for_adapter.get(i);
                j = i - 1;

            /* Move elements of arr[0..i-1], that are
            greater than key, to one position ahead
            of their current position */
                while (j >= 0 && order_list_in_int.get(j) > temp)
                {
                    order_list_in_int.set(j+1 , order_list_in_int.get(j) );
                categories_names.set(j+1 ,  categories_names.get(j ));
                 super_nested_list_of_categories.set(j+1 , super_nested_list_of_categories.get(j));
                    list_of_data_objects__for_adapter.set(j+1 , list_of_data_objects__for_adapter.get(j));

                    j = j - 1;
                }
                order_list_in_int.set(j+1 , temp);
                categories_names.set(j+1 , tempString);
                super_nested_list_of_categories.set(j+1 , objectTemp);
                list_of_data_objects__for_adapter.set(j+1 , temp_adapter_item);
            }


//data is sorted now
//so storing it for global purpose or fututre use
            StaticClassForGlobalInfo.super_nested_list_of_categories =super_nested_list_of_categories ;
        StaticClassForGlobalInfo.categories_names =categories_names ;

        //now the data is retrived we will do the ui work

            loadtheUI();


            //

      //  method_to_print_the_state_of_cateogory_data_in_console_for_debugging_purpose();
    }

    private void loadtheUI() {

        set_nav_dr_button_setup();

        LinearLayout ll_container =(LinearLayout)findViewById(R.id.id_fr_ll_container_fr_categories);
        ll_container.setVisibility(View.VISIBLE);

        //load the categories on main pageusing grid recycler view later...for now I am harcoring UI work

set_up_the_recycler_grid_view(list_of_data_objects__for_adapter);
    }

    private void set_up_the_recycler_grid_view(ArrayList<ClassForMainActvityItemReclrDATAObject> list_of_data_objects__for_adapter) {
        //now executing the UI part
        recyclerView = (RecyclerView) findViewById(R.id.id_fr_recycler_view_main_activity_items_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.removeAllViews();

      /*  RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CategoryItemsActivity.this);
        recyclerView.setLayoutManager(layoutManager);*/

        // set a GridLayoutManager with 2 number of columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL); // set Horizontal Orientation
        recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView

        //data_list_for_adapter = list_of_data_objects__for_adapter;
        reclr_adapter_class_for_main_activity_items adapter = new reclr_adapter_class_for_main_activity_items(MainActivity.this, list_of_data_objects__for_adapter);
        recyclerView.setAdapter(adapter);


        if (list_of_data_objects__for_adapter.size() == 0) {
            Toast.makeText(this, "No Items found", Toast.LENGTH_SHORT).show();
        }


    }

    private void method_to_print_the_state_of_cateogory_data_in_console_for_debugging_purpose() {
        for (int i = 0; i < categories_names.size(); i++) {
            Log.w("&&&&&&&&&&&&&&", categories_names.get(i));
            for (int j = 0; j < super_nested_list_of_categories.get(i).size(); j++) {
                if (super_nested_list_of_categories.get(i).get(j).getCategoryNmae() != null && super_nested_list_of_categories.get(i).get(j).getListOfsubCategory() != null ) {
                    Log.w("&&&&&&&&&&&&&&", super_nested_list_of_categories.get(i).get(j).getCategoryNmae());

                 //   for (int k = 0; k < super_nested_list_of_categories.get(i).get(j).getListOfsubCategory().size(); k++) {

                        if (super_nested_list_of_categories.get(i).get(j).getListOfsubCategory().toString() != null) {
                            Log.w("&&&&&&&&&&&&&&", super_nested_list_of_categories.get(i).get(j).getListOfsubCategory().toString());
                        } else {
                            Log.w("&&&&&&&&&&&&&&", "null");

                        }
                   // }

                } else {
                    Log.w("&&&&&&&&&&&&&&", "null");
                }

            }
        }
    }


    private void setSearchViewWork() {

        SearchView searchView = (SearchView)findViewById(R.id.searchview);


      /*  String suggestWord = intent.getDataString();
        searchView.clearFocus()*/;
      // searchView.setQueryHint("Search for product");




    }

    private void set_nav_dr_button_setup() {

        LinearLayout myProfileButton = (LinearLayout) findViewById(R.id.id_fr_nav_bt_profile);
        myProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //do nothing
            }
        });
//=----------------------------------------

        LinearLayout switchToSellerbutton = (LinearLayout) findViewById(R.id.id_fr_nav_bt_switch);
        switchToSellerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StaticClassForGlobalInfo.isLoggedIn == true){
                    ProgressDialog progressDialog;
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("Wait a sec please");
                progressDialog.show();

                //checking if the user is becoming the buyer for the first time
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                DocumentReference pg = db.collection("users").document(currentFirebaseUser.getUid());
                pg.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            Boolean is_user_a_seller = (Boolean) doc.get("is a seller also ?");

                            if (is_user_a_seller == true) {
                                progressDialog.dismiss();
                                Intent in = new Intent(MainActivity.this, seller_items_list.class);
                                startActivity(in);

                                //animation for sliding activity
                                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                            } else {
                                progressDialog.dismiss();
                                Intent in = new Intent(MainActivity.this, seller_confirmation_activity.class);
                                startActivity(in);

                                //animation for sliding activity
                                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                            }
                        }
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
            }else {
                    Toast.makeText(MainActivity.this, "Not Logged In ...make a dialog appear later having 2 buttons ,one for login and other as 'cancel'", Toast.LENGTH_SHORT).show();
                }
            }
        });
//------------------------------------
//doinf a bit changes in ui first ..setting logou icon if user is logged in and vice versa

        TextView tv_fr_button_signin_in_navdr = (TextView)findViewById(R.id.id_fr_tv_nav_login_);
        ImageView iv_fr_icon_button_signin_in_navdr = (ImageView) findViewById(R.id.id_fr_iv_nav_login_icon);
        LinearLayout Login_or_logout_button = (LinearLayout) findViewById(R.id.id_fr_nav_bt_login_or_logout);

        if(StaticClassForGlobalInfo.isLoggedIn ==true){
            tv_fr_button_signin_in_navdr.setText("Logout");
            iv_fr_icon_button_signin_in_navdr.setImageResource(R.drawable.logout_icon_);
        }else{
            //by default both have login text and image ..so no changes
        }
        //doing some ui work now....the default height for this button linearlayout is different from others button because ll here is depending on child's heighth  and its icon aint android studio rather imported from web so just copying the height of prev button nad setting to it
      //  iv_fr_icon_button_signin_in_navdr.setLayoutParams( new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,switchToSellerbutton.getHeight()));

         //doing actual backend work
        Login_or_logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(StaticClassForGlobalInfo.isLoggedIn ==true){
                        //make user sign out and redirect to welcome screen in case he wants to switch id

                    FirebaseAuth.getInstance().signOut();

                    startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                    finish();

                    //animation for sliding activity
                    overridePendingTransition(R.anim.right_out, R.anim.left_in);
                }else{
                    startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                    finish();

                    overridePendingTransition(R.anim.right_out, R.anim.left_in);
                }

            }
        });

    }

    private void navigationDrawerSetUp() {



        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        final LinearLayout content = (LinearLayout) findViewById(R.id.content);



        //removing the feature where the main content of activity gets dark as the drawer opoens
        drawerLayout.setScrimColor(Color.TRANSPARENT);

        //this beolw ondrawer slide lets us push the main content when the drawer opens ...
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float slideX = drawerView.getWidth() * slideOffset;
                content.setTranslationX(slideX);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

//-----------------------------
       ImageButton menu =(ImageButton)findViewById(R.id.id_fr_menu_bt);
       menu.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               //below logic is commentisized because one the drawer is open ..then any touch outside of drwer will closes the drawer ...
              /* if(is_nav_drawer_open == false){
                   is_nav_drawer_open=true;
                   drawerLayout.openDrawer(Gravity.LEFT);

               }else if(is_nav_drawer_open == true){
                   is_nav_drawer_open=false;
                   drawerLayout.closeDrawer(Gravity.LEFT);

               }*/
               drawerLayout.openDrawer(Gravity.LEFT);

           }
       });


//Setting up the name ,email in drawer if user is not guest user.......do profile pic later

        if(StaticClassForGlobalInfo.isLoggedIn !=false) {
            TextView tv_name = (TextView) findViewById(R.id.tv_for_nav_dr_name);
            TextView tv_email = (TextView) findViewById(R.id.tv_for_nav_dr_email);

            tv_name.setText(StaticClassForGlobalInfo.UserName);
            tv_email.setText(StaticClassForGlobalInfo.UserEmail);
        }
    }




    @Override
    public void onBackPressed() {
        //below code is for "click two time to exit the application"
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            finishAffinity();
            System.exit(0);
        }
        else { Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show(); }
        mBackPressed = System.currentTimeMillis();

        //just adding an animatiion here whic makes it go with animation sliding to right
        overridePendingTransition(R.anim.left_in, R.anim.right_out);

    }
}
