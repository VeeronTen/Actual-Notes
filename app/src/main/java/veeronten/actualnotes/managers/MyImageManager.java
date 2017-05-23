package veeronten.actualnotes.managers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import veeronten.actualnotes.L;

import static veeronten.actualnotes.managers.FileManager.FileType.IMAGE;
import static veeronten.actualnotes.managers.FileManager.imageRoot;
import static veeronten.actualnotes.managers.FileManager.miniRoot;

public class MyImageManager{

    public static File getBig(String name){
        return new File(imageRoot, name);
    }

    public static Boolean savePhoto(Bitmap bitmapMain){
        File big = FileManager.createNewFile(IMAGE);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(big);
            bitmapMain.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);


            fileOutputStream.flush();
            fileOutputStream.close();
            MyImageManager.matchMini(big);

            return true;
        } catch (IOException e) {
            L.e("IO exception with "+ big.toString(),e);
            return false;
        }
    }

    public static void matchMini(File dad){
        File mini=null;
        try {
            mini = new File(miniRoot,dad.getName());
            mini.createNewFile();
            L.i(mini.toString()+" was created");
        } catch (IOException e) {
            L.e("cant create a new file "+mini.toString(), e);
        }

        try{
            Bitmap bitMini = MyImageManager.decodeSampledBitmapFromResource(dad.getAbsolutePath(),20,20);
            FileOutputStream fileOutputStreamM = new FileOutputStream(mini);
            bitMini.compress(Bitmap.CompressFormat.JPEG,100, fileOutputStreamM);

            fileOutputStreamM.flush();
            fileOutputStreamM.close();
        } catch (FileNotFoundException e) {
            L.e("Cant found the file "+mini.toString(),e);
        } catch (IOException e) {
            L.e("IO exception with "+ mini.toString(),e);
        }
    }

    private static Bitmap decodeSampledBitmapFromResource(String path, int reqWidth, int reqHeight) {
        // Читаем с inJustDecodeBounds=true для определения размеров
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Вычисляем inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Читаем с использованием inSampleSize коэффициента
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static int getFileAngleToRotate(File file){
        int answer = 0;
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(file.getAbsolutePath());
        } catch (IOException e) {
            L.printStackTrace(e);
            return answer;
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        switch(orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90: answer=90; break;
            case ExifInterface.ORIENTATION_ROTATE_180: answer=180; break;

            case ExifInterface.ORIENTATION_ROTATE_270: answer=270; break;
        }
        return answer;
    }
    public static Bitmap rotateBitmap (Bitmap source, float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return  Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),matrix,true);
    }
    public static void rotateImage(File fileToRotate){
        Bitmap sourceBitmap = BitmapFactory.decodeFile(fileToRotate.getAbsolutePath());
        Bitmap newBitmap = rotateBitmap(sourceBitmap, getFileAngleToRotate(fileToRotate));
        try{
            FileOutputStream fOut = new FileOutputStream(fileToRotate);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        }catch (Exception e) {
            L.e("cant rotate file+ "+fileToRotate.getAbsolutePath(),e);
        }
    }
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Реальные размеры изображения
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Вычисляем наибольший inSampleSize, который будет кратным двум
            // и оставит полученные размеры больше, чем требуемые
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

}
