package veeronten.actualnotes.managers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import veeronten.actualnotes.L;
import veeronten.actualnotes.MyBroadcastReceiver;
import veeronten.actualnotes.MyTimeFormat;
import veeronten.actualnotes.R;
import veeronten.actualnotes.activities.AudioRecordActivity;
import veeronten.actualnotes.activities.ExploreActivity;
import veeronten.actualnotes.activities.ImageActivity;
import veeronten.actualnotes.activities.TextEditActivity;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class MyNotificationManager {
    AlarmManager am;
    NotificationManager nm;

    Context context;

    public MyNotificationManager(Context context){
        this.context = context;
        am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    }

    public void registerNewNotification(String action){
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, MyTimeFormat.getHoursFromString(action));
        cal.set(Calendar.MINUTE, MyTimeFormat.getMinutesFromString(action));
        cal.set(Calendar.SECOND, 0);

        if(cal.getTimeInMillis()<System.currentTimeMillis())
            cal.add(Calendar.DATE,1);

        am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000*60*60*24, createPendingIntentWithAction(action));

        L.i(action+" was registered on "+(cal.getTimeInMillis()>System.currentTimeMillis()?"this day":"the next day"));
    }
    public void cancelNotification(String action){
        am.cancel(createPendingIntentWithAction(action));
        L.i(action+" was canceled");
    }

    public void saveNotifications(ArrayList<String> notifications){
        JSONObject mainObj = new JSONObject();
        JSONArray arrayForList = new JSONArray();

        try{
            for (int i = 0; i < notifications.size(); i++)
                arrayForList.put(notifications.get(i));
            mainObj.put("notifyList", arrayForList);
        } catch (JSONException e){
            L.e("JSON exception", e);
        }
        SharedPreferences sPref = context.getSharedPreferences("notifyList", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("notifyList", mainObj.toString());
        ed.apply();

        L.i("Notifications were saved.\n\tNow they are "+mainObj.toString());
    }
    public ArrayList<String> downloadNotifications(){
        JSONObject mainObj;
        JSONArray arrayForList;
        ArrayList<String> answer = new ArrayList<>();

        SharedPreferences sPref = context.getSharedPreferences("notifyList", MODE_PRIVATE);
        String savedText = sPref.getString("notifyList", "");
        try {
            mainObj = new JSONObject(savedText);
            arrayForList = mainObj.getJSONArray("notifyList");
            for (int i = 0; i < arrayForList.length(); i++) {
                String actionToRegister = arrayForList.getString(i);
                registerNewNotification(actionToRegister);
                answer.add(arrayForList.getString(i));
            }
            L.i("All notifications were downloaded");
        } catch (JSONException e) {
            L.w("JSON parse fails. JSON is not exist maybe");
            return answer;
        }
        return answer;
    }

    public void sendUsualNotification(int count){
        L.d("ВОТ Я ВНУТРИ");
        if(count==0)
            return;
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, ExploreActivity.class), 0))
                .setDefaults(Notification.DEFAULT_SOUND)
                .setSmallIcon(R.mipmap.ic_launcher)
                // большая картинка
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                .setTicker("There is something here!")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                .setContentTitle("Notes:"+count)
                //.setContentText(res.getString(R.string.notifytext))
                .setContentText(""); // Текст уведомления

        // Notification notification = builder.getNotification(); // до API 16
        Notification notification = builder.getNotification();
        //notification.flags |= Notification.FLAG_INSISTENT;
        nm.notify(1, notification);
        L.d("ПОСЛАНО");
    }
    public void sendFastAccessNotification(){
        nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // Намерение для запуска второй активности
        Intent imageIntent = new Intent(context, ImageActivity.class);
        Intent textIntent = new Intent(context, TextEditActivity.class);
        Intent audioIntent = new Intent(context, AudioRecordActivity.class);

        PendingIntent imagePIntent = PendingIntent.getActivity(context, 0, imageIntent, 0);
        PendingIntent textPIntent = PendingIntent.getActivity(context, 0, textIntent, 0);
        PendingIntent audioPIntent = PendingIntent.getActivity(context, 0, audioIntent, 0);

        // Строим уведомление
        Notification notification = new Notification.Builder(context)
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, ExploreActivity.class), 0))
                .setTicker("Actual Notes fast access")
                .setContentTitle("Actual Notes")
                .setContentText("")
                .setSmallIcon(R.mipmap.ic_launcher)
                .addAction(R.drawable.ic_camera_notif, "", imagePIntent)
                .addAction(R.drawable.ic_text_notif, "", textPIntent)
                .addAction(R.drawable.ic_audio_notif, "", audioPIntent)
                .setOngoing(true)
                .build();
        //notification.flags |= Notification.FLAG_NO_CLEAR;
        nm.notify(0, notification);
    }


    private PendingIntent createPendingIntentWithAction(String action){
        Intent intent = new Intent(context, MyBroadcastReceiver.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
