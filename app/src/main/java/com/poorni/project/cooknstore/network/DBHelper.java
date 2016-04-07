package com.poorni.project.cooknstore.network;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Harini on 3/11/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String RECIPE = "Recipe";
    public static final String INGREDIENT ="Ingredient";
    public static final String INGREDIENT_ID = "_ingredientid";
    public static final String RECIPE_ID = "_recipeid";
    public static final String RECIPE_TITLE = "_title";
    public static final String RECIPE_CATEGORY = "_category";
    public static final String RECIPE_SERVES = "_serves";
    public static final String RECIPE_TIME = "_time";
    public static final String RECIPE_IMAGE = "_url";
    public static final String RECIPE_DIRECTION = "_direction";
    public static final String RECIPE_INGREDIENT_NAME = "_item";
    public static final String RECIPE_INGREDIENT_QUANTITY = "_quantity";
    public static final String RECIPE_INGREDIENT_UNIT ="_unit";
    public static final String RECIPE_INGREDIENT_UNIT_POSITION = "_unitposition";



    private static final String DATABASE_NAME = "CookNStore.db";
    private static final int DATABASE_VERSION = 1;


    // creation SQLite statement
    private static final String RECIPE_TABLE_CREATE = "create table " + RECIPE
            + "(" + RECIPE_ID + " integer primary key autoincrement, "
            + RECIPE_TITLE + " text not null, "+ RECIPE_CATEGORY
            +" text not null, "+ RECIPE_SERVES + " text not null, " + RECIPE_TIME+ " text not null, "
            + RECIPE_IMAGE + " text not null, " + RECIPE_DIRECTION + " text not null);";
    private static final String INGREDIENT_TABLE_CREATE = "create table "+ INGREDIENT + "(" + INGREDIENT_ID + " integer primary key autoincrement, " +
            ""+RECIPE_ID+ " integer not null, " + RECIPE_INGREDIENT_NAME + " text not null, "+RECIPE_INGREDIENT_QUANTITY+" text not null, "+
            RECIPE_INGREDIENT_UNIT+" text not null, "+RECIPE_INGREDIENT_UNIT_POSITION+ " integer not null);";

    public DBHelper (Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RECIPE_TABLE_CREATE);
        db.execSQL(INGREDIENT_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+RECIPE);
            onCreate(db);
    }
    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }

}
