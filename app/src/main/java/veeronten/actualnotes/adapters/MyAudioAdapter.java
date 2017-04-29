package veeronten.actualnotes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import veeronten.actualnotes.R;
import veeronten.actualnotes.managers.FileManager;

public class MyAudioAdapter extends ArrayAdapter<File> {
    LayoutInflater li;
    FileManager fileManager;

    public MyAudioAdapter(Context context, List<File> objects) {
        super(context, R.layout.item_audio, objects);
        fileManager = fileManager.getInstance();
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent){
        View v = convertView;
        if(v==null) {
            li = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v=li.inflate(R.layout.item_audio, null);
        }
        TextView tv = (TextView)v.findViewById(R.id.textView);
        tv.setText(fileManager.audio.getDuration(getItem(pos))+" s.");

        TextView tv2 = (TextView)v.findViewById(R.id.textView2);
        tv2.setText(fileManager.ageOf(getItem(pos)));
        return v;
    }


}
