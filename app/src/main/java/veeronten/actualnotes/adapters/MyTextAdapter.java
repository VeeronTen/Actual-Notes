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
import veeronten.actualnotes.managers.MainManager;

public class MyTextAdapter extends ArrayAdapter<File> {
    LayoutInflater li;
    MainManager mainManager;

    public MyTextAdapter(Context context, List<File> objects) {
        super(context, R.layout.item_text, objects);
        mainManager = mainManager.getInstance();
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent){
        View v = convertView;
        if(v==null) {
            li = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v=li.inflate(R.layout.item_text, null);
        }
        TextView tv = (TextView)v.findViewById(R.id.textView);
        tv.setText(mainManager.text.readFile(getItem(pos)));

        TextView tv2 = (TextView)v.findViewById(R.id.textView2);
        tv2.setText(mainManager.getAge(getItem(pos)));
        return v;
    }


}