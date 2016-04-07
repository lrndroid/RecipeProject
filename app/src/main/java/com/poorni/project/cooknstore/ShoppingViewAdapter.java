package com.poorni.project.cooknstore;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import com.poorni.project.cooknstore.data.ShoppingListDataSet;

/**
 * Created by Harini on 3/16/16.
 */
public class ShoppingViewAdapter extends RecyclerView.Adapter<ShoppingViewAdapter.ShoppingViewHolder> {
    private LayoutInflater inflater;
    Context mContext;
    ShoppingListDataSet mShoppingListStorage;
    ShoppingClickListener clickListener;
    List<ShoppingListDataSet.ShoppingListItems> data;
    public ShoppingViewAdapter(Context context)
    {
        inflater = inflater.from(context);
        mContext = context;
        mShoppingListStorage = ShoppingListDataSet.getInstance();
        data =  mShoppingListStorage.gShoppingListItems;
    }

    public void setListener(ShoppingClickListener listener)
    {
        clickListener = listener;
    }

    @Override
    public ShoppingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.shopping_list,parent,false);
        ShoppingViewHolder svh = new ShoppingViewHolder(view);
        return  svh;
    }

    private int selectedPos = 0;

    @Override
    public void onBindViewHolder(ShoppingViewHolder holder, int position) {

        holder.textView.setText(data.get(position).shoppingListName);
        holder.itemTextView.setText(String.valueOf(data.get(position).items.size()));
        //holder.itemTextView.setText("0");
        holder.itemView.setSelected(selectedPos == position);

    }

    @Override
    public int getItemCount() {
       return   data.size();
    }

    public class ShoppingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textView;
        TextView itemTextView;
        //ImageView editImage;
      //  ImageView clearImage;
        public ShoppingViewHolder(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.shoppingListName);
            itemTextView = (TextView)itemView.findViewById(R.id.itemsCount);
        //    editImage = (ImageView)itemView.findViewById(R.id.editImage);

    //        clearImage = (ImageView)itemView.findViewById(R.id.deleteImage);
      //      editImage.setOnClickListener(this);
            itemView.setOnClickListener(this);
    //        clearImage.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if(clickListener!=null)
                clickListener.onShoppingListEditClicked(getAdapterPosition());
/*            if(v.getId()== R.id.editImage){
                if(clickListener!= null)
                    clickListener.onShoppingListEditClicked(getAdapterPosition());
            }*/
 /*           if(v.getId() == R.id.deleteImage){

                if(clickListener!= null)
                    clickListener.onShoppingListdeleteClicked(getAdapterPosition());

            }*/
            notifyItemChanged(selectedPos);
            selectedPos = getLayoutPosition();
            notifyItemChanged(selectedPos);
        }
    }
    public interface ShoppingClickListener{
        public void onShoppingListEditClicked(int position);
       // public void onShoppingListdeleteClicked(int position);
    }

}
