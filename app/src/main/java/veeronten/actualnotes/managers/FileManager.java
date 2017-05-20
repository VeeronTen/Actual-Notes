package veeronten.actualnotes.managers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import veeronten.actualnotes.L;

public class FileManager {
    public enum FileType{TEXT, IMAGE, AUDIO, MINI}

    public static File textRoot;
    public static File audioRoot;
    public static File imageRoot;
    public static File miniRoot;

    private static Context context;

    public FileManager(){}
    public static void start(Context lContext){
        if (context!=null)
            return;
        context = lContext;
        if(MyNotificationManager.getFastAccessStatus())
            MyNotificationManager.sendFastAccessNotification();
        textRoot = new File(context.getFilesDir(), "text");
        textRoot.mkdirs();
        imageRoot = new File(context.getFilesDir(), "image");
        imageRoot.mkdirs();
        miniRoot = new File(context.getFilesDir(), "mini");
        miniRoot.mkdirs();
        audioRoot = new File(context.getFilesDir(), "audio");
        audioRoot.mkdirs();

    }
    public static Context getContext(){
        return context;
    }

    public static File createNewFile(FileType fileType){

        String newName = FileManager.generateFilenamePrefix()+'-'+getPostfixByType(fileType);
        File newFile = new File(getRootByType(fileType), newName);
        try {
            newFile.createNewFile();
            L.i(newFile.toString()+" was created");

            return newFile;
        } catch (IOException e) {
            L.e("cant create a new file "+newFile.toString(), e);
            return null;
        }
    }
    public static void removeFile(File fileToRemove){
        if(FileManager.typeOf(fileToRemove)==FileType.IMAGE) {
            new File(imageRoot, fileToRemove.getName()).delete();
            new File(miniRoot, fileToRemove.getName()).delete();
            L.i(new File(imageRoot, fileToRemove.getName())+" was deleted");
            L.i(new File(miniRoot, fileToRemove.getName())+" was deleted");
        }else {
            fileToRemove.delete();
            L.i(fileToRemove+" was deleted");
        }
        return;
    }

    public static boolean deleteLastFromDCIM() {
        boolean success = false;
        GregorianCalendar now = new GregorianCalendar();
        try {
            //Samsungs:
            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "DCIM/Camera/");
            L.d(folder.toString());
            if(!folder.exists()){ //other phones:
                File[] subfolders = new File(Environment.getExternalStorageDirectory() + File.separator + "DCIM").listFiles();
                for(File subfolder : subfolders){
                    if(subfolder.getAbsolutePath().contains("100")){
                        folder = subfolder;
                        break;
                    }
                }
                if(!folder.exists())
                    return false;
            }

            File[] images = folder.listFiles();
            File latestSavedImage = images[0];
            for (int i = 1; i < images.length; ++i) {
                if (images[i].lastModified() > latestSavedImage.lastModified()) {
                    latestSavedImage = images[i];
                }
            }
            long difference=now.getTimeInMillis()-latestSavedImage.lastModified();
            if(difference<6000&&difference>-1) {
                //context.getContentResolver().delete(Uri.fromFile(latestSavedImage), null,null);
                success = latestSavedImage.delete();

//                I've seen a lot of answers suggesting the use of
//
//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" +  Environment.getExternalStorageDirectory())));
//                This works but causes the Media Scanner to re-scan the media on the device. A more efficient approach would be to query/delete via the Media Store content provider:
                String[] projection = { MediaStore.Images.Media._ID };

// Match on the file path
                String selection = MediaStore.Images.Media.DATA + " = ?";
                String[] selectionArgs = new String[] { latestSavedImage.getAbsolutePath() };

// Query for the ID of the media matching the file path
                Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = context.getContentResolver();
                Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
                if (c.moveToFirst()) {
                    // We found the ID. Deleting the item via the content provider will also remove the file
                    long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                    Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    contentResolver.delete(deleteUri, null, null);
                } else {
                    // File not found in media store DB
                }
                c.close();

            }
            return success;
        } catch (Exception e) {
            e.printStackTrace();
            return success;
        }

    }

    public ArrayList<File> getFiles(){
        ArrayList<File> answer = new ArrayList<>();

        answer.addAll(getFiles(FileType.TEXT));
        answer.addAll(getFiles(FileType.AUDIO));
        answer.addAll(getFiles(FileType.IMAGE));
        return answer;
    }
    public static ArrayList<File> getFiles(FileType fileType){
        ArrayList<File> answer = new ArrayList<>();
        for(File f : getRootByType(fileType).listFiles())
            answer.add(f);
        return  answer;
    }

    public static int countOfFiles(){
        return countOfFiles(FileType.TEXT)+countOfFiles(FileType.AUDIO)+countOfFiles(FileType.IMAGE);
    }
    public static int countOfFiles(FileType fileType){
        return getRootByType(fileType).listFiles().length;
    }

    public static String ageOf(File f){
        String answer;

        long count;
        String name = f.getName();
        String[] mas = name.split("-|:");

        Calendar now = new GregorianCalendar();
        Calendar fileCal = new GregorianCalendar(Integer.valueOf(mas[0]),Integer.valueOf(mas[1]),
                Integer.valueOf(mas[2]),Integer.valueOf(mas[3]),Integer.valueOf(mas[4]),Integer.valueOf(mas[5]));


        count=(long)(now.getTimeInMillis()-fileCal.getTimeInMillis())/1000/60/60;
        if(count>23){
            count/=24;
            answer=count+" days";
        }else
            answer=count+" hours";
        return answer;
    }
    public static FileType typeOf(File f){
        String[] mas = f.getName().split("-");
        switch (mas[5]){
            case "t":
                return FileType.TEXT;
            case "i.jpeg":
                return FileType.IMAGE;
            case "a.mp3":
                return FileType.AUDIO;
        }
        return null;
    }

    public static String generateFilenamePrefix(){
        Calendar cal = new GregorianCalendar();
        String prefix = cal.get(Calendar.YEAR)+"-"+
                cal.get(Calendar.MONTH)+"-"+
                cal.get(Calendar.DATE)+":"+
                cal.get(Calendar.HOUR_OF_DAY)+"-"+
                cal.get(Calendar.MINUTE)+"-"+
                cal.get(Calendar.SECOND);
        return prefix;
    }

    private static File getRootByType(FileType fileType){
        File root=null;
        switch (fileType){
            case TEXT:
                root=textRoot;
                break;
            case AUDIO:
                root=audioRoot;
                break;
            case IMAGE:
                root=imageRoot;
                break;
            case MINI:
                root=miniRoot;
                break;
        }
        return root;
    }
    private static String getPostfixByType(FileType fileType){
        String postfix=new String();
        switch (fileType){
            case TEXT:
                postfix="t";
                break;
            case IMAGE:case MINI:
                postfix="i.jpeg";
                break;
            case AUDIO:
                postfix="a.mp3";
                break;
        }
        return postfix;
    }


}
