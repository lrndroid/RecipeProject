package com.poorni.project.cooknstore;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.poorni.project.cooknstore.data.ShoppingListDataSet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ShoppingListView extends AppCompatActivity implements NewShoppingListDialog.NewShoppingListInterface,ShoppingViewAdapter.ShoppingClickListener {

    ShoppingListDataSet shoppingLists;
    ShoppingViewAdapter shoppingViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        shoppingLists = ShoppingListDataSet.getInstance();

        RecyclerView view = (RecyclerView)findViewById(R.id.shoppingRecyclerView);
        shoppingViewAdapter = new ShoppingViewAdapter(getBaseContext());
        shoppingViewAdapter.setListener(this);
        view.setAdapter(shoppingViewAdapter);
        view.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                ShoppingListDataSet.getInstance().gShoppingListItems.remove(position);
                //shoppingAdapter.data.remove(position);
                shoppingViewAdapter.notifyDataSetChanged();
                try {

                    FileOutputStream fos = openFileOutput("cooknstore", MODE_PRIVATE);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(ShoppingListDataSet.getInstance());
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_shopping_list,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.add_shopping_list) {
            NewShoppingListDialog dialog =new NewShoppingListDialog();
            dialog.show(getFragmentManager(),"New Shopping List");
            Toast.makeText(this, "Shopping List", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNewShoppingList(String shoppingListName) {
        Intent intent = new Intent(this,NewShoppingListActivity.class);
        intent.putExtra("SHOPPING_LIST_NAME",shoppingListName);
        startActivity(intent);
    }

    @Override
    public void onShoppingListEditClicked(int position) {
        Intent intent = new Intent(this,NewShoppingListActivity.class);
        intent.putExtra("SHOPPING_LIST_POSITION",position);
        startActivity(intent);    }

/*    @Override

    public void onShoppingListdeleteClicked(int position) {
        final int titlePosition = position;
        AlertDialog alertDialog = new AlertDialog.Builder(ShoppingListView.this).create();
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
                        shoppingViewAdapter.data.remove(titlePosition);
                        //shoppingAdapter.data.remove(position);
                        shoppingViewAdapter.notifyDataSetChanged();
                    }
                });
        alertDialog.show();
    }
*/

}
