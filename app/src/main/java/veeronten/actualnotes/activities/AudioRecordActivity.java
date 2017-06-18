package veeronten.actualnotes.activities;


import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import veeronten.actualnotes.L;
import veeronten.actualnotes.R;
import veeronten.actualnotes.Tutorial;
import veeronten.actualnotes.managers.FileManager;
import veeronten.actualnotes.managers.MyAudioManager;

public class AudioRecordActivity extends AppCompatActivity implements View.OnClickListener{
    ViewGroup vg;
    ImageButton btnPlay;
    ImageButton btnCancel;
    ImageButton btnSave;
    File fileToRecord;
    ImageView microImage;
    TextView tvTime;
    Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FileManager.start(getApplicationContext());
        Intent receiveIntent=getIntent();
        if(receiveIntent.getAction().equals("android.intent.action.SEND")) {
            receiveAudio();
            finish();
        }else if(receiveIntent.getAction().equals("actualnotes.intent.action.START_DICTAPHONE")) {
            setContentView(R.layout.activity_audiorecord);
            microImage = (ImageView) findViewById(R.id.microImage);
            vg = (ViewGroup) findViewById(R.id.activity_audiorecord);
            vg.setOnClickListener(this);
            btnPlay = (ImageButton) findViewById(R.id.btnPlay);
            btnPlay.setOnClickListener(this);
            btnPlay.setVisibility(View.INVISIBLE);
            btnCancel = (ImageButton) findViewById(R.id.btnCancel);
            btnCancel.setOnClickListener(this);
            btnCancel.setVisibility(View.INVISIBLE);
            btnSave = (ImageButton) findViewById(R.id.btnSave);
            btnSave.setOnClickListener(this);
            btnSave.setVisibility(View.INVISIBLE);
            tvTime = (TextView) findViewById(R.id.tvTime);
            fileToRecord = FileManager.createNewFile(FileManager.FileType.AUDIO);


        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_audiorecord:
                if(MyAudioManager.recording){
                    timer.cancel();
                    try {
                        MyAudioManager.stopRecording();
                    }catch (RuntimeException e){
                        fileToRecord.delete();
                        recreate();
                    }
                    microImage.setColorFilter(getResources().getColor(R.color.microOff));
                    MyAudioManager.recording=false;
                    btnPlay.setVisibility(View.VISIBLE);
                    btnCancel.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.VISIBLE);
                    if(Tutorial.isFirstLaunch(this))
                        Tutorial.startTutorial(this);
                }else{
                    btnPlay.setVisibility(View.INVISIBLE);
                    btnCancel.setVisibility(View.INVISIBLE);
                    btnSave.setVisibility(View.INVISIBLE);
                    btnPlay.setBackgroundColor(getResources().getColor(R.color.ButtonColor));
                    tvTime.setText("0 s");
                    microImage.setColorFilter(getResources().getColor(R.color.microOn));
                    MyAudioManager.stopPlay();
                    MyAudioManager.startRecording(fileToRecord);
                    timer = new Timer();
                    timer.schedule(new MyTimerTask(), 0, 1000);
                    MyAudioManager.recording=true;
                }
                break;
            case R.id.btnPlay:
                if(MyAudioManager.isPlaying()) {
                    MyAudioManager.stopPlay();
                    btnPlay.setBackgroundColor(getResources().getColor(R.color.ButtonColor));
                }
                else {
                    MyAudioManager.startPlay(fileToRecord);
                    registerCompletionListener();
                    btnPlay.setBackgroundColor(getResources().getColor(R.color.ButtonColorFocused));
                }
                break;
            case R.id.btnCancel:
                btnPlay.setVisibility(View.INVISIBLE);
                btnCancel.setVisibility(View.INVISIBLE);
                btnSave.setVisibility(View.INVISIBLE);
                btnPlay.setBackgroundColor(getResources().getColor(R.color.ButtonColor));
                MyAudioManager.stopPlay();
                FileManager.removeFile(fileToRecord);
                fileToRecord = FileManager.createNewFile(FileManager.FileType.AUDIO);
                timer.cancel();
                tvTime.setText("0 s");
                break;
            case R.id.btnSave:
                finish();
                break;
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if(MyAudioManager.isPlaying())
            MyAudioManager.stopPlay();
        if(MyAudioManager.recording) {
            MyAudioManager.stopRecording();
            timer.cancel();
        }
        if(fileToRecord.length()==0)
            FileManager.removeFile(fileToRecord);
        else
            Toast.makeText(this, "File was saved", Toast.LENGTH_SHORT).show();
        MyAudioManager.recording=false;
    }

    private class MyTimerTask extends TimerTask{
        int sec=0;
        @Override
        public void run() {tvTime.post(new Runnable() {
                public void run() {tvTime.setText(sec++ +" s");
                }
            }); }
    }
    private void receiveAudio() {
        Uri uri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
        L.i("source uri: "+uri.getPath());
        File destinationFile = FileManager.createNewFile(FileManager.FileType.AUDIO);
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(getContentResolver().openInputStream(uri));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFile, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while(bis.read(buf) != -1);
            L.i("audio was received");
            Toast.makeText(this, "Audio was received",Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            L.printStackTrace(e);
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                L.printStackTrace(e);
            }
        }
    }
    private void registerCompletionListener(){
        MyAudioManager.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnPlay.setBackgroundColor(getResources().getColor(R.color.ButtonColor));
            }
        });
    }
}


