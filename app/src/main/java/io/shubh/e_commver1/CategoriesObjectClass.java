package io.shubh.e_commver1;

import java.util.ArrayList;

public class CategoriesObjectClass {

    //***************************************Documentaion - of structure holding the categories heirarchy***********************

        /*

        the Conceptual model for  heirarchy is:-

        master -List  ---->Category(Like- ['men-s Fashion' , 'womens fashion',.....])
            Sub-list(one sub categories list for each item in categories ) -------> SubCategory for category named 'Mens fashion ' ===> ['Clothing' , 'Shoes' ,...]
                Sub-Sub-list(one sub-sub-categories list for each item in sub-categories ) -------> SubSubCategory for sub-category named 'Clothing ' ===> ['T-Shirts' , 'Shirts' ,...]


        we have stored the whole category data in following structure

                       List<List<CategoriesObjectClass>> super_nested_list_of_categories;
                       List<String> categories_names;


                     Category_names----------------------------------------------------->               'men-s Fashion'                                 ,                     'womens fashion',.....]
                                                                                                                    |                                                                |
                                                                                                                    |                                                                |
     super_nested_list_of_categories -->[elemnt_no_0_of_list(of_string(sub_catepgry_name)and_nested_list(list_of_sub_sub_categories)of_aboveelement)      ,           elemnt_no_1_of_list(of_string(sub_catepgry_name)and_nested_list(list_of_sub_sub_categories)of_aboveelement)                                                     ]
                                                                                                                     |                                                                |
                                                                                                                     |                                                                |
                                                                                                                     |                                                                |
                                                        [       {Clothing , subsubCategories list}       ,        {Shoes , subsubCategories list} , ....]
                                                                                        |                               |
                                                                                        |                               |
                                                                                        |                               |
                                                                            [Shirts , Tshirts,......]             [Sneakers  , Sports,......]


        */



    String categoryNmae ;
    ArrayList<String> listOfsubCategory;

    //Constructor
    public  CategoriesObjectClass() {
   //     this.categoryNmae =categoryNmae;
       listOfsubCategory = new ArrayList<String>();
    }


    public void setCategoryNmae(String categoryNmae) {
        this.categoryNmae = categoryNmae;
    }

    public void setListOfsubCategory(ArrayList<String> listOfsubCategory) {
        this.listOfsubCategory = listOfsubCategory;
    }

    public ArrayList<String> getListOfsubCategory() {
        return listOfsubCategory;
    }

    public String getCategoryNmae() {
        return categoryNmae;
    }
}
