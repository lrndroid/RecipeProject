package com.poorni.project.cooknstore;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.poorni.project.cooknstore.data.ShoppingListDataSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingListNameDialog extends DialogFragment {

    ShoppingListCommunicator shoppingListCommunicator;

    public void setPosition(int position) {
        this.Position = position;
    }

    private int Position = -1;

    public ShoppingListNameDialog() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        shoppingListCommunicator = (ShoppingListCommunicator)activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final List<String> shoppingListName = new ArrayList<>();
        ShoppingListDataSet shoppinglists = ShoppingListDataSet.getInstance();
        for(int i = 0; i <shoppinglists.gShoppingListItems.size();i++ ) {
            shoppingListName.add(shoppinglists.gShoppingListItems.get(i).shoppingListName);
        }
        shoppingListName.add("New List");
        builder.setTitle("Choose Your Preferences");

        builder.setSingleChoiceItems(shoppingListName.toArray(new String[0]), Position, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Position = which;
            }
        });
        builder.setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        builder.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
/*
                if(Position == (shoppingListName.size()-1))
*/
                    shoppingListCommunicator.onShoppingOptionSelected(Position);
            }
        });
        Dialog dialog = builder.create();
        return  dialog;
    }



    public interface ShoppingListCommunicator
    {
        public void onShoppingOptionSelected(int option);
    }
}
