package veeronten.actualnotes.managers;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import veeronten.actualnotes.L;

public class MyAudioManager {
    public static Boolean recording;
    private static MediaRecorder mediaRecorder;
    private static MediaPlayer mediaPlayer;

    static {
        recording=false;
        mediaRecorder=null;
        mediaPlayer= new MediaPlayer();
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
            } catch (FileNotFoundException e) {
                L.e("Cant found the file "+f.toString(),e);
            } catch (IOException e) {
                L.e("IO exception with "+f.toString(),e);
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
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setAudioEncodingBitRate(16);
            mediaRecorder.setAudioSamplingRate(44100);
            mediaRecorder.setOutputFile(file.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start();
            recording=true;
        } catch (IOException e) {
            L.e("Cant prepare mediaRecorder",e);
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
        } catch (FileNotFoundException e) {
            L.e("Cant found the file "+file.toString(),e);
        } catch (IOException e) {
            L.e("Cant prepare mediaPlayer",e);
        }
    }
    public static void stopPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
    public static boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }
}
