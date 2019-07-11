package com.fudicia.usupply1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.support.design.widget.TabLayout;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageManager {

    public static Bitmap getBitmap(String imgUrl){
        File imagefile = new File(imgUrl);
        FileInputStream fis = null;
        Bitmap bitmap = null;

        try{
            fis = new FileInputStream(imagefile);
            bitmap = BitmapFactory.decodeStream(fis);
        }catch(FileNotFoundException e){

        }finally {
            try{
                fis.close();
            }
            catch(IOException e){

            }
        }
        return bitmap;
    }

    public static byte[] getBytesFromBitmap(Bitmap bm, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();

    }

}
