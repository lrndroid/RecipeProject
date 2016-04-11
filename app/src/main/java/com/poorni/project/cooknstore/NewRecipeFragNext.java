package com.poorni.project.cooknstore;



import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;


import com.poorni.project.cooknstore.data.IngredientDataSet;
import com.poorni.project.cooknstore.data.RecipeDetailDataSet;
import com.poorni.project.cooknstore.network.RecipeOperations;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewRecipeFragNext extends Fragment implements  RecipeAdapter.ClickListener {
    private RecyclerView recyclerView;
    private String mUnitIngredient;
    private String mIngredient;
    private String mQuantity;

    private int mUnitPosition;
    Spinner volumeSpinner;
    EditText ingredientEditText;
    EditText quantityEditText;
    ArrayList<IngredientDataSet> mRecipeDataSet;
    RecipeAdapter recipeAdapter;
    Button addButton;
    Button updateButton;
    Button deleteButton;
    Button saveButton;
    int selectedPosition;

    EditText directions;
    RecipeDetailDataSet mRecipeDetail;

    public NewRecipeFragNext() {
        // Required empty public constructor
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

    //    outState.putParcelableArrayList("DataSet", (ArrayList) mRecipeDataSet);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("DataSet",(ArrayList)mRecipeDataSet);
        outState.putBundle("Data",bundle);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_recipe_next, container, false);
        mRecipeDetail = RecipeDetailDataSet.getInstance();
        if(mRecipeDetail.IsEdit)
            mRecipeDataSet = (ArrayList)mRecipeDetail.getIngredientList();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        volumeSpinner = (Spinner)getActivity().findViewById(R.id.unitsVolumeSpinner);
        volumeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mUnitIngredient = parent.getItemAtPosition(position).toString();
                mUnitPosition = position;
                //  Toast.makeText(getBaseContext(),"Selected dropdown item "+(parent.getItemAtPosition(position).toString()),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        directions = (EditText) getActivity().findViewById(R.id.directionsEditText);
        directions.setText(mRecipeDetail.getDirections());


        saveButton = (Button)getActivity().findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecipeDetail.setIngredientList(mRecipeDataSet);
                mRecipeDetail.setDirections(directions.getText().toString());
                if(mRecipeDetail.editRecipeId != -1)
                    updateRecipe();
                else
                    addRecipe();


            }
        });


        //initialize recycler view adapter
        RecyclerView recyclerView = (RecyclerView)getActivity().findViewById(R.id.recyclerView);
        if((savedInstanceState != null) && savedInstanceState.containsKey("Data"))
        {
            Bundle bundle =savedInstanceState.getBundle("Data");
            mRecipeDataSet = bundle.getParcelableArrayList("DataSet");
            //mRecipeDataSet = savedInstanceState.getParcelableArrayList("DateSet");

        }
        else if(mRecipeDataSet == null){
            mRecipeDataSet = (ArrayList)mRecipeDetail.getIngredientList();
          //  mRecipeDataSet.add(new IngredientDataSet("Salt", "1", "tbsp", 2));
        }
        recipeAdapter = new RecipeAdapter(getActivity().getBaseContext(),mRecipeDataSet);

        recyclerView.setAdapter(recipeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recipeAdapter.setClickListener(this);

        ingredientEditText = (EditText)getActivity().findViewById(R.id.ingredientEditText);
        quantityEditText = (EditText)getActivity().findViewById(R.id.quantityEditText);

        addButton = (Button)getActivity().findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ingredientEditText.getText().toString().isEmpty())
                {
                    Toast.makeText(getContext(), "Enter a ingredient", Toast.LENGTH_LONG).show();
                }
                else {
                    mIngredient = ingredientEditText.getText().toString();
                    mQuantity = quantityEditText.getText().toString();
                    mRecipeDataSet.add(new IngredientDataSet(mIngredient, mQuantity, mUnitIngredient,mUnitPosition));
                    recipeAdapter.notifyDataSetChanged();
                    quantityEditText.setText("");
                    ingredientEditText.setText("");
                    volumeSpinner.setSelection(0);
                    mRecipeDetail.setIngredientList(mRecipeDataSet);
                }
            }
        });
        deleteButton = (Button) getActivity().findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedPosition!= -1) {
                    mRecipeDataSet.remove(selectedPosition);
                    recipeAdapter.notifyItemRemoved(selectedPosition);
                    mRecipeDetail.setIngredientList(mRecipeDataSet);
                    selectedPosition = -1;
                }
                else
                    Toast.makeText(getContext(),"Select n item to delete",Toast.LENGTH_SHORT).show();
            }
        });
        updateButton = (Button) getActivity().findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition != -1 && updateButton.getText().toString().equals("Edit")) {
                    updateButton.setText(R.string.update);
                    ingredientEditText.setText(mRecipeDataSet.get(selectedPosition).getIngredientName());
                    quantityEditText.setText(mRecipeDataSet.get(selectedPosition).getIngredientQuantity());
                    volumeSpinner.setSelection(mRecipeDataSet.get(selectedPosition).getUnitPosition());
                } else if (selectedPosition != -1 && updateButton.getText().toString().equals("Update")) {
                    mIngredient = ingredientEditText.getText().toString();
                    mQuantity = quantityEditText.getText().toString();
                    mRecipeDataSet.remove(selectedPosition);
                    mRecipeDataSet.add(selectedPosition, new IngredientDataSet(mIngredient, mQuantity, mUnitIngredient, mUnitPosition));
                    updateButton.setText(R.string.edit);
                    selectedPosition =-1;
                    quantityEditText.setText("");
                    ingredientEditText.setText("");
                    volumeSpinner.setSelection(0);
                    recipeAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    private RadioButton radioButton;
    @Override
    public void itemClicked(View view, int position) {
        selectedPosition = position;
       //  radioButton = (RadioButton)view.findViewById(R.id.radioButton);
    }

    public void addRecipe(){
        RecipeOperations operations = new RecipeOperations(getContext());
        long recipeId =-1;
        try {
            operations.open();
            recipeId = operations.insertData();
            operations.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
/*        if(RecipeStorage.gStorageList == null)
        {
            RecipeStorage.gStorageList = new ArrayList<RecipeStorage>();
        }
        if(recipeId != -1) {
            RecipeStorage.gStorageList.add(new RecipeStorage(recipeId, mRecipeDetail));
        }*/
        startActivity(new Intent(getContext(), CookHome.class));
    }
    public void updateRecipe(){
        RecipeOperations operations = new RecipeOperations(getContext());
        long recipeId =-1;
        try{
            operations.open();
            recipeId = operations.updateRecipe(mRecipeDetail.editRecipeId);
            operations.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
 /*       if(RecipeStorage.gStorageList == null)
        {
            RecipeStorage.gStorageList = new ArrayList<RecipeStorage>();
        } */
        if(recipeId != -1) {
          /*  Iterator<RecipeStorage> itr = RecipeStorage.gStorageList.iterator();
            while(itr.hasNext())
            {
                RecipeStorage storage = itr.next();
                if(storage.getId() == recipeId) {
                    itr.remove();
                }

            }*/
            //RecipeStorage.gStorageList.remove(mRecipeDetail.recipePos);
           // RecipeStorage.gStorageList.add(new RecipeStorage(recipeId, mRecipeDetail));
            List<IngredientDataSet> ds = new ArrayList<>();
            mRecipeDetail.setIngredientList(ds);
            mRecipeDetail.setServes("");
            mRecipeDetail.setTotalTime("");
            mRecipeDetail.setPictureUrl("");
            mRecipeDetail.setDirections("");
            mRecipeDetail.setCategory("");
            mRecipeDetail.setName("");
        }
        startActivity(new Intent(getContext(), CookHome.class));
    }
}
