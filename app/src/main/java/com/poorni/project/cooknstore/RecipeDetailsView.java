package com.poorni.project.cooknstore;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.poorni.project.cooknstore.data.IngredientDataSet;
import com.poorni.project.cooknstore.data.RecipeDetailDataSet;
import com.poorni.project.cooknstore.data.ShoppingListDataSet;
import com.poorni.project.cooknstore.network.RecipeOperations;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecipeDetailsView extends AppCompatActivity implements ShoppingListNameDialog.ShoppingListCommunicator,NewShoppingListDialog.NewShoppingListInterface {
    TextView title;
    ImageView image;
    TextView time;
    TextView serves;
    TextView ingredients;
    TextView details;
    ImageView downArrow;
    ImageView upArrow;
    ImageView upArrowDirections;
    ImageView downArrowDirections;
    List<String> addToShoppingList;
    long recipeId;
    RecipeStorage currentRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = (TextView)findViewById(R.id.titleText);
        image = (ImageView)findViewById(R.id.recipeImage);
        time = (TextView)findViewById(R.id.timeText);
        serves =(TextView)findViewById(R.id.servesText);
        ingredients = (TextView)findViewById(R.id.ingredientsText);
        details = (TextView)findViewById(R.id.directionsText);
        details.setVisibility(View.VISIBLE);
        ingredients.setVisibility(View.VISIBLE);

        downArrow = (ImageView)findViewById(R.id.downArrow);
        upArrow = (ImageView)findViewById(R.id.upArrow);

        upArrow.setVisibility(View.GONE);

        upArrowDirections = (ImageView)findViewById(R.id.upArrowDirection);
        downArrowDirections = (ImageView)findViewById(R.id.downArrowDirection);
        upArrowDirections.setVisibility(View.GONE);
        TextView actionbarTitle = (TextView)findViewById(R.id.toolbar_title);
        addToShoppingList = new ArrayList<>();
        recipeId = getIntent().getLongExtra("ID",-1);
        RecipeOperations operations = new RecipeOperations(getBaseContext());
         currentRecipe = null;
        if(operations!=null){
            try{
                operations.open();
                currentRecipe = operations.getRecipe(recipeId);
                operations.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

        if(currentRecipe!= null) {
        //    title.setText(RecipeStorage.gStorageList.get(position).getName());

            actionbarTitle.setText(currentRecipe.getName());
            if(!currentRecipe.getPictureUrl().isEmpty())
                image.setImageBitmap(getScaledBitmap(currentRecipe.getPictureUrl(), 385, 200));
            time.setText("Total Time: "+currentRecipe.getTotalTime());
            serves.setText("Serves: "+currentRecipe.getServes());

            details.setText(" \n"+currentRecipe.getDirections());
            List<IngredientDataSet> ingredientList = currentRecipe.getIngredientList();
            ingredients.setText(" \n");
            for(int i =0 ;i <ingredientList.size();i++) {

                IngredientDataSet ds = ingredientList.get(i);

                addToShoppingList.add(ingredientList.get(i).getIngredientName());
                String units = ds.IngredientUnit;
                if(ds.IngredientUnit.equals("unit"))
                    units = "";
                ingredients.setText(ingredients.getText()+ds.getIngredientName()+ " "+ds.IngredientQuantity+ " "+units+"\n");
            }
        }
    }
    public void toggleIngredients(View view)
    {
        ingredients.setVisibility(ingredients.isShown()?View.GONE:View.VISIBLE);
        upArrow.setVisibility(upArrow.isShown()?View.GONE:View.VISIBLE);
        downArrow.setVisibility(downArrow.isShown()?View.GONE:View.VISIBLE);
    }
    public void toggleDirections(View view)
    {
        details.setVisibility(details.isShown() ? View.GONE : View.VISIBLE);
        upArrowDirections.setVisibility(upArrowDirections.isShown()?View.GONE:View.VISIBLE);
        downArrowDirections.setVisibility(downArrowDirections.isShown()?View.GONE:View.VISIBLE);
    }
    private Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);
        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.share_recipe)
            shareRecipe();
        else if(item.getItemId() == R.id.edit_recipe){
            editRecipe(recipeId);
        }
        else if(item.getItemId() == R.id.delete_recipe){
            deleteRecipe(recipeId);
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteRecipe(final long recipeId){
        AlertDialog.Builder builder = new AlertDialog.Builder(RecipeDetailsView.this);
        builder.setTitle(currentRecipe.getName());
        builder.setMessage("Do you want to delete this Recipe Permanently?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RecipeOperations operations = new RecipeOperations(getBaseContext());
                try {
                    operations.open();
                    operations.deleteRecipe(recipeId);
/*                    Iterator<RecipeStorage> itr = RecipeStorage.gStorageList.iterator();
                    while (itr.hasNext()) {
                        RecipeStorage storage = itr.next();
                        if (storage.getId() == recipeId)
                            itr.remove();
                    }*/
                    operations.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(RecipeDetailsView.this, CookHome.class));
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

     /*   AlertDialog alertDialog = new AlertDialog.Builder(RecipeDetailsView.this).create();
        alertDialog.setTitle(currentRecipe.getName());
        alertDialog.setMessage("Do you want to delete this Recipe Permanently?");

        alertDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.setButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        RecipeOperations operations = new RecipeOperations(getBaseContext());
                        try {
                            operations.open();
                            operations.deleteRecipe(recipeId);
                            Iterator<RecipeStorage> itr = RecipeStorage.gStorageList.iterator();
                            while(itr.hasNext())
                            {
                                RecipeStorage storage = itr.next();
                                if(storage.getId() == recipeId)
                                    itr.remove();
                            }
                            operations.close();
                        }catch (SQLException e){
                            e.printStackTrace();
                        }
                        startActivity(new Intent(RecipeDetailsView.this,CookHome.class));
                    }
                });
        alertDialog.show();*/
    }

    public void editRecipe(final long mPosition){

        Intent intent =new Intent(RecipeDetailsView.this,NewRecipeActivity.class);
        intent.putExtra("EDIT_RECIPE_ID",mPosition);
        intent.putExtra("MODE","Edit");
        startActivity(intent);
    }

    public void onShoppingList(View view)
    {
           ShoppingListNameDialog dialog = new ShoppingListNameDialog();
           dialog.show(getFragmentManager(),"Shopping List");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_details,menu);
        return true;
    }

    @Override
    public void onShoppingOptionSelected(int option) {
        if(option == ShoppingListDataSet.getInstance().gShoppingListItems.size())
        {
            NewShoppingListDialog dialog =new NewShoppingListDialog();
            dialog.show(getFragmentManager(),"New Shopping List");
        }
        else
        {
            Intent intent = new Intent(getBaseContext(),NewShoppingListActivity.class);
            ShoppingListDataSet ds = ShoppingListDataSet.getInstance();
            ds.gShoppingListItems.get(option).items.addAll(addToShoppingList);
            intent.putExtra("SHOPPING_LIST_POSITION",option);
            startActivity(intent);
        }
    }

    @Override
    public void onNewShoppingList(String shoppingListName) {

        ShoppingListDataSet ds = ShoppingListDataSet.getInstance();
        ds.addItem(addToShoppingList, shoppingListName);
        Intent intent = new Intent(this,NewShoppingListActivity.class);
        intent.putExtra("SHOPPING_LIST_NAME",shoppingListName);
        intent.putExtra("SHOPPING_LIST_POSITION",ds.gShoppingListItems.size()-1);
        startActivity(intent);
    }
    public void shareRecipe()
    {
        RecipeStorage shareRecipe = currentRecipe;
        String ingredients =  "Ingrdients :<br/><br/>";
        for(int i=0; i<shareRecipe.getIngredientList().size();i++)
        {
            ingredients = ingredients + shareRecipe.getIngredientList().get(i).getIngredientName()+"<br/>";
        }
        String shareString = Html.fromHtml("<b>Total Time: </b>" + shareRecipe.getTotalTime() +
                "<p>Serves: " + shareRecipe.getServes()+ "</p>"+"<p>"+ingredients+"</p>"+
                "<p> Directions:<br/><br /> "+shareRecipe.getDirections()+"</p>").toString();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        Bitmap bm = BitmapFactory.decodeFile(currentRecipe.getPictureUrl());
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, currentRecipe.getName());
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareString);
        String url= MediaStore.Images.Media.insertImage(this.getContentResolver(), bm, "title", "description");
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));

        sendIntent.setType("image/*");
        startActivity(sendIntent);
    }
}
