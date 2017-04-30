package veeronten.actualnotes.managers;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import veeronten.actualnotes.L;

public class MyAudioManager {
    public static Boolean recording;
    public static Boolean playing;
    private static MediaRecorder mediaRecorder;
    private static MediaPlayer mediaPlayer;

    static {
        recording=false;
        playing = false;
        mediaRecorder=null;
        mediaPlayer=null;
    }

    public static int getDuration(File f){
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

    public static void startRecording(File file){
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
    public static void stopRecording(){
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder=null;
            recording=false;
        }
    }

    public static void startPlay(File file) {
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
    public static void stopPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            playing = false;
        }
    }
}
