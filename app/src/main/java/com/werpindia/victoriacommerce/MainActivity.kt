package com.werpindia.victoriacommerce

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.werpindia.victoriacommerce.adapters.CategoriesAdapter
import com.werpindia.victoriacommerce.databinding.NavigationDrawerBinding
import com.werpindia.victoriacommerce.models.Category

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navigationDrawerBinding: NavigationDrawerBinding =
            DataBindingUtil.setContentView(this, R.layout.navigation_drawer)

        val toolbar: Toolbar = navigationDrawerBinding.main.mainToolBar;

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            navigationDrawerBinding.drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        navigationDrawerBinding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationDrawerBinding.main.categoriesList.layoutManager = GridLayoutManager(applicationContext, 2)
        navigationDrawerBinding.main.categoriesList.setHasFixedSize(true)

        val categories: ArrayList<Category> = ArrayList()
        categories.add(Category("", "Men"))
        categories.add(Category("", "Electronic"))
        categories.add(Category("", "Women"))
        categories.add(Category("", "Devices"))
        categories.add(Category("", "Shoes"))
        categories.add(Category("", "Men"))

        navigationDrawerBinding.main.categoryAdapter = CategoriesAdapter(categories, applicationContext)

    }
}
