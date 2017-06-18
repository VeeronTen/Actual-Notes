package veeronten.actualnotes.activities;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import veeronten.actualnotes.L;
import veeronten.actualnotes.MyTimeFormat;
import veeronten.actualnotes.R;
import veeronten.actualnotes.Tutorial;
import veeronten.actualnotes.managers.FileManager;
import veeronten.actualnotes.managers.MyNotificationManager;

public class NotifyActivity extends AppCompatActivity implements View.OnClickListener, ListView.OnItemClickListener{
    ImageButton btnCreate;
    ListView lvNotifications;
    public static ArrayList<String> notifyList;
    ArrayAdapter<String> adapter;
    String itemActionToEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        FileManager.start(getApplicationContext());

        notifyList= MyNotificationManager.downloadNotifications();
        if(Tutorial.isFirstLaunch(this))
            Tutorial.prepare(this);
        adapter = new ArrayAdapter<>(NotifyActivity.this, R.layout.item_notif, notifyList);
        btnCreate = (ImageButton)findViewById(R.id.btnCreate);
            btnCreate.setOnClickListener(this);
        lvNotifications = (ListView)findViewById(R.id.lvNotifications);
            lvNotifications.setOnItemClickListener(this);
            registerForContextMenu(lvNotifications);
            lvNotifications.setAdapter(adapter);
        if(Tutorial.isFirstLaunch(this))
            Tutorial.startTutorial(this);
    }
    @Override
    public void onClick(View v){
        if(v.getId()==btnCreate.getId()){
            new TimePickerDialog(this, R.style.DialogTheme, CallBackWithCreate, 12, 0, true).show();
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        itemActionToEdit = notifyList.get(position);
        new TimePickerDialog(this, R.style.DialogTheme, CallBackWithEdit, MyTimeFormat.getHoursFromString(itemActionToEdit), MyTimeFormat.getMinutesFromString(itemActionToEdit), true).show();
    }
    TimePickerDialog.OnTimeSetListener CallBackWithEdit = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute){
            String actionToRegister = MyTimeFormat.convertToString(hourOfDay, minute);
            if(notifyList.contains(actionToRegister))
                return;
            MyNotificationManager.cancelNotification(itemActionToEdit);
            notifyList.remove(notifyList.indexOf(itemActionToEdit));
            MyNotificationManager.registerNewNotification(actionToRegister);
            notifyList.add(actionToRegister);
            Collections.sort(notifyList, new Comparator<String>() {
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });
            adapter.notifyDataSetChanged();
            MyNotificationManager.saveNotifications(notifyList);
            L.i(itemActionToEdit+" was changed to "+actionToRegister);
    }
    };
    TimePickerDialog.OnTimeSetListener CallBackWithCreate = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String actionToRegister = MyTimeFormat.convertToString(hourOfDay, minute);
            if(notifyList.contains(actionToRegister))
                return;
            MyNotificationManager.registerNewNotification(actionToRegister);
            notifyList.add(actionToRegister);
            Collections.sort(notifyList, new Comparator<String>() {
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });
            adapter.notifyDataSetChanged();
            MyNotificationManager.saveNotifications(notifyList);
        }
    };
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0,0,0,"Delete");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int position = acmi.position;
        String text = notifyList.get(position);
        MyNotificationManager.cancelNotification(text);
        notifyList.remove(position);
        adapter.notifyDataSetChanged();
        MyNotificationManager.saveNotifications(notifyList);
        return true;
    }
}
