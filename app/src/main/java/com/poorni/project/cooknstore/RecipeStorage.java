package com.poorni.project.cooknstore;


import android.app.Application;

import com.poorni.project.cooknstore.data.IngredientDataSet;
import com.poorni.project.cooknstore.data.RecipeDetailDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harini on 3/11/16.
 */
public class RecipeStorage  {
    private String Name;
    private String Category;
    private String Serves;
    private String TotalTime;
    private String PictureUrl;
    private List<IngredientDataSet> IngredientList;
    private String Directions;
   // public static List<RecipeStorage> gStorageList;

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

    public long getId() { return Id; }

    private long Id;


    //private constructor
    public RecipeStorage(long id, String name, String category, String serves, String totalTime,String pictureUrl,
                         String directions,List<IngredientDataSet> dataSet){
        this.Id = id;
        Name = name;
        Category=category;
        Serves=serves;
        TotalTime=totalTime;
        PictureUrl=pictureUrl;
        IngredientList = dataSet;
        Directions =directions;

    }
    public RecipeStorage(long id,RecipeDetailDataSet detail)
    {
        Id = id;
        Name = detail.getName();
        Category = detail.getCategory();
        Serves = detail.getServes();
        TotalTime = detail.getTotalTime();
        PictureUrl = detail.getPictureUrl();
        IngredientList = detail.getIngredientList();
        Directions = detail.getDirections();
    }

}
