package veeronten.actualnotes.managers;

import android.content.Context;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainManager {


    public static MainManager instance;

    public MyImageManager image;
    public MyTextManager text;
    public MyAudioManager audio;

    public MyNotificationManager notification;

    public MainManager(Context context){
        audio = new MyAudioManager(context);
        image = new MyImageManager(context);
        text = new MyTextManager(context);
        notification = new MyNotificationManager(context);
        instance = this;
    }
    public static MainManager getInstance(){
        return instance;
    }

    public int count(){
        int answer=image.getMinis().size()+text.getFiles().size()+audio.getFiles().size();
        return answer;
    }
    public String getAge(File f){
        String answer;

        int count;
        String name = f.getName();
        String[] mas = name.split("-|:");

        Calendar now = new GregorianCalendar();
        Calendar fileCal = new GregorianCalendar(Integer.valueOf(mas[0]),Integer.valueOf(mas[1]),
                Integer.valueOf(mas[2]),Integer.valueOf(mas[3]),Integer.valueOf(mas[4]),Integer.valueOf(mas[5]));


        count=(int)(now.getTimeInMillis()-fileCal.getTimeInMillis())/1000/60/60;
        if(count>23){
            count/=24;
            answer=count+" days";
        }else
            answer=count+" hours";
        return answer;
    }
    public char getFileType(File f){
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
