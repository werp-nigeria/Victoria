package io.shubh.e_commver1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class seller_items_list extends AppCompatActivity {

    List<String> itemID = new ArrayList<String>();


    ArrayList data_list_for_adapter ;
    RecyclerView recyclerView ;
    FirebaseFirestore db;

    String TAG ="********";

    ArrayList<classForSellerItemReclrDATAObject>   list_of_data_objects__for_adapter = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_items_list);

        db = FirebaseFirestore.getInstance();

     set_up_add_itms_bt();

     get_firebase_data_of_seller_items_for_sale();

    }

    private void get_firebase_data_of_seller_items_for_sale() {
        final int[] size_of_items = new int[1];

//getting all the documents whose have a feild 'seller id' as this seller id
        db.collection("items for sale")
                .whereEqualTo("seller id", StaticClassForGlobalInfo.UId) // <-- This line
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i=0;
                            size_of_items[0] =task.getResult().size();

                            for (DocumentSnapshot document : task.getResult()) {

                                classForSellerItemReclrDATAObject data_object = new classForSellerItemReclrDATAObject();

                                     Log.d(TAG, document.getId() + " => " + document.getData());

                                itemID.add(document.getId() );

                                data_object.setItem_id( document.getId() );
                                data_object.setItem_title(  (String) document.get("name"));
                                data_object.setItem_Descrp((String) document.get("description"));
                               data_object.setItem_ctgr((String) document.get("category"));

                                Log.d("&&&&&&&&&&1",(String) document.get("description"));
                                Log.d("&&&&&&&&&&2",data_object.getItem_Descrp());

                               //adding dummy data into this position in master list...later this will be updated with correct data
                                list_of_data_objects__for_adapter.add(i , new classForSellerItemReclrDATAObject());

                           //     this below line is amibigiious because function called willr un on sepatrate thread ,,thus I will Add urlt to below list inside the called function when data is retrived completely
                            //    data_object.setItem_image_url((retrive_all_the_item_image_url_in_a_list(document.getId()));

                                retrive_all_the_item_image_url_in_a_list(document.getId() , i ,data_object ,size_of_items[0]);

                                i++;
                            }


//TODO- I should also order the item list as per the order no(as they are in increasing order as per their timing).......Do later


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }





    private void retrive_all_the_item_image_url_in_a_list(String document_id, int index_for_list, classForSellerItemReclrDATAObject data_object, int size_of_items) {

        //HERE I AM RETRIVNG ALL THE IMAGES

        //getting all the documents whose have a feild 'item id' as this item's called from

        final ArrayList<String>[] images_URLs = new ArrayList[]{new ArrayList<>()};
        ArrayList<Integer> imagesorder =new ArrayList<>();

        db.collection("items for sale").document(document_id).collection("uploaded images urls")
                .whereEqualTo("order no", Integer.valueOf(document_id)) // <-- This line
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                images_URLs[0].add((String) document.get("url") );
                                imagesorder.add(( (Long) document.get("order no") ).intValue());
                            }


                            //TODO-sort the Image url according to their order here
                           images_URLs[0] = sort_the_url_into_order(images_URLs[0], imagesorder);

                            //now adding the first Image url
                            data_object.setItem_image_url( images_URLs[0].get(0));

                            data_object.setItem_all_images_list(images_URLs);

                            //all the work related to this specific item's data object is complete adding it to final list at correct place
                            list_of_data_objects__for_adapter.set(index_for_list , data_object);

                            //if this index is 1 less then the total size
                            //that will mean that this is the last thread ans
                            //now all the data is retrived Completely thus calling for setting up the list
                                if (index_for_list ==size_of_items-1) {
                                    set_up_the_recycler_view();

                                }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void set_up_the_recycler_view() {

        //now executing the UI part
        recyclerView = (RecyclerView)findViewById(R.id.card_recycler_view_seller_items_list);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(seller_items_list.this);
        recyclerView.setLayoutManager(layoutManager);

        //data_list_for_adapter = list_of_data_objects__for_adapter;
        reclr_adapter_class_for_seller_items adapter = new reclr_adapter_class_for_seller_items(seller_items_list.this, list_of_data_objects__for_adapter);
        recyclerView.setAdapter(adapter);


        if(list_of_data_objects__for_adapter.size()==0){
            Toast.makeText(this, "No Items found", Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<String> sort_the_url_into_order(ArrayList<String> images_url, ArrayList<Integer> imagesorder) {
        int i, key , j;
        String key2;
        for (i = 1; i < imagesorder.size(); i++)
        {
            key = imagesorder.get(i);
            key2 = images_url.get(i);
            j = i - 1;

        /* Move elements of arr[0..i-1], that are
        greater than key, to one position ahead
        of their current position */
            while (j >= 0 && imagesorder.get(j) > key)
            {
                imagesorder.set(j+1,imagesorder.get(j));
                images_url.set(j+1,images_url.get(j));
                j = j - 1;
            }
            imagesorder.set(j+1 , key);
            images_url.set(j+1 , key2);
        }
        return images_url;
    }

    private void set_up_add_itms_bt() {

        final Button bt_fr_add_items =(Button)findViewById(R.id.id_fr_bt_add_items);

        bt_fr_add_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent(seller_items_list.this, ItemToSellDetailsActivity.class);
                startActivity(in);

                //animation for sliding activity
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });
    }


    @Override
    public void onBackPressed() {
        Intent in = new Intent(seller_items_list.this, MainActivity.class);
        startActivity(in);


        //just adding an animatiion here whic makes it go with animation sliding to right
        overridePendingTransition(R.anim.left_in, R.anim.right_out);

    }
}
