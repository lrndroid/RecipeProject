package com.poorni.project.cooknstore;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.poorni.project.cooknstore.data.ShoppingListDataSet;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Harini on 3/17/16.
 */
public class ShoppingListItemAdapter extends RecyclerView.Adapter<ShoppingListItemAdapter.ShoppingListViewHolder>{

    ShoppingListItemListener itemListener;
    Context mContext;
    LayoutInflater inflater;
    List<String> data;

    public ShoppingListItemAdapter(Context context, List<String> data)
    {
        mContext = context;
        this.data = data;
        inflater = inflater.from(context);
    }

    @Override
    public ShoppingListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.shoppint_list_items,parent,false);
        ShoppingListViewHolder slvh = new ShoppingListViewHolder(view);
        return slvh;
    }

    private int selectedPos = 0;

    @Override
    public void onBindViewHolder(ShoppingListViewHolder holder, int position) {
        holder.textView.setText(data.get(position));
        holder.itemView.setSelected(selectedPos == position);

    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setItemListener(ShoppingListItemListener listener)
    {
        itemListener=listener;
    }
    public class ShoppingListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView textView;
        ImageView editImage;
    //    ImageView clearImage;
        public ShoppingListViewHolder(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.shoppingListName);
            editImage = (ImageView)itemView.findViewById(R.id.editImage);

       //     clearImage = (ImageView)itemView.findViewById(R.id.deleteImage);
            editImage.setOnClickListener(this);
       //     clearImage.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if(v.getId()== R.id.editImage){
                if(itemListener!= null)
                    itemListener.OnItemEditClicked(getAdapterPosition(),textView.getText().toString());
            }
    /*        if(v.getId() == R.id.deleteImage){
                if(itemListener != null)
                    itemListener.OnItemDeleteClicked(getAdapterPosition());
            }*/
            notifyItemChanged(selectedPos);
            selectedPos = getLayoutPosition();
            notifyItemChanged(selectedPos);

        }
    }
    public interface ShoppingListItemListener{
        public void OnItemEditClicked(int position, String itemName);
  //      public void OnItemDeleteClicked(int position);
    }
}
