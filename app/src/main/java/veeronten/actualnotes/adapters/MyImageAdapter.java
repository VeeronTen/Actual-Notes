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

public class MyImageAdapter extends ArrayAdapter<File> {
    LayoutInflater li;
    FileManager fileManager;

    public MyImageAdapter(Context context, List<File> objects) {
        super(context, R.layout.item_image, objects);
        fileManager = fileManager.getInstance();
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent){
        View v = convertView;
        if(v==null) {
            li = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v=li.inflate(R.layout.item_image, null);
        }
        TextView tv = (TextView)v.findViewById(R.id.textView);
        tv.setText(fileManager.ageOf(getItem(pos)));



        Bitmap bitmap = BitmapFactory.decodeFile(getItem(pos).getAbsolutePath());
        ImageView iv = (ImageView)v.findViewById(R.id.imageView);
        iv.setImageBitmap(bitmap);
        return v;
    }


}
