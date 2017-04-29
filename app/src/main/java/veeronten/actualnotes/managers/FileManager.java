package veeronten.actualnotes.managers;

import android.content.Context;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class FileManager {

    public static FileManager instance;

    public MyImageManager image;
    public MyTextManager text;
    public MyAudioManager audio;

    public MyNotificationManager notification;

    public FileManager(Context context){
        audio = new MyAudioManager(context);
        image = new MyImageManager(context);
        text = new MyTextManager(context);
        notification = new MyNotificationManager(context);
        instance = this;
    }
    public static FileManager getInstance(){
        return instance;
    }

    public int countOfFiles(){
        int answer=image.countOfFiles()+text.countOfFiles()+audio.countOfFiles();
        return answer;
    }
    public String ageOf(File f){
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
    public char typeOf(File f){
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
}
