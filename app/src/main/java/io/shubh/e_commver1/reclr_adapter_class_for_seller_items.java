package io.shubh.e_commver1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class reclr_adapter_class_for_seller_items extends RecyclerView.Adapter<reclr_adapter_class_for_seller_items.ViewHolder> {
    private ArrayList<classForSellerItemReclrDATAObject> dataForItemArrayList;
    private Context context;



    public reclr_adapter_class_for_seller_items(Context context, ArrayList<classForSellerItemReclrDATAObject> dataForItems) {
        this.context = context;
        this.dataForItemArrayList = dataForItems;

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_item_Title;
        TextView tv_item_catgr;
        TextView tv_item_desc;
     //   TextView tv_vidTime;
        ImageView iv_item_image;

        ImageButton delete_bt;
        ImageButton edit_bt;

        public ViewHolder(View view) {
            super(view);

            tv_item_Title = (TextView)view.findViewById(R.id.id_tv_title_fR_rclr_item_seller_item_list);
            tv_item_desc = (TextView)view.findViewById(R.id.id_tv_descr_fR_rclr_item_seller_item_list);
            tv_item_catgr = (TextView)view.findViewById(R.id.id_fr_tv_ctgr_fR_rclr_item_seller_item_list);
         //   tv_vidTime = (TextView)view.findViewById(R.id.tv_vid_time);
            iv_item_image = (ImageView)view.findViewById(R.id.id_iv_fR_rclr_item_seller_item_list);

            edit_bt =(ImageButton) view.findViewById(R.id.id_bt_delete_fR_rclr_item_seller_item_list);
            delete_bt  =(ImageButton) view.findViewById(R.id.id_bt_edit_fR_rclr_item_seller_item_list);
        }
    }

    @Override
    public reclr_adapter_class_for_seller_items.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reclr_item_fr_seller_items_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull reclr_adapter_class_for_seller_items.ViewHolder holder, int position) {

        holder.tv_item_Title.setText(dataForItemArrayList.get(position).getItem_title());
        holder.tv_item_desc.setText(dataForItemArrayList.get(position).getItem_Descrp());

        Log.d("&&&&&&&&&&3", dataForItemArrayList.get(position).getItem_Descrp());

        holder.tv_item_catgr.setText(dataForItemArrayList.get(position).getItem_ctgr());
        //     holder.iv_item_image.setText(dataForItemArrayList.get(position).getvid_time());
        // Picasso.with(context).load(dataForItemArrayList.get(position).getVid_image_url()).placeholder(R.drawable.load).into(viewHolder.img_android);
        Picasso.get().load(dataForItemArrayList.get(position).getItem_image_url()).placeholder(R.drawable.ring)
                .resize(300, 300).centerCrop().into(holder.iv_item_image);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    /*    viewHolder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareBody = dataForItemArrayList.get(i).getVidTitle() + "\n\nSee this titled video at this app in play store" + "\n\nhttps://play.google.com/store/apps/details?id=io.shubh.BeautyApp";
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                view.getContext().startActivity(Intent.createChooser(sharingIntent, "Share"));
            }
        });*/

    }


    @Override
    public int getItemCount() {
        return dataForItemArrayList.size();
    }



}