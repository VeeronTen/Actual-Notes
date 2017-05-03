package veeronten.actualnotes.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class MyCommonAdapter extends ArrayAdapter<File> {
    LayoutInflater li;

    public MyCommonAdapter(Context context, List<File> objects) {
        super(context, R.layout.item_image, objects);
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        View v = convertView;
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
                v = li.inflate(R.layout.item_audio, null);
                tv = (TextView) v.findViewById(R.id.textView);
                tv.setText(MyAudioManager.getDuration(getItem(pos)) + " s.");
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
