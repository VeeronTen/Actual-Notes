package veeronten.actualnotes.managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import veeronten.actualnotes.L;

public class MyTextManager {

    public static String readFile(File file){
        StringBuffer sb = new StringBuffer();
        try {
            file.createNewFile();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s;
            while((s=br.readLine())!=null)
                sb.append(s+"\n");
        } catch (FileNotFoundException e) {
            L.e("Cant found the file "+file.toString(),e);
        } catch (IOException e) {
            L.e("IO exception with "+file.toString(),e);
        }
        return sb.toString();
    }
    public static void saveChanges(File file, String text){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(text);
            bw.flush();
        } catch (FileNotFoundException e) {
            L.e("Cant found the file "+file.toString(),e);
        } catch (IOException e) {
            L.e("IO exception with "+file.toString(),e);
        }
    }
}
