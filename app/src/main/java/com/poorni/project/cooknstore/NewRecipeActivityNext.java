package com.poorni.project.cooknstore;


import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;


import com.poorni.project.cooknstore.data.RecipeDetailDataSet;
import com.poorni.project.cooknstore.data.ShoppingListDataSet;

import java.util.List;

public class NewRecipeActivityNext extends AppCompatActivity {

    List<ShoppingListDataSet.ShoppingListItems> shoppingList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe_next);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {

            RecipeDetailDataSet.getInstance().IsBack = true;
        }
        return super.onOptionsItemSelected(item);
    }


}
