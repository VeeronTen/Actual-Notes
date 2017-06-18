package veeronten.actualnotes.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import veeronten.actualnotes.L;
import veeronten.actualnotes.R;
import veeronten.actualnotes.Tutorial;
import veeronten.actualnotes.adapters.MyCommonAdapter;
import veeronten.actualnotes.managers.FileManager;
import veeronten.actualnotes.managers.MyAudioManager;
import veeronten.actualnotes.managers.MyImageManager;
import veeronten.actualnotes.managers.MyTextManager;

import static veeronten.actualnotes.activities.ExploreActivity.Mode.COMMON;
import static veeronten.actualnotes.activities.ExploreActivity.Mode.IMAGE;
import static veeronten.actualnotes.managers.MyAudioManager.viewToStop;

public class ExploreActivity extends AppCompatActivity implements  View.OnClickListener, ListView.OnItemClickListener{
    enum Mode {TEXT, AUDIO, IMAGE, COMMON};
    Mode mode;
    ViewGroup layout;
    ListView list;
    public static ArrayList<File> modeFiles;
    BaseAdapter currentAdapter;
    ImageButton btnCommonMode;
    ImageButton btnAudioMode;
    ImageButton btnTextMode;
    ImageButton btnImageMode;

    public final static int CAMERA_PERMISSION_REQUEST = 314;
    public final static int MICROPHONE_PERMISSION_REQUEST = 764;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        FileManager.start(getApplicationContext());
        layout = (ViewGroup) findViewById(R.id.activity_explore);
        list = (ListView)findViewById(R.id.list);
            list.setOnItemClickListener(this);
            registerForContextMenu(list);
        btnCommonMode = (ImageButton) findViewById(R.id.btnCommonMode);
            btnCommonMode.setOnClickListener(this);
        btnAudioMode = (ImageButton) findViewById(R.id.btnAudioMode);
            btnAudioMode.setOnClickListener(this);
        btnTextMode = (ImageButton) findViewById(R.id.btnTextMode);
            btnTextMode.setOnClickListener(this);
        btnImageMode = (ImageButton) findViewById(R.id.btnImageMode);
            btnImageMode.setOnClickListener(this);
        mode=COMMON;
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent=null;
        switch (item.getItemId()){
            case R.id.settings:
                intent = new Intent(this,SettingsActivity.class);
                break;
            case R.id.notif:
                intent = new Intent(this,NotifyActivity.class);
                break;
        }
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        File item;
        Intent intent;
        switch (FileManager.typeOf(modeFiles.get(position))) {
            case AUDIO:
                item = modeFiles.get(position);
                if(MyAudioManager.isPlaying()) {
                    MyAudioManager.stopPlay();
                    if(viewToStop!=null) {
                        viewToStop.findViewById(R.id.internalLayout).setBackgroundColor(getResources().getColor(R.color.audioBackground));
                    }
                    view.findViewById(R.id.internalLayout).setBackgroundColor(getResources().getColor(R.color.audioBackground));
                }
                else {
                    MyAudioManager.startPlay(item);
                    registerCompletionListener(view);
                    viewToStop=view;
                    view.findViewById(R.id.internalLayout).setBackgroundColor(getResources().getColor(R.color.audioBackgroundChosen));
                }
                break;
            case TEXT:
                item = modeFiles.get(position);
                intent = new Intent(this, TextEditActivity.class);
                intent.putExtra("path", item.getAbsolutePath());
                startActivity(intent);
                break;
            case IMAGE:
                item = MyImageManager.getBig((modeFiles.get(position)).getName());
                intent = new Intent(this, LookActivity.class);
                intent.putExtra("path", item.getAbsolutePath());
                startActivity(intent);
                break;
        }

    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0,0,0,"Share");
        menu.add(0,1,0,"Delete");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        File chosenFile = modeFiles.get(acmi.position);
        if(item.getItemId()==0){
            //share
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            switch (FileManager.typeOf(chosenFile)){
                case TEXT:
                    sendIntent.putExtra(Intent.EXTRA_TEXT, MyTextManager.readFile(chosenFile));
                    sendIntent.setType("text/plain");
                    break;
                case AUDIO:
                    Uri audioURI = FileProvider.getUriForFile(this,
                            "veeronten.actualnotes.fileProvider",
                            chosenFile);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, audioURI);
                    sendIntent.setType("audio/aac");
                    break;
                case IMAGE:
                    chosenFile = MyImageManager.getBig(chosenFile.getName());
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "veeronten.actualnotes.fileProvider",
                            chosenFile);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
                    sendIntent.setType("image/jpeg");
                    break;
                default:break;
            }
            startActivity(sendIntent);
        }else{
            //delete
            modeFiles.remove(acmi.position);
            FileManager.removeFile(chosenFile);
            currentAdapter.notifyDataSetChanged();
        }
        return true;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCommonMode:
                commonMode();
                break;
            case R.id.btnAudioMode:
                if(mode==Mode.AUDIO){
                    if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
                        if(!checkAudioPermission())
                            return;
                    Intent intent = new Intent(this, AudioRecordActivity.class);
                    intent.setAction("actualnotes.intent.action.START_DICTAPHONE");
                    startActivity(intent);
                    return;
                } else audioMode();
                break;
            case R.id.btnTextMode:
                if(mode==Mode.TEXT) {
                    Intent intent = new Intent(this, TextEditActivity.class);
                    startActivity(intent);
                    return;
                } else textMode();
                break;
            case R.id.btnImageMode:
                if(mode== IMAGE){
                    if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
                        if(!checkCameraPermission())
                            return;
                    Intent intent = new Intent(this, ImageActivity.class);
                    intent.setAction("actualnotes.intent.action.START_CAM");
                    startActivity(intent);
                    return;
                } else imageMode();
                break;
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        if(mode!=null)
            switch (mode) {
                case COMMON:
                    commonMode();
                    break;
                case AUDIO:
                    audioMode();
                    break;
                case TEXT:
                    textMode();
                    break;
                case IMAGE:
                    imageMode();
                    break;
            }
    }
    private void  commonMode(){

        mode = Mode.COMMON;
        modeFiles = FileManager.getFiles(FileManager.FileType.TEXT);
        modeFiles.addAll(FileManager.getFiles(FileManager.FileType.AUDIO));
        modeFiles.addAll(FileManager.getFiles(FileManager.FileType.MINI));
        modeFiles = sort(modeFiles);

        if(Tutorial.isFirstLaunch(this))
            Tutorial.prepare(this);

        currentAdapter = new MyCommonAdapter(this, modeFiles);
        list.setAdapter(currentAdapter);
        resetStyle();
        btnCommonMode.setImageDrawable(getResources().getDrawable(R.drawable.ic_common_pressed));

        if(Tutorial.isFirstLaunch(this))
            Tutorial.startTutorial(this);
    }
    private void textMode(){
        mode = Mode.TEXT;
        modeFiles = FileManager.getFiles(FileManager.FileType.TEXT);
        modeFiles = sort(modeFiles);
        currentAdapter = new MyCommonAdapter(this, modeFiles);
        list.setAdapter(currentAdapter);
        resetStyle();
        btnTextMode.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_text));
        if(Tutorial.isFirstLaunch(this, 2))
            Tutorial.startTutorial(this, 2);
        }
    private void audioMode(){
        mode = Mode.AUDIO;
        modeFiles = FileManager.getFiles(FileManager.FileType.AUDIO);
        modeFiles = sort(modeFiles);
        currentAdapter = new MyCommonAdapter(this,modeFiles);
        list.setAdapter(currentAdapter);
        resetStyle();
        btnAudioMode.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_audio));
        if(Tutorial.isFirstLaunch(this, 3))
            Tutorial.startTutorial(this, 3);
    }
    private void imageMode(){
        mode = IMAGE;
        modeFiles = FileManager.getFiles(FileManager.FileType.MINI);
        modeFiles = sort(modeFiles);
        currentAdapter = new MyCommonAdapter(this,modeFiles);
        list.setAdapter(currentAdapter);
        resetStyle();
        btnImageMode.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_image));
        if(Tutorial.isFirstLaunch(this, 1))
            Tutorial.startTutorial(this, 1);
    }
    private ArrayList<File> sort(ArrayList<File> sourse){
        ArrayList<File> answer = new ArrayList<>();
        Object[] boba=sourse.toArray();
        Arrays.sort(boba,new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                Integer[] i1 = new Integer[6];
                Integer[] i2 = new Integer[6];
                String[] s1 = ((File)o1).getName().split("-|:");
                String[] s2 = ((File)o2).getName().split("-|:");
                for(int i =0;i<6;i++){
                    i1[i] = Integer.valueOf(s1[i]);
                    i2[i] = Integer.valueOf(s2[i]);
                }
                for(int i =0;i<6;i++)
                    if(i1[i].compareTo(i2[i])!=0)
                        return i1[i].compareTo(i2[i]);
                return 0;
            }
        });

        for(Object o:boba)
            answer.add((File)o);
        return answer;
    }
    private void resetStyle(){
        btnCommonMode.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_common, null));
        btnTextMode.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_text, null));
        btnImageMode.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_camera, null));
        btnAudioMode.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_audio, null));
    }
    @TargetApi(23)
    private Boolean checkAudioPermission(){
        Boolean answer = false;
        if ((checkSelfPermission(Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED)||(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, MICROPHONE_PERMISSION_REQUEST);
        } else answer = true;
        return  answer;
    }
    @TargetApi(23)
    private Boolean checkCameraPermission(){
        Boolean answer = false;
        if (checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        } else answer = true;
        return  answer;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if ((requestCode == MICROPHONE_PERMISSION_REQUEST)&&(grantResults[0] == PackageManager.PERMISSION_GRANTED)&&(grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    ||
                    (requestCode == CAMERA_PERMISSION_REQUEST)&&(grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                L.i("permissions were gotten");
            }
            else Toast.makeText(getApplicationContext(), "You must allow permission record audio to your mobile device.", Toast.LENGTH_LONG).show();
    }
    public void registerCompletionListener(final View v){
        MyAudioManager.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                v.findViewById(R.id.internalLayout).setBackgroundColor(getResources().getColor(R.color.audioBackground));
            }
        });
    }
}
