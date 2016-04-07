package com.poorni.project.cooknstore.data;


import  com.poorni.project.cooknstore.data.IngredientDataSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Harini on 3/11/16.
 */
public class RecipeDetailDataSet {
    private String Name;
    private String Category;
    private String Serves;
    private String TotalTime;
    private String PictureUrl;
    private List<IngredientDataSet> IngredientList;
    private String Directions;
    public boolean IsData = false;
    public boolean IsBack = false;
    public boolean IsEdit = false;
    public long editRecipeId=-1;
    public int recipePos =-1;
    public String getCategory() { return Category; }

    public void setCategory(String category) { Category = category; }

    public String getDirections() { return Directions; }

    public void setDirections(String directions) { Directions = directions; }

    public List<IngredientDataSet> getIngredientList() { return IngredientList; }

    public void setIngredientList(List<IngredientDataSet> ingredientList) { IngredientList = ingredientList; }

    public String getName() { return Name; }

    public void setName(String name) { Name = name; }

    public String getPictureUrl() { return PictureUrl; }

    public void setPictureUrl(String pictureUrl) { PictureUrl = pictureUrl; }

    public String getServes() { return Serves; }

    public void setServes(String serves) { Serves = serves; }

    public String getTotalTime() { return TotalTime; }

    public void setTotalTime(String totalTime) { TotalTime = totalTime; }

    //private constructor
    private RecipeDetailDataSet(){
          Name = "";
          Category="";
          Serves="";
          TotalTime="";
          PictureUrl="";
          IngredientList = new ArrayList<>();
          Directions ="";

    }
    private static RecipeDetailDataSet recipeDetail;
    public static RecipeDetailDataSet getInstance()
    {
        if(recipeDetail == null)
            recipeDetail = new RecipeDetailDataSet();
        return  recipeDetail;
    }
}
