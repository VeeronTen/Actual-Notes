package veeronten.actualnotes.managers;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import veeronten.actualnotes.L;

public class MyAudioManager {
    public Boolean recording;
    public Boolean playing;

    private Context context;
    private File audioRoot;

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;

    public MyAudioManager(Context c){
        context = c;
        audioRoot = new File(context.getFilesDir(), "audio");
        audioRoot.mkdirs();
        recording=false;
        playing = false;
    }

    public File createNewFile(){
        Calendar cal = new GregorianCalendar();
        String newName = cal.get(Calendar.YEAR)+"-"+
                cal.get(Calendar.MONTH)+"-"+
                cal.get(Calendar.DATE)+":"+
                cal.get(Calendar.HOUR_OF_DAY)+"-"+
                cal.get(Calendar.MINUTE)+"-"+
                cal.get(Calendar.SECOND)+"-a";
        try {

            File answer = new File(audioRoot, newName);
            answer.createNewFile();
            return answer;
        } catch (IOException e) {
            L.d("cant create new file");
            return null;
        }
    }
    public void removeFile(File fileToRemove){
        fileToRemove.delete();
    }

    public ArrayList<File> getFiles(){
        ArrayList<File> answer = new ArrayList<>();
        for(File f : audioRoot.listFiles())
            answer.add(f);
        return  answer;
    }

    public int countOfFiles(){
        return audioRoot.listFiles().length;
    }

    public int getDuration(File f){
        int answer=0;

            try {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                mediaPlayer = new MediaPlayer();
                FileInputStream fis = new FileInputStream(f);
                mediaPlayer.setDataSource(fis.getFD());
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
                answer = mediaPlayer.getDuration()/1000;
            } catch (Exception e) {
                L.d("b",e);
            }

        return  answer;
    }

    public void startRecording(File file){
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(file.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start();
            recording=true;
        } catch (IOException e) {
            L.d("be da", e);
        }

    }
    public void stopRecording(){
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder=null;
            recording=false;
        }
    }

    public void startPlay(File file) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }

            mediaPlayer = new MediaPlayer();
            FileInputStream fis = new FileInputStream(file);
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            mediaPlayer.start();
            playing = true;
        } catch (Exception e) {
            L.d("b",e);
        }
    }
    public void stopPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            playing = false;
        }
    }
}
