package com.poorni.project.cooknstore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

/**
 * Created by Harini on 3/17/16.
 */
public class ShoppingItemDialog extends DialogFragment {

    public  String mHeader;
    public String mMessage;
    boolean boolPreferences = false;
    ShoppingItemDialogListener shoppingItemListener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mHeader);
        builder.setMessage(mMessage);
        final CheckBox prefCheck = new CheckBox(getActivity());
        prefCheck.setText("don't check again");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        prefCheck.setLayoutParams(params);
        if(!boolPreferences)
            builder.setView(prefCheck);
        prefCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolPreferences = isChecked;
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

            }
        });
        Dialog dialog = builder.create();
        return dialog;
    }

    public ShoppingItemDialog()
    {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        shoppingItemListener = (ShoppingItemDialogListener)activity;
    }
    public interface ShoppingItemDialogListener{
        public void onItemClicked(String itemname);
    }
}
