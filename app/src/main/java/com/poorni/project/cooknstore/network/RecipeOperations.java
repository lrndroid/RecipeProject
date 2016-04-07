package com.poorni.project.cooknstore.network;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.poorni.project.cooknstore.data.IngredientDataSet;
import com.poorni.project.cooknstore.data.RecipeDetailDataSet;
import com.poorni.project.cooknstore.RecipeStorage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harini on 3/11/16.
 */
public class RecipeOperations {
    DBHelper dbHelper;
    Context context;
    private SQLiteDatabase database;
    private  String[] RECIPE_TABLE_COLUMNS = {DBHelper.RECIPE_ID, DBHelper.RECIPE_TITLE,DBHelper.RECIPE_CATEGORY,DBHelper.RECIPE_SERVES,
            DBHelper.RECIPE_TIME,DBHelper.RECIPE_IMAGE,DBHelper.RECIPE_DIRECTION};
    private String[] INGREDIENT_TABLE_COLUMNS = {DBHelper.INGREDIENT_ID,DBHelper.RECIPE_INGREDIENT_NAME,DBHelper.RECIPE_INGREDIENT_QUANTITY,
            DBHelper.RECIPE_INGREDIENT_UNIT,DBHelper.RECIPE_INGREDIENT_UNIT_POSITION};
    public  RecipeOperations(Context context)
    {
        this.context = context;
        dbHelper = new DBHelper(context);
    }
    public void open() throws SQLException
    {
        database = dbHelper.getWritableDatabase();
    }
    public void close()throws SQLException{

        dbHelper.close();
    }

    public void deleteRecipe(long position){
        database.delete(DBHelper.RECIPE,DBHelper.RECIPE_ID + "=?", new String[]{"" + position});
        database.delete(DBHelper.INGREDIENT, DBHelper.RECIPE_ID + "=?", new String[]{"" + position});
    }

/*    public long updateRecipe(long position){
        deleteRecipe(position);
        return insertData();
    }*/
    public long updateRecipe(long id){
        RecipeDetailDataSet updateRecipe = RecipeDetailDataSet.getInstance();
        ContentValues contentRecipe = new ContentValues();
        contentRecipe.put(DBHelper.RECIPE_TITLE, updateRecipe.getName());
        contentRecipe.put(DBHelper.RECIPE_CATEGORY, updateRecipe.getCategory());
        contentRecipe.put(DBHelper.RECIPE_SERVES, updateRecipe.getServes());
        contentRecipe.put(DBHelper.RECIPE_TIME, updateRecipe.getTotalTime());
        contentRecipe.put(DBHelper.RECIPE_IMAGE, updateRecipe.getPictureUrl());
        contentRecipe.put(DBHelper.RECIPE_DIRECTION, updateRecipe.getDirections());
       long recipeId=  database.update(DBHelper.RECIPE,contentRecipe,DBHelper.RECIPE_ID+"=?",new String[]{""+id});
        ContentValues contentIngredient = new ContentValues();
        database.delete(DBHelper.INGREDIENT, DBHelper.RECIPE_ID + "=?", new String[]{"" + id});
        for (int i = 0; i < updateRecipe.getIngredientList().size(); i++) {
            contentIngredient.put(DBHelper.RECIPE_ID, id);
            contentIngredient.put(DBHelper.RECIPE_INGREDIENT_NAME, updateRecipe.getIngredientList().get(i).getIngredientName());
            contentIngredient.put(DBHelper.RECIPE_INGREDIENT_QUANTITY, updateRecipe.getIngredientList().get(i).getIngredientQuantity());
            contentIngredient.put(DBHelper.RECIPE_INGREDIENT_UNIT, updateRecipe.getIngredientList().get(i).getIngredientUnit());
            contentIngredient.put(DBHelper.RECIPE_INGREDIENT_UNIT_POSITION, updateRecipe.getIngredientList().get(i).getUnitPosition());
            database.insert(DBHelper.INGREDIENT, null, contentIngredient);
        }
        return id;
    }


