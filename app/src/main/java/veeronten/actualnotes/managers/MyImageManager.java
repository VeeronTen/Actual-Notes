package veeronten.actualnotes.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MyImageManager {
    private Context context;
    private File imageRoot;
    private File mimiRoot;

    public MyImageManager(Context c){
        context = c;
        imageRoot = new File(context.getFilesDir(), "image");
        imageRoot.mkdirs();
        mimiRoot = new File(context.getFilesDir(), "mimi");
        mimiRoot.mkdirs();
    }
    public File createNewImageFile(){
        Calendar cal = new GregorianCalendar();
        String newName = cal.get(Calendar.YEAR)+"-"+
                cal.get(Calendar.MONTH)+"-"+
                cal.get(Calendar.DATE)+":"+
                cal.get(Calendar.HOUR_OF_DAY)+"-"+
                cal.get(Calendar.MINUTE)+"-"+
                cal.get(Calendar.SECOND)+"-i";
        try {

            File answer = new File(imageRoot, newName);
            answer.createNewFile();
            return answer;
        } catch (IOException e) {
            Log.d("MyLog","cant create new file");
            return null;
        }
    }
    public void removeImageFile(File fileToRemove){
        fileToRemove.delete();
        String name = fileToRemove.getName();
    }

    public File createNewMini(File dad){
        try{
            File mini = new File(mimiRoot,dad.getName());
            mini.createNewFile();

            Bitmap bitMini = MyImageManager.decodeSampledBitmapFromResource(dad.getAbsolutePath(),20,20);
            FileOutputStream fileOutputStreamM = new FileOutputStream(mini);
            bitMini.compress(Bitmap.CompressFormat.JPEG,100, fileOutputStreamM);

            fileOutputStreamM.flush();
            fileOutputStreamM.close();

            return mini;
        } catch (Exception e) {
            Log.d("MyLog","cant create new file");
            return null;
        }
    }
    public void removeMini(File fileToRemove){
        fileToRemove.delete();
        removeImageFile(new File(imageRoot, fileToRemove.getName()));
    }

    public ArrayList<File> getMinis(){
        ArrayList<File> answer = new ArrayList<>();
        for(File f : mimiRoot.listFiles())
            answer.add(f);
        return  answer;
    }


    public File getBig(String name){
        return new File(imageRoot, name);
    }

    public static Bitmap decodeSampledBitmapFromResource(String path, int reqWidth, int reqHeight) {
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
