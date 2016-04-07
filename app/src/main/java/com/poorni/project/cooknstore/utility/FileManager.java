package com.poorni.project.cooknstore.utility;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Harini on 3/23/16.
 */
public class FileManager {
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int RECIPE_TYPE_BACKUP = 2;

    public static File getOutputMediaFile(int type,Context context){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        String appPath = context.getApplicationContext().getFilesDir().getAbsolutePath();

        //File storageDir = new File(appPath+"/backup");
        File storageDir = new File(Environment.getExternalStorageDirectory(), "CooknStore");
      //  File storageDir = new File(Environment.getDataDirectory(),"data/com.simbiosys.avatar.cooknstore/files/backup/xml");

        // /data/data/com.simbiosys.avatar.cooknstore/files
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! storageDir.exists()){
            if (! storageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File newFile;
        if (type == MEDIA_TYPE_IMAGE){
            newFile = new File(storageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        }
        else if(type== RECIPE_TYPE_BACKUP){
            newFile = new File(storageDir.getPath()+File.separator+"backup_"+timeStamp+".xml");
        }
        else {
            return null;
        }
        return newFile;
    }

}
