package com.werpindia.victoriacommerce.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.werpindia.victoriacommerce.databinding.CategoriesListItemBinding
import com.werpindia.victoriacommerce.models.Category

class CategoriesAdapter(private var categories: ArrayList<Category>, private var context: Context) :
    RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CategoriesListItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.categoriesListItemBinding.category = categories[position]
    }

    class ViewHolder(var categoriesListItemBinding: CategoriesListItemBinding) :
        RecyclerView.ViewHolder(categoriesListItemBinding.root)
}