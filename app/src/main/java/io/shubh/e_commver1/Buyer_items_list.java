package io.shubh.e_commver1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Buyer_items_list extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_items_list);

        final Button bt_fr_add_items =(Button)findViewById(R.id.id_fr_bt_add_items);

        bt_fr_add_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent(Buyer_items_list.this, ItemToSellDetailsActivity.class);
                startActivity(in);
            }
        });
    }
}
