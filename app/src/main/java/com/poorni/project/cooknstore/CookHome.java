package com.poorni.project.cooknstore;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.poorni.project.cooknstore.data.RecipeDetailDataSet;
import com.poorni.project.cooknstore.data.ShoppingListDataSet;
import com.poorni.project.cooknstore.network.RecipeOperations;

import com.poorni.project.cooknstore.utility.XMLManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import android.net.Uri;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CookHome extends AppCompatActivity implements RecipeViewAdapter.RecipeViewListener {


    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawerLayout;
    RecyclerView recipeView;
    RecipeViewAdapter recipeViewAdapter;
    List<RecipeStorage> storageList;
    ArrayList<RecipeStorage> searchList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUp(toolbar);
        if(savedInstanceState == null)
            getShoppingList();

        searchList = new ArrayList<>();

        recipeView = (RecyclerView)findViewById(R.id.recipeRecyclerView);
        recipeView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        // get the recipes from sqlite
        RecipeOperations operations = new RecipeOperations(this);
        try {
            operations.open();
            storageList = operations.getAllRecipes();
            operations.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(storageList != null)
        {
            recipeViewAdapter = new RecipeViewAdapter(this,storageList);
            recipeView.setAdapter(recipeViewAdapter);
            recipeViewAdapter.setListener(this);
        }



        Intent intent = getIntent();
        String action = intent.getAction();

        //import recipes from backup files..
        if (action!=null && action.compareTo(Intent.ACTION_VIEW) == 0) {
            String scheme = intent.getScheme();
            ContentResolver resolver = getContentResolver();
            if (scheme.compareTo(ContentResolver.SCHEME_CONTENT) == 0) {
                Uri uri = intent.getData();
                Log.v("tag", "Content intent detected: " + action + " : " + intent.getDataString() + " : " + intent.getType() + " : ");
                InputStream input = null;
                try {
                    input = resolver.openInputStream(uri);
                    XMLManager manager = new XMLManager();
                    ArrayList<RecipeStorage>  importList = manager.parseXml(input);
                    insertRecipesfromXML(importList);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (scheme.compareTo(ContentResolver.SCHEME_FILE) == 0) {
                Uri uri = intent.getData();
                String name = uri.getLastPathSegment();

                Log.v("tag" , "File intent detected: " + action + " : " + intent.getDataString() + " : " + intent.getType() + " : " + name);
                try {
                    InputStream input = resolver.openInputStream(uri);
                    XMLManager manager = new XMLManager();
                    ArrayList<RecipeStorage>  importList = manager.parseXml(input);
                    insertRecipesfromXML(importList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //import recipes from backup xmlfile
    public void insertRecipesfromXML(ArrayList<RecipeStorage> importList) {
        RecipeOperations operations = new RecipeOperations(getBaseContext());
        if (importList != null) {
            for (RecipeStorage recipeStorage : importList) {
                RecipeDetailDataSet recipeDetails = RecipeDetailDataSet.getInstance();
                recipeDetails.setCategory(recipeStorage.getCategory());
                recipeDetails.setDirections(recipeStorage.getDirections());
                recipeDetails.setName(recipeStorage.getName());
                recipeDetails.setPictureUrl(recipeStorage.getPictureUrl());
                recipeDetails.setServes(recipeStorage.getServes());
                recipeDetails.setTotalTime(recipeStorage.getTotalTime());
                recipeDetails.setIngredientList(recipeStorage.getIngredientList());
                long recipeId = -1;
                try {
                    operations.open();
                    recipeId = operations.insertData();
                    operations.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (storageList == null) {
                    storageList = new ArrayList<RecipeStorage>();
                }
                if (recipeId != -1) {
                    storageList.add(new RecipeStorage(recipeId, recipeDetails));
                }
            }

        }

    }

    public void setUp( final Toolbar toolbar)
    {
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView)findViewById(R.id.navigation_view);
      //  navView.inflateMenu(R.menu.drawer);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.home_screen)
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                if(item.getItemId()==R.id.shopping_list)
                    startActivity(new Intent(getBaseContext(),ShoppingListView.class));
                if(item.getItemId()==R.id.backup) {
                    ArrayList<RecipeStorage> storageList = new ArrayList<RecipeStorage>();
                    takeBackup(storageList);
                }
                if(item.getItemId()==R.id.import_export){
                    AlertDialog alertDialog = new AlertDialog.Builder(CookHome.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("You can import backed up files, opening it up by CookNStore");

                    alertDialog.setButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                dialog.cancel();
                                }
                            });
                    alertDialog.show();
                }

                return false;
            }
        });
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
/*                if(!mUserLearnedDrawer)
                {
                    mUserLearnedDrawer = true;
                    saveToPreferences(this,KEY_USER_LEARNED_DRAWER,mUserLearnedDrawer+"");
                }*/
                invalidateOptionsMenu();
            }

            @Override
            public  void onDrawerSlide(View drawerView, float slideOffset)
            {
           /*     if(slideOffset <0.7)
                    toolbar.setAlpha(1-slideOffset);*/
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
/*       if(!mUserLearnedDrawer && !mFromSavedInstanceState)
        {
            layout.openDrawer(getCurrentFocus());
        }*/
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cook_home, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search_recipe).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(new ComponentName(getApplicationContext(), CookHome.class)));
       // searchView.setSubmitButtonEnabled(true);
        searchView.setIconifiedByDefault(false);
        searchView.requestFocusFromTouch();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                searchList.clear();
                //use the query to search your data somehow
                for (RecipeStorage rs : storageList) {
                    if (rs.getName().equalsIgnoreCase(query) || rs.getName().toLowerCase().contains(query.toLowerCase()))
                        searchList.add(rs);

                }
                if (recipeViewAdapter != null && searchList.size() > 0) {
                    recipeViewAdapter.data = searchList;
                    recipeViewAdapter.notifyDataSetChanged();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ((!hasFocus) && recipeViewAdapter != null) {
                    recipeViewAdapter.data = storageList;
                    recipeViewAdapter.notifyDataSetChanged();
                }
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_recipe) {
            Intent intent = new Intent(this,NewRecipeActivity.class);
            intent.putExtra("MODE","New");
            startActivity(intent);

        }
        if (id==android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }




        return super.onOptionsItemSelected(item);
    }


    @Override
    public void itemClicked(View view, long id) {
        Intent intent = new Intent(this,RecipeDetailsView.class);
        intent.putExtra("ID",id);
        startActivity(intent);
    }
    public void getShoppingList()
    {
        FileInputStream fis = null;
        try{
            fis = openFileInput("cooknstore");
            ObjectInputStream objectInputStream = new ObjectInputStream(fis);
            ShoppingListDataSet shoppingListDataSet = ShoppingListDataSet.getInstance();
           shoppingListDataSet.gShoppingListItems = ((ShoppingListDataSet)objectInputStream.readObject()).gShoppingListItems;
        }catch (IOException e){
            e.printStackTrace();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }

    }
    public void takeBackup(ArrayList<RecipeStorage> storages){

            XMLManager manager = new XMLManager();
            manager.mContext = getBaseContext();
            File backupFile =manager.takeBackup(storageList);
            String appPath = getApplicationContext().getFilesDir().getAbsolutePath();

            if(backupFile != null){
              //  Uri fileUri = FileProvider.getUriForFile(getBaseContext(), "com.simbiosys.avatar.cooknstore.fileprovider", backupFile);
                // java.net.URI fileUri = backupFile.toURI();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
               // String temp = getContentResolver().getType(fileUri);
                sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(backupFile));
                //sendIntent.setType(getContentResolver().getType(fileUri));
                sendIntent.setType("text/xml");
                startActivity(sendIntent);
            }
        }

}
