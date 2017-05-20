package veeronten.actualnotes.activities;

//TODO ability to send audio and image info to my app. does no matter
//TODO shared audio file is cut
//TODO sometimes cam orientation is working wrong
//TODO show fastaccess after installation

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import veeronten.actualnotes.L;
import veeronten.actualnotes.R;
import veeronten.actualnotes.adapters.MyCommonAdapter;
import veeronten.actualnotes.managers.FileManager;
import veeronten.actualnotes.managers.MyAudioManager;
import veeronten.actualnotes.managers.MyImageManager;
import veeronten.actualnotes.managers.MyTextManager;

import static veeronten.actualnotes.activities.ExploreActivity.Mode.IMAGE;



public class ExploreActivity extends AppCompatActivity implements  View.OnClickListener, ListView.OnItemClickListener{
    enum Mode {TEXT, AUDIO, IMAGE, COMMON};
    Mode mode;

    ViewGroup layout;

    FileManager fileManager;

    ListView list;
    ArrayList<File> modeFiles;

    BaseAdapter currentAdapter;

    ImageButton btnCommonMode;
    ImageButton btnAudioMode;
    ImageButton btnTextMode;
    ImageButton btnImageMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        if(FileManager.getInstance()==null)
            new FileManager(getApplicationContext());
        fileManager = FileManager.getInstance();

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

        commonMode();
        //MyNotificationManager.sendFastAccessNotification();
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
                if(MyAudioManager.isPlaying())
                    MyAudioManager.stopPlay();
                else
                    MyAudioManager.startPlay(item);
                break;
            case TEXT:
                item = modeFiles.get(position);
                intent = new Intent(this, TextEditActivity.class);
                intent.putExtra("path", item.getAbsolutePath());
                startActivity(intent);
                break;
            case IMAGE:
                item = MyImageManager.getBig((modeFiles.get(position)).getName());
                L.d(item.toString());
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
                    L.d(audioURI.toString());
                    sendIntent.putExtra(Intent.EXTRA_STREAM, audioURI);
                    sendIntent.setType("audio/mp3");
                    break;
                case IMAGE:
                    chosenFile = MyImageManager.getBig(chosenFile.getName());
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "veeronten.actualnotes.fileProvider",
                            chosenFile);
                    L.d(photoURI.toString());
                    sendIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
                    sendIntent.setType("image/jpeg");
                    break;
                default:break;
            }

            startActivity(sendIntent);
        }else {
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
                    Intent intent = new Intent(this, AudioRecordActivity.class);
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
                    Intent intent = new Intent(this, ImageActivity.class);
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
        currentAdapter = new MyCommonAdapter(this, modeFiles);
        list.setAdapter(currentAdapter);

        resetStyle();
    }

    private void textMode(){


        mode = Mode.TEXT;
        modeFiles = FileManager.getFiles(FileManager.FileType.TEXT);
        modeFiles = sort(modeFiles);
        currentAdapter = new MyCommonAdapter(this, modeFiles);
        list.setAdapter(currentAdapter);

        resetStyle();
        btnTextMode.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_text));
        }

    private void audioMode(){


        mode = Mode.AUDIO;
        modeFiles = FileManager.getFiles(FileManager.FileType.AUDIO);
        modeFiles = sort(modeFiles);
        currentAdapter = new MyCommonAdapter(this,modeFiles);
        list.setAdapter(currentAdapter);

        resetStyle();
        btnAudioMode.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_audio));

    }
///////////////////////
    private void imageMode(){
        mode = IMAGE;
        modeFiles = FileManager.getFiles(FileManager.FileType.MINI);
        modeFiles = sort(modeFiles);
        currentAdapter = new MyCommonAdapter(this,modeFiles);
        list.setAdapter(currentAdapter);
        resetStyle();
        btnImageMode.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_image));

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
        btnCommonMode.setImageDrawable(getResources().getDrawable(R.drawable.ic_common));
        btnTextMode.setImageDrawable(getResources().getDrawable(R.drawable.ic_text));
        btnImageMode.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera));
        btnAudioMode.setImageDrawable(getResources().getDrawable(R.drawable.ic_audio));
    }
}
