package com.poorni.project.cooknstore;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.poorni.project.cooknstore.data.IngredientDataSet;

import java.util.Collections;
import java.util.List;

/**
 * Created by Harini on 3/7/16.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeHolder>  {

    private LayoutInflater inflater;
    List<IngredientDataSet> data = Collections.EMPTY_LIST;
    private Context context;
    ClickListener mListener;



    public RecipeAdapter(Context context, List<IngredientDataSet> data) {
        inflater = inflater.from(context);
        this.context = context;
        this.data = data;
    }

    @Override
    public RecipeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.ingredients_list, parent, false);
        RecipeHolder recipeHolder = new RecipeHolder(view);
        return recipeHolder;
    }
    public void setClickListener(ClickListener listener)
    {
        this.mListener = listener;
    }

    private int selectedPos = 0;
    @Override
    public void onBindViewHolder(RecipeHolder holder, int position) {
        String ingredient = data.get(position).getIngredientName();
        String quantity = data.get(position).getIngredientQuantity();
        String unit = data.get(position).getIngredientUnit();
        if(data.get(position).getUnitPosition() == 0) {
            holder.ingredientTextView.setText(ingredient + " " + quantity);
        }
        else
            holder.ingredientTextView.setText(ingredient + " " + quantity + " " + unit);

   //     holder.radio.setChecked(false);
        holder.itemView.setSelected(selectedPos == position);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class RecipeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView ingredientTextView;
        RadioButton radio;
        public RecipeHolder(View itemView) {
            super(itemView);
            ingredientTextView = (TextView) itemView.findViewById(R.id.textView);
/*
            radio = (RadioButton) itemView.findViewById(R.id.radioButton);
*/
            itemView.setOnClickListener(this);
/*
            radio.setOnClickListener(this);
*/

        }

        @Override
        public void onClick(View v) {
            Toast.makeText(context, "Item Click at " + getPosition(), Toast.LENGTH_SHORT).show();

            // deleteItem(getPosition());
            if (mListener != null) {
                mListener.itemClicked(v, getPosition());
            }
            notifyItemChanged(selectedPos);
            selectedPos = getLayoutPosition();
            notifyItemChanged(selectedPos);
        }

    }
    public interface ClickListener{
        public void itemClicked(View view, int position);
    }
}