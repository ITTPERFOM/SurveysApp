package com.timetracker.business;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.StrictMode;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageMethods {

    public static Bitmap CreateBitmap(String path)
    {
        try {
            Bitmap bm = decodeFile(path);
            DeleteImageFile(path);
            CreateImageFile(bm,path);
            return bm;
        }catch(Exception ex) {
            String hola1 = ex.toString();
            return null;
        }
    }

    private static Bitmap decodeFile(String path) {
        int orientation;
        try {
            if (path == null) {
                return null;
            }
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = calculateInSampleSize(o, 280, 380);
            o.inJustDecodeBounds = false;
            Bitmap bm = BitmapFactory.decodeFile(path, o);
            ExifInterface exif = new ExifInterface(path);
            File fdelete = new File(path);
            if (fdelete.exists()) {
                fdelete.delete();
            }
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix m = new Matrix();
            if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
                m.postRotate(180);
                return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(), m, true);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                m.postRotate(90);
                return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(), m, true);
            }
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                m.postRotate(270);
                return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(), m, true);
            }
            return bm;
        } catch (Exception e) {
            return null;
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            while (height > reqHeight && width  > reqWidth) {
                height = height / 2;
                width = width / 2;
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    public static void DeleteImageFile(String path)
    {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        File fdelete = new File(path);
        if (fdelete.exists()) {
            fdelete.delete();
        }
    }

    public static void CreateImageFile(Bitmap bitmap,String path)
    {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
