package veeronten.actualnotes.activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import veeronten.actualnotes.R;
import veeronten.actualnotes.managers.FileManager;
import veeronten.actualnotes.managers.MyAudioManager;




public class AudioRecordActivity extends AppCompatActivity implements View.OnClickListener{
    ViewGroup vg;
    ImageButton btnPlay;
    ImageButton btnCancel;
    ImageButton btnSave;
    File fileToRecord;
    FileManager fileManager;

    ImageView microImage;
    TextView tvTime;
    Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiorecord);
        if(FileManager.getInstance()==null)
            new FileManager(getApplicationContext());
        fileManager = fileManager.getInstance();

        microImage = (ImageView)findViewById(R.id.microImage);

        vg=(ViewGroup)findViewById(R.id.activity_audiorecord);
            vg.setOnClickListener(this);
        btnPlay = (ImageButton)findViewById(R.id.btnPlay);
            btnPlay.setOnClickListener(this);
            btnPlay.setVisibility(View.INVISIBLE);
        btnCancel = (ImageButton)findViewById(R.id.btnCancel);
            btnCancel.setOnClickListener(this);
            btnCancel.setVisibility(View.INVISIBLE);
        btnSave = (ImageButton)findViewById(R.id.btnSave);
            btnSave.setOnClickListener(this);
            btnSave.setVisibility(View.INVISIBLE);

        tvTime = (TextView) findViewById(R.id.tvTime);
        fileToRecord = fileManager.createNewFile(FileManager.FileType.AUDIO);
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
                }else{
                    btnPlay.setVisibility(View.INVISIBLE);
                    btnCancel.setVisibility(View.INVISIBLE);
                    btnSave.setVisibility(View.INVISIBLE);
                    tvTime.setText("0 s");
                    microImage.setColorFilter(getResources().getColor(R.color.microOn));                    MyAudioManager.stopPlay();
                    MyAudioManager.startRecording(fileToRecord);

                    timer = new Timer();
                    timer.schedule(new MyTimerTask(), 0, 1000);

                    MyAudioManager.recording=true;
                }
                break;
            case R.id.btnPlay:
                if(MyAudioManager.isPlaying())
                    MyAudioManager.stopPlay();
                else
                    MyAudioManager.startPlay(fileToRecord);
                break;
            case R.id.btnCancel:
                btnPlay.setVisibility(View.INVISIBLE);
                btnCancel.setVisibility(View.INVISIBLE);
                btnSave.setVisibility(View.INVISIBLE);
                MyAudioManager.stopPlay();
                fileManager.removeFile(fileToRecord);
                fileToRecord = fileManager.createNewFile(FileManager.FileType.AUDIO);
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
        if(MyAudioManager.recording) {
            MyAudioManager.stopRecording();
            timer.cancel();
        }
        if(fileToRecord.length()==0)
            fileManager.removeFile(fileToRecord);
        else
            Toast.makeText(this, "File was saved", Toast.LENGTH_SHORT).show();
        MyAudioManager.recording=false;
    }
    private class MyTimerTask extends TimerTask{
        int sec=0;
        @Override
        public void run() {
            tvTime.post(new Runnable() {
                public void run() {
                    tvTime.setText(sec++ +" s");
                }
            });
        }

    }
}


