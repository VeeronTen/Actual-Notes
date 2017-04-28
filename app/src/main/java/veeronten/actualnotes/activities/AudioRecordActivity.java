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
import veeronten.actualnotes.managers.MainManager;


public class AudioRecordActivity extends AppCompatActivity implements View.OnClickListener{
    ViewGroup vg;
    ImageButton btnPlay;
    ImageButton btnCancel;
    ImageButton btnSave;
    File fileToRecord;
    MainManager mainManager;

    ImageView microImage;
    TextView tvTime;
    Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiorecord);
        if(MainManager.getInstance()==null)
            new MainManager(getApplicationContext());
        mainManager = mainManager.getInstance();

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
        fileToRecord = mainManager.audio.createNewFile();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_audiorecord:
                if(mainManager.audio.recording){
                    timer.cancel();
                    try {
                        mainManager.audio.stopRecording();
                    }catch (RuntimeException e){
                        fileToRecord.delete();
                        recreate();
                    }
                    microImage.setColorFilter(getResources().getColor(R.color.microOff));
                    mainManager.audio.recording=false;
                    btnPlay.setVisibility(View.VISIBLE);
                    btnCancel.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.VISIBLE);
                }else{
                    btnPlay.setVisibility(View.INVISIBLE);
                    btnCancel.setVisibility(View.INVISIBLE);
                    btnSave.setVisibility(View.INVISIBLE);
                    tvTime.setText("0 s");
                    microImage.setColorFilter(getResources().getColor(R.color.microOn));                    mainManager.audio.stopPlay();
                    mainManager.audio.startRecording(fileToRecord);

                    timer = new Timer();
                    timer.schedule(new MyTimerTask(), 0, 1000);

                    mainManager.audio.recording=true;
                }
                break;
            case R.id.btnPlay:
                mainManager.audio.startPlay(fileToRecord);
                break;
            case R.id.btnCancel:
                btnPlay.setVisibility(View.INVISIBLE);
                btnCancel.setVisibility(View.INVISIBLE);
                btnSave.setVisibility(View.INVISIBLE);
                mainManager.audio.stopPlay();
                mainManager.audio.removeFile(fileToRecord);
                fileToRecord = mainManager.audio.createNewFile();
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
        if(mainManager.audio.recording) {
            mainManager.audio.stopRecording();
            timer.cancel();
        }
        if(fileToRecord.length()==0)
            mainManager.audio.removeFile(fileToRecord);
        else
            Toast.makeText(this, "File was saved", Toast.LENGTH_SHORT).show();
        mainManager.audio.recording=false;
        mainManager.audio.playing=false;

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


