package com.poorni.project.cooknstore;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Created by Harini on 3/16/16.
 */
public class NewShoppingListDialog extends DialogFragment {

    NewShoppingListInterface shoppingListInterface;


    public NewShoppingListDialog(){super();}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        shoppingListInterface = (NewShoppingListInterface)activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Create a new Shopping List");
        builder.setMessage("Enter name of your shopping list");
        final EditText shoppingText = new EditText(getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        shoppingText.setLayoutParams(layoutParams);
        builder.setView(shoppingText);
        builder.setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        builder.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = shoppingText.getText().toString();
                shoppingListInterface.onNewShoppingList(name);
            }
        });
        Dialog dialog = builder.create();
        return dialog;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    public interface NewShoppingListInterface{
        public void onNewShoppingList(String shoppingListName);
    }
}
