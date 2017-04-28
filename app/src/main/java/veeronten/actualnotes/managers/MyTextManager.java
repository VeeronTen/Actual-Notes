package veeronten.actualnotes.managers;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MyTextManager {
    private Context context;
    private File textRoot;

    public MyTextManager(Context c){
        context = c;
        textRoot = new File(context.getFilesDir(), "text");
        textRoot.mkdirs();
    }

    public File createNewFile(){
        Calendar cal = new GregorianCalendar();
        String newName = cal.get(Calendar.YEAR)+"-"+
                cal.get(Calendar.MONTH)+"-"+
                cal.get(Calendar.DATE)+":"+
                cal.get(Calendar.HOUR_OF_DAY)+"-"+
                cal.get(Calendar.MINUTE)+"-"+
                cal.get(Calendar.SECOND)+"-t";
        try {
            File answer = new File(textRoot, newName);
            answer.createNewFile();
            return answer;
        } catch (IOException e) {
            Log.d("MyLog","cant create new file");
            return null;
        }
    }
    public void removeFile(File FileToRemove){
        FileToRemove.delete();
    }

    public ArrayList<File> getFiles(){
        ArrayList<File> answer = new ArrayList<>();
        for(File f : textRoot.listFiles())
            answer.add(f);
        return  answer;
    }

    public String readFile(File file){
        StringBuffer sb = new StringBuffer();
        try {
            file.createNewFile();// НУЖНО?
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s;
            while((s=br.readLine())!=null)
                sb.append(s+"\n");
        } catch (FileNotFoundException e) {
            Log.d("MyLog", "fnf");
        } catch (IOException e) {
            Log.d("MyLog", "io");
        }
        Log.d("MyLog",sb.toString());
        return sb.toString();
    }
    public void saveChanges(File file, String text){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(text);
            bw.flush();
        } catch (FileNotFoundException e) {
            Log.d("MyLog","fexp");
        } catch (IOException e) {
            Log.d("MyLog","io");
        }
    }
}
