package veeronten.actualnotes.managers;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import veeronten.actualnotes.L;

public class FileManager {
    public static FileManager instance;

    public enum FileType{text, image, audio, mini}

    static File textRoot;
    static File audioRoot;
    static File imageRoot;
    static File miniRoot;

    private static Context context;

    public FileManager(Context context){
        this.context = context;

        textRoot = new File(context.getFilesDir(), "text");
        textRoot.mkdirs();
        imageRoot = new File(context.getFilesDir(), "image");
        imageRoot.mkdirs();
        //TODO mini
        miniRoot = new File(context.getFilesDir(), "mimi");
        miniRoot.mkdirs();
        audioRoot = new File(context.getFilesDir(), "audio");
        audioRoot.mkdirs();

        instance = this;
    }
    public static FileManager getInstance(){
        return instance;
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
            if(fileType==FileType.image){
                File mini = new File(miniRoot,newFile.getName());
                mini.createNewFile();
                L.i(mini.toString()+" was created");
            }
            return newFile;
        } catch (IOException e) {
            L.d("cant create a new file "+newFile.toString(), e);
            return null;
        }
    }
    public static void removeFile(File fileToRemove){
        if(FileManager.typeOf(fileToRemove)=='i')
            new File(imageRoot, fileToRemove.getName()).delete();
        fileToRemove.delete();
        return;
    }

    public ArrayList<File> getFiles(){
        ArrayList<File> answer = new ArrayList<>();

        answer.addAll(getFiles(FileType.text));
        answer.addAll(getFiles(FileType.audio));
        answer.addAll(getFiles(FileType.image));
        return answer;
    }
    public static ArrayList<File> getFiles(FileType fileType){
        ArrayList<File> answer = new ArrayList<>();
        for(File f : getRootByType(fileType).listFiles())
            answer.add(f);
        return  answer;
    }

    public static int countOfFiles(){
        return countOfFiles(FileType.text)+countOfFiles(FileType.audio)+countOfFiles(FileType.image);
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
    public static char typeOf(File f){
        String[] mas = f.getName().split("-");
        switch (mas[5]){
            case "t":
                return 't';
            case "i":
                return 'i';
            case "a":
                return 'a';
        }
        return '0';
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
            case text:
                root=textRoot;
                break;
            case audio:
                root=audioRoot;
                break;
            case image:
                root=imageRoot;
                break;
            case mini:
                root=miniRoot;
                break;
        }
        return root;
    }
    private static char getPostfixByType(FileType fileType){
        char postfix='_';
        switch (fileType){
            case text:
                postfix='t';
                break;
            case image:case mini:
                postfix='i';
                break;
            case audio:
                postfix='a';
                break;
        }
        return postfix;
    }


}
