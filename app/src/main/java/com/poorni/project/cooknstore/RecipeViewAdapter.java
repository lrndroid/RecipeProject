package com.poorni.project.cooknstore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

/**
 * Created by Harini on 3/13/16.
 */
public class RecipeViewAdapter extends RecyclerView.Adapter<RecipeViewAdapter.RecipeViewHolder> {
    private LayoutInflater inflater;
    List<RecipeStorage> data = Collections.EMPTY_LIST;
    private Context context;
    RecipeViewListener clickListener;
    RecyclerView.LayoutParams mImageViewLayoutParams;


    public RecipeViewAdapter(Context context,List<RecipeStorage> list){
        inflater = inflater.from(context);
        this.context = context;
        this.data = list;
        mImageViewLayoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    }
    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View view = inflater.inflate(R.layout.recipe_cards,parent,false);
        RecipeViewHolder rvh = new RecipeViewHolder(view);
        return rvh;
    }

    public void setListener(RecipeViewListener listener)
    {
        clickListener = listener;
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        String title = data.get(position).getName();
        String image = data.get(position).getPictureUrl();
        String detail = data.get(position).getDirections();
        holder.titleText.setText(title);
        holder.detailsText.setText(detail);
        if(!image.isEmpty())
            holder.recipeImage.setImageBitmap(getScaledBitmap(image, 100, 100));
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
    public int getItemCount() {
        return data.size();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView titleText;
        TextView detailsText;
        ImageView recipeImage;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.titleText);
            detailsText = (TextView) itemView.findViewById(R.id.detailsText);
            recipeImage = (ImageView) itemView.findViewById(R.id.recipeImage);
            recipeImage.setScaleType(ImageView.ScaleType.CENTER_CROP);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.itemClicked(v,data.get(getAdapterPosition()).getId());
        }
    }
    public interface RecipeViewListener{
        public void itemClicked(View view,long position);
    }
}
