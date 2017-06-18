package veeronten.actualnotes.managers;

import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import veeronten.actualnotes.L;

public class MyAudioManager {
    public static Boolean recording;
    private static MediaRecorder mediaRecorder;
    private static MediaPlayer mediaPlayer;
    private static File currentlyPlaying;

    public static View viewToStop;
    static {
        recording=false;
        mediaRecorder=null;
        mediaPlayer= new MediaPlayer();
    }

    public static int getDuration(File f){
        Uri uri = Uri.fromFile(f);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(FileManager.getContext(),uri);
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Integer.parseInt(durationStr)/1000;
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
            currentlyPlaying=file;
        } catch (FileNotFoundException e) {
            L.e("Cant found the file "+file.toString(),e);
        } catch (IOException e) {
            L.e("Cant prepare mediaPlayer",e);
        }
    }
    public static void stopPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            currentlyPlaying=null;
        }
    }
    public static boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    public static MediaPlayer getMediaPlayer(){
        return mediaPlayer;
    }
    public static File getCurrentlyPlaying(){
        if (mediaPlayer.isPlaying())
            return currentlyPlaying;
        else return null;
    }
}
