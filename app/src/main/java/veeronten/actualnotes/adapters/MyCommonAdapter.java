package veeronten.actualnotes.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import veeronten.actualnotes.R;
import veeronten.actualnotes.managers.FileManager;
import veeronten.actualnotes.managers.MyAudioManager;
import veeronten.actualnotes.managers.MyTextManager;

import static veeronten.actualnotes.activities.ExploreActivity.modeFiles;

public class MyCommonAdapter extends ArrayAdapter<File> {
    LayoutInflater li;

    public MyCommonAdapter(Context context, List<File> objects) {
        super(context, R.layout.item_image, objects);
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        View v = null;
        TextView tv;
        TextView tv2;
        li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (FileManager.typeOf(getItem(pos))) {
            case IMAGE:
                v = li.inflate(R.layout.item_image, null);
                Bitmap bitmap = BitmapFactory.decodeFile(getItem(pos).getAbsolutePath());
                ImageView iv = (ImageView) v.findViewById(R.id.imageView);
                iv.setImageBitmap(bitmap);
                break;
            case AUDIO:
                File item = modeFiles.get(pos);
                v = li.inflate(R.layout.item_audio, null);
                tv = (TextView) v.findViewById(R.id.textView);
                tv.setText(MyAudioManager.getDuration(getItem(pos)) + " s.");
                if(MyAudioManager.getCurrentlyPlaying()!=null){
                    if(MyAudioManager.getCurrentlyPlaying().getName().equals(item.getName())) {
                        MyAudioManager.viewToStop.findViewById(R.id.internalLayout).setBackgroundColor(FileManager.getContext().getResources().getColor(R.color.audioBackground));
                        MyAudioManager.viewToStop = v;
                        MyAudioManager.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                MyAudioManager.viewToStop.findViewById(R.id.internalLayout).setBackgroundColor(FileManager.getContext().getResources().getColor(R.color.audioBackground));
                            }
                        });
                        v.findViewById(R.id.internalLayout).setBackgroundColor(FileManager.getContext().getResources().getColor(R.color.audioBackgroundChosen));
                    }
                }
                break;
            case TEXT:
                v = li.inflate(R.layout.item_text, null);
                tv = (TextView) v.findViewById(R.id.textView);
                tv.setText(MyTextManager.readFile(getItem(pos)));
                break;
        }
        tv2 = (TextView) v.findViewById(R.id.textView2);
        tv2.setText(FileManager.ageOf(getItem(pos)));
        return v;
    }
}
