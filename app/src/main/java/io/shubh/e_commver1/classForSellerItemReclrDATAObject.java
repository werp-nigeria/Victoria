package io.shubh.e_commver1;

import java.util.ArrayList;

public class classForSellerItemReclrDATAObject {

    private String item_id;
    private String item_title;
    private String item_Descrp;
    private String item_ctgr;
    private String item_image_url;
    private ArrayList<String>[] item_all_images_list ;


    public String getItem_id() {
        return item_id;
    }

    public String getItem_title() {
        return item_title;
    }

    public String getItem_Descrp() {
        return item_Descrp;
    }

    public String getItem_ctgr() {
        return item_ctgr;
    }

    public String getItem_image_url() {
        return item_image_url;
    }

    public ArrayList<String>[] getItem_all_images_list() {
        return item_all_images_list;
    }


    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public void setItem_title(String item_title) {
        this.item_title = item_title;
    }

    public void setItem_Descrp(String item_Descrp) {
        this.item_Descrp = item_Descrp;
    }

    public void setItem_ctgr(String item_ctgr) {
        this.item_ctgr = item_ctgr;
    }

    public void setItem_image_url(String item_image_url) {
        this.item_image_url = item_image_url;
    }

    public void setItem_all_images_list(ArrayList<String>[] item_all_images_list) {
        this.item_all_images_list = item_all_images_list;
    }
}

