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
import veeronten.actualnotes.managers.MainManager;

public class MyCommonAdapter extends ArrayAdapter<File> {
    LayoutInflater li;
    MainManager mainManager;

    public MyCommonAdapter(Context context, List<File> objects) {
        super(context, R.layout.item_image, objects);
        mainManager = mainManager.getInstance();
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        View v = convertView;
        TextView tv;
        TextView tv2;


        li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (mainManager.getFileType(getItem(pos))) {
            case 'i':
                v = li.inflate(R.layout.item_image, null);
                tv = (TextView) v.findViewById(R.id.textView);
                tv.setText(mainManager.getAge(getItem(pos)));

                Bitmap bitmap = BitmapFactory.decodeFile(getItem(pos).getAbsolutePath());
                ImageView iv = (ImageView) v.findViewById(R.id.imageView);
                iv.setImageBitmap(bitmap);
                break;
            case 'a':
                v = li.inflate(R.layout.item_audio, null);
                tv = (TextView) v.findViewById(R.id.textView);
                tv.setText(mainManager.audio.getDuration(getItem(pos)) + " s.");

                tv2 = (TextView) v.findViewById(R.id.textView2);
                tv2.setText(mainManager.getAge(getItem(pos)));
                break;
            case 't':
                v = li.inflate(R.layout.item_text, null);
                tv = (TextView) v.findViewById(R.id.textView);
                tv.setText(mainManager.text.readFile(getItem(pos)));

                tv2 = (TextView) v.findViewById(R.id.textView2);
                tv2.setText(mainManager.getAge(getItem(pos)));
                break;
        }
        return v;
    }
}
