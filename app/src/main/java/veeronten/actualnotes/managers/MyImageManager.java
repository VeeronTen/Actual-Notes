package veeronten.actualnotes.managers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import veeronten.actualnotes.L;

import static veeronten.actualnotes.managers.FileManager.FileType.IMAGE;
import static veeronten.actualnotes.managers.FileManager.imageRoot;

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

    private static void matchMini(File dad){
        File mini = new File(FileManager.miniRoot, dad.getName());
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
