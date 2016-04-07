package com.poorni.project.cooknstore;



import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewRecipeFrag extends Fragment {

    NewRecipeFirstInterface firstListener;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static int RESULT_LOAD_IMAGE = 1;
    public String mFilePath;
    public String IMAGE_PATH = "path";



    public NewRecipeFrag() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_recipe, container, false);
        Button galleryButton = (Button)getActivity().findViewById(R.id.galleryButton);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(IMAGE_PATH, mFilePath);
    }

    public void setFirstListener(NewRecipeFirstInterface firstListener)
    {
        this.firstListener = firstListener;
    }
    public void onContinueButtonClick(View view)
    {

    }
    public interface NewRecipeFirstInterface{
        public void ItemClicked(View view, int position);
    }
}