    public long insertData() {
        RecipeDetailDataSet insertRecipe = RecipeDetailDataSet.getInstance();

        ContentValues contentRecipe = new ContentValues();
        contentRecipe.put(DBHelper.RECIPE_TITLE, insertRecipe.getName());
        contentRecipe.put(DBHelper.RECIPE_CATEGORY, insertRecipe.getCategory());
        contentRecipe.put(DBHelper.RECIPE_SERVES, insertRecipe.getServes());
        contentRecipe.put(DBHelper.RECIPE_TIME, insertRecipe.getTotalTime());
        contentRecipe.put(DBHelper.RECIPE_IMAGE, insertRecipe.getPictureUrl());
        contentRecipe.put(DBHelper.RECIPE_DIRECTION, insertRecipe.getDirections());

        long recipeId = database.insert(DBHelper.RECIPE, null, contentRecipe);
        ContentValues contentIngredient = new ContentValues();

        for (int i = 0; i < insertRecipe.getIngredientList().size(); i++) {
            contentIngredient.put(DBHelper.RECIPE_ID, recipeId);
            contentIngredient.put(DBHelper.RECIPE_INGREDIENT_NAME, insertRecipe.getIngredientList().get(i).getIngredientName());
            contentIngredient.put(DBHelper.RECIPE_INGREDIENT_QUANTITY, insertRecipe.getIngredientList().get(i).getIngredientQuantity());
            contentIngredient.put(DBHelper.RECIPE_INGREDIENT_UNIT, insertRecipe.getIngredientList().get(i).getIngredientUnit());
            contentIngredient.put(DBHelper.RECIPE_INGREDIENT_UNIT_POSITION, insertRecipe.getIngredientList().get(i).getUnitPosition());
            database.insert(DBHelper.INGREDIENT, null, contentIngredient);
        }
        return  recipeId;
    }
    public RecipeStorage getRecipe(long id){
        RecipeStorage recipeStorage = null;
        Cursor cursor = database.query(DBHelper.RECIPE,RECIPE_TABLE_COLUMNS,DBHelper.RECIPE_ID + "=?", new String[]{"" + id},null,null,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            long recipeId = cursor.getInt(0);
            Cursor cursorIngredients = database.query(DBHelper.INGREDIENT, INGREDIENT_TABLE_COLUMNS, DBHelper.RECIPE_ID + "=?", new String[]{"" + id}, null, null, null);

            List<IngredientDataSet> ingredientList = new ArrayList<>();
            cursorIngredients.moveToFirst();
            while (!cursorIngredients.isAfterLast()){
                IngredientDataSet ids= new IngredientDataSet(cursorIngredients.getString(1),cursorIngredients.getString(2),cursorIngredients.getString(3),cursorIngredients.getInt(4));
                ingredientList.add(ids);
                cursorIngredients.moveToNext();
            }
            recipeStorage = new RecipeStorage(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),ingredientList);
            cursor.moveToNext();
        }
        return recipeStorage;
    }
    public List<RecipeStorage> getAllRecipes()
    {
        List<RecipeStorage> recipeList = new ArrayList<>();
        Cursor cursor =  database.query(DBHelper.RECIPE, RECIPE_TABLE_COLUMNS, null, null, null, null, null);
        cursor.moveToFirst();
        while ((!cursor.isAfterLast()))
        {
            long id = cursor.getInt(0);
            Cursor cursorIngredients = database.query(DBHelper.INGREDIENT, INGREDIENT_TABLE_COLUMNS, DBHelper.RECIPE_ID + "=?", new String[]{"" + id}, null, null, null);
            List<IngredientDataSet> ingredientList = new ArrayList<>();
            cursorIngredients.moveToFirst();
            while((!cursorIngredients.isAfterLast()))
            {
                IngredientDataSet ids= new IngredientDataSet(cursorIngredients.getString(1),cursorIngredients.getString(2),cursorIngredients.getString(3),cursorIngredients.getInt(4));
                ingredientList.add(ids);
                cursorIngredients.moveToNext();
            }
            RecipeStorage recipeStorage = new RecipeStorage(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),ingredientList);
            cursor.moveToNext();
            recipeList.add(recipeStorage);
        }
        cursor.close();
        return  recipeList;
    }

}
