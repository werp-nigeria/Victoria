package io.shubh.e_commver1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class reclr_adapter_class_for_main_activity_items extends RecyclerView.Adapter<reclr_adapter_class_for_main_activity_items.ViewHolder> {
    private ArrayList<ClassForMainActvityItemReclrDATAObject> dataForItemArrayList;
    private Context context;



    public reclr_adapter_class_for_main_activity_items(Context context, ArrayList<ClassForMainActvityItemReclrDATAObject> dataForItems) {
        this.context = context;
        this.dataForItemArrayList = dataForItems;

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_item_Title;

        ImageView iv_item_image;

        CardView cv_item ;


        public ViewHolder(View view) {
            super(view);

            tv_item_Title = (TextView)view.findViewById(R.id.id_tv_fR_main_item_ctgr_item_list);
iv_item_image=(ImageView) view.findViewById(R.id.id_iv_fR_main_item_ctgr_item_list);

            cv_item = (CardView) view.findViewById(R.id.id_card_fR_main_item_ctgr_item_list);
        }
    }

    @Override
    public reclr_adapter_class_for_main_activity_items.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reclr_item_fr_main_activity_items_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull reclr_adapter_class_for_main_activity_items.ViewHolder holder, int position) {

        holder.tv_item_Title.setText(dataForItemArrayList.get(position).getItem_title());

        Picasso.get().load(dataForItemArrayList.get(position).getItem_image_url())
                //.placeholder(R.drawable.ring)
                .resize(300, 300).centerCrop().into(holder.iv_item_image);


        holder.cv_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(context, CategoryItemsActivity.class);
                myIntent.putExtra("name" ,dataForItemArrayList.get(position).getItem_title() );
                context.startActivity(myIntent);

                //animation for sliding activity
                Activity activity = (Activity) context;
                activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });
       // this code is on main activty
       /* holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(context, CategoryItemsActivity.class);
                myIntent.putExtra("name" ,dataForItemArrayList.get(position).getItem_title() );
                context.startActivity(myIntent);
            }
        });*/

    /*    holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareBody = dataForItemArrayList.get(i).getVidTitle() + "\n\nSee this titled video at this app in play store" + "\n\nhttps://play.google.com/store/apps/details?id=io.shubh.BeautyApp";
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                view.getContext().startActivity(Intent.createChooser(sharingIntent, "Share"));
            }
        });
*/
    }


    @Override
    public int getItemCount() {
        return dataForItemArrayList.size();
    }



}