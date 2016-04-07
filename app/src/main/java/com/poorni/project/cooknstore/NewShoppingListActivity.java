package com.poorni.project.cooknstore;


import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.poorni.project.cooknstore.data.ShoppingListDataSet;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewShoppingListActivity extends AppCompatActivity implements ShoppingListItemAdapter.ShoppingListItemListener {

    ShoppingListDataSet shoppingListDataSet;
    List<ShoppingListDataSet.ShoppingListItems> listItem;
    List<String> data;
    String mode;
    boolean updateItem;
    EditText itemText;
    String listName;

    // position of shopping list to be updated
    int positionUpdate;

    //position of item to be updated
    int itemPosition;
    ShoppingListItemAdapter shoppingAdapter;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            if(mode == "NEW")
                shoppingListDataSet.addItem(data,listName);
            else if(mode == "UPDATE")
                shoppingListDataSet.gShoppingListItems.get(positionUpdate).items = data;
            updateShoppingList();
        }
        if(item.getItemId() == R.id.share_shopping_list){
            shareShoppingList();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.share,menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_shopping_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        shoppingListDataSet = ShoppingListDataSet.getInstance();
        listItem = shoppingListDataSet.gShoppingListItems;

        mode = "UPDATE";
        data = Collections.EMPTY_LIST;
        listName = getIntent().getStringExtra("SHOPPING_LIST_NAME");
        positionUpdate = getIntent().getIntExtra("SHOPPING_LIST_POSITION",-1);
        if(positionUpdate!=-1) {
            if(data.isEmpty())
                data = new ArrayList<>();
            data = listItem.get(positionUpdate).items;
            listName = listItem.get(positionUpdate).shoppingListName;
        }
        TextView actionbarTitle = (TextView)findViewById(R.id.toolbar_title);
        actionbarTitle.setText(listName);

/*        TextView txt = (TextView)findViewById(R.id.nameText);
        txt.setText(listName);*/

        if(data.size() ==0)
        {
            mode = "NEW";
        }
        RecyclerView shoppingListItemView  = (RecyclerView)findViewById(R.id.listItemsRecyclerView);

        shoppingAdapter = new ShoppingListItemAdapter(getBaseContext(),data);
        shoppingListItemView.setAdapter(shoppingAdapter);
        shoppingListItemView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        shoppingAdapter.setItemListener(this);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                data.remove(position);
                //shoppingAdapter.data.remove(position);
                shoppingAdapter.notifyDataSetChanged();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(shoppingListItemView);
        itemText = (EditText)findViewById(R.id.itemEditText);
    }

    @Override
    public void OnItemEditClicked(int position, String itemName) {
        itemText.setText(itemName);
        updateItem = true;
        itemPosition = position;
    }

/*    @Override
    public void OnItemDeleteClicked(final int position) {
        AlertDialog alertDialog = new AlertDialog.Builder(NewShoppingListActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Do you really want to delete this shopping list?");
        alertDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.setButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        data.remove(position);
                        //shoppingAdapter.data.remove(position);
                        shoppingAdapter.notifyDataSetChanged();
                    }
                });
        alertDialog.show();
    }*/

    public void onAddButtonClick(View view)
    {
        if(data.isEmpty())
            data = new ArrayList<>();
        if(updateItem) {
            data.remove(itemPosition);
            updateItem = false;
            data.add(itemPosition,itemText.getText().toString());
        }
        else {
            data.add(itemText.getText().toString());
        }
        itemText.setText("");
        shoppingAdapter.data = data;
        shoppingAdapter.notifyDataSetChanged();
    }

    public void updateShoppingList(){
        try {
            FileOutputStream fos = this.openFileOutput("cooknstore", MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(shoppingListDataSet);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void onSave(View view)
    {
        if(mode == "NEW")
            shoppingListDataSet.addItem(data,listName);
        else
            shoppingListDataSet.gShoppingListItems.get(positionUpdate).items = data;
        try {
            FileOutputStream fos = this.openFileOutput("cooknstore", MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(shoppingListDataSet);
        }catch (IOException e){
            e.printStackTrace();
        }
/*        try {
            FileOutputStream fos = this.openFileOutput("cooknstore.txt", MODE_PRIVATE);
            final OutputStreamWriter osw = new OutputStreamWriter(fos);
            JSONArray array = new JSONArray();
            array.put(shoppingListDataSet.gShoppingListItems);
            // Add your objects to the array

            String tofile = array.toString();
            osw.write(tofile);

            osw.flush();
            osw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }
    public void shareShoppingList()
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);

        String share = "";
        for(int i=0;i<data.size();i++)
            share = share + data.get(i)+"\n";

        sendIntent.putExtra(Intent.EXTRA_TEXT,share);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}
