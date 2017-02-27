package any.audio.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import any.audio.R;
import any.audio.SharedPreferences.SharedPrefrenceUtils;

/**
 * Created by Ankit on 2/10/2017.
 */

public class MetaDataHelper {

    private static Context context;
    private static MetaDataHelper mInstance;
    private SharedPrefrenceUtils utils;

    public MetaDataHelper(Context context) {

        this.context = context;
        utils = SharedPrefrenceUtils.getInstance(context);

    }

    public static MetaDataHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MetaDataHelper(context);
        }
        return mInstance;
    }

    public void storeImage(Bitmap image , String fileName) {

        File pictureFile = getOutputMediaFile(fileName);

        if (pictureFile == null) {
            Log.d("Metadata",
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("Metadata", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("Metadata", "Error accessing file: " + e.getMessage());
        }
    }

    public Bitmap getBitmap(String fileName){

        Bitmap bitmap = null;
        try {
            FileInputStream fis = new FileInputStream(getOutputMediaFile(fileName));
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(bitmap==null){
            bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.hqdefault);
        }

        return bitmap;

    }

    /** Create a File for saving an image or video */
    private  File getOutputMediaFile(String downloaded_id){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }

        // Create a media file name
        File mediaFile;
        String mImageName="MI_"+downloaded_id+".png";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;

    }

    public void setDuration(String fileName , String duration){
       utils.setMetadataDuration(fileName,duration);
    }

    public String getDuration(String fileName){

        return utils.getMetadataDuration(fileName);
    }

    public void setArtist(String filename,String artist){
        utils.setMetadataArtist(filename, artist);
    }

    public String getArtist(String filename){

        return utils.getMetadataArtist(filename);
    }


}
