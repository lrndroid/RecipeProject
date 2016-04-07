package com.poorni.project.cooknstore;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.poorni.project.cooknstore.data.IngredientDataSet;
import com.poorni.project.cooknstore.data.RecipeDetailDataSet;
import com.poorni.project.cooknstore.network.RecipeOperations;


import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class NewRecipeActivity extends AppCompatActivity implements NewRecipeFrag.NewRecipeFirstInterface {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public String mFilePath = "";
    private static int RESULT_LOAD_IMAGE = 1;
    public String IMAGE_PATH = "path";
    public RecipeDetailDataSet mRecipeDetails;
    EditText title;
    EditText category;
    EditText serves;
    EditText totalTime;
    ImageView image;
    boolean EDIT_MODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NewRecipeFrag recipeFrag = (NewRecipeFrag)getSupportFragmentManager().findFragmentById(R.id.recipeFirstFragment);
        recipeFrag.setFirstListener(this);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        title = (EditText)findViewById(R.id.titleEditText);
        category = (EditText)findViewById(R.id.categoryEditText);
        serves = (EditText)findViewById(R.id.servesEditText);
        totalTime = (EditText)findViewById(R.id.hrEditText);
        image = (ImageView)findViewById(R.id.recipeImage);

        if(mRecipeDetails == null)
            mRecipeDetails = RecipeDetailDataSet.getInstance();
        long updateRecipeId = -1;
        EDIT_MODE = false;
        Intent updateIntent = getIntent();
        if(updateIntent!=null) {
            updateRecipeId = updateIntent.getLongExtra("EDIT_RECIPE_ID", -1);
            if(updateRecipeId!=-1){
                RecipeOperations operations = new RecipeOperations(getBaseContext());
                RecipeStorage storage = null;
                if(operations!=null){
                    try{
                        operations.open();
                        storage =operations.getRecipe(updateRecipeId);
                        operations.close();
                    }catch (SQLException e){
                        e.printStackTrace();
                    }
                }

                 if(storage!=null) {
                     mRecipeDetails.setName(storage.getName());
                     mRecipeDetails.setCategory(storage.getCategory());
                     mRecipeDetails.setServes(storage.getServes());
                     mRecipeDetails.setTotalTime(storage.getTotalTime());
                     mRecipeDetails.setPictureUrl(storage.getPictureUrl());
                     mRecipeDetails.setIngredientList(storage.getIngredientList());
                     mRecipeDetails.setDirections(storage.getDirections());
                     mRecipeDetails.IsData = true;
                     mRecipeDetails.editRecipeId = storage.getId();
                     //mRecipeDetails.recipePos = updatePosition;
                     mRecipeDetails.IsEdit = true;
                     EDIT_MODE = true;
                 }
            }
        }

        if((mRecipeDetails.IsData && mRecipeDetails.IsBack)||mRecipeDetails.IsEdit )
        {
            title.setText(mRecipeDetails.getName());
            category.setText(mRecipeDetails.getCategory());
            serves.setText(mRecipeDetails.getServes());
            totalTime.setText(mRecipeDetails.getTotalTime());
            if(mRecipeDetails.getPictureUrl() != null && !mRecipeDetails.getPictureUrl().isEmpty())
            {
                mFilePath = mRecipeDetails.getPictureUrl();
                image.setImageBitmap(getScaledBitmap(mFilePath, 800, 800));
            }
            mRecipeDetails.IsBack = false;
        }
        if(savedInstanceState!=null)
        {
            mFilePath = savedInstanceState.getString(IMAGE_PATH,"");
            if(!mFilePath.isEmpty())
                image.setImageBitmap(getScaledBitmap(mFilePath, 800, 800));

        }
/*        else {
            String mode = updateIntent.getStringExtra("MODE");
            if(mode!=null && mode.equals("New")){
                title.setText("");
                category.setText("");
                serves.setText("");
                totalTime.setText("");
                image.setImageBitmap(null);
            }
        }*/



    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(IMAGE_PATH, mFilePath);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        List<IngredientDataSet> ds = new ArrayList<>();
        mRecipeDetails.setIngredientList(ds);
        mRecipeDetails.setServes("");
        mRecipeDetails.setTotalTime("");
        mRecipeDetails.setPictureUrl("");
        mRecipeDetails.setDirections("");
        mRecipeDetails.setCategory("");
        mRecipeDetails.setName("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            List<IngredientDataSet> ds = new ArrayList<>();
            mRecipeDetails.setIngredientList(ds);
            mRecipeDetails.setServes("");
            mRecipeDetails.setTotalTime("");
            mRecipeDetails.setPictureUrl("");
            mRecipeDetails.setDirections("");
            mRecipeDetails.setCategory("");
            mRecipeDetails.setName("");
        }

        return super.onOptionsItemSelected(item);
    }

    public void onCameraButtonClick(View view)
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = Uri.fromFile(getOutputMediaFile(MEDIA_TYPE_IMAGE));
        // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        setResult(RESULT_OK, getIntent().putExtra(MediaStore.EXTRA_OUTPUT, fileUri));
        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "@string/app_name");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        }
        else {
            return null;
        }
        return mediaFile;
    }

    public void onGalleryButtonClick(View view)
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
/*                Toast.makeText(this, "Image saved to:\n" +data.getData()
                        , Toast.LENGTH_LONG).show();*/
                ImageView image = (ImageView)findViewById(R.id.recipeImage);

                Bundle Extras= getIntent().getExtras();
                if(Extras!=null) {
                    Uri link = (Uri) Extras.getParcelable(MediaStore.EXTRA_OUTPUT);
                    mFilePath = link.getPath();
                    image.setImageBitmap(getScaledBitmap(link.getPath(), 800, 800));
                    //    setImage(link.getPath(),R.id.recipeImage);
                }

            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            mFilePath = picturePath;
            ImageView image = (ImageView)findViewById(R.id.recipeImage);
            image.setImageBitmap(getScaledBitmap(picturePath, 800, 800));
            //   setImage(picturePath,R.id.recipeImage);
        }
    }
    private Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        sizeOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        sizeOptions.inDither = true;
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
    public void ItemClicked(View view, int position) {

    }
    public void onContinueButtonClick(View view)
    {
        mRecipeDetails.setName(title.getText().toString());
        mRecipeDetails.setCategory(category.getText().toString());
        mRecipeDetails.setServes(serves.getText().toString());
        mRecipeDetails.setTotalTime(totalTime.getText().toString());
        mRecipeDetails.setPictureUrl(mFilePath);
        mRecipeDetails.IsData = true;
        if(EDIT_MODE)
            mRecipeDetails.IsEdit = true;
        Intent intent = new Intent(this, NewRecipeActivityNext.class);
        startActivity(intent);
    }
}
