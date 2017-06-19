package veeronten.actualnotes.managers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

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
import veeronten.actualnotes.Settings;
import veeronten.actualnotes.activities.AudioRecordActivity;
import veeronten.actualnotes.activities.ExploreActivity;
import veeronten.actualnotes.activities.ImageActivity;
import veeronten.actualnotes.activities.TextEditActivity;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class MyNotificationManager {
    private static AlarmManager am;
    private static NotificationManager nm;

    private static Context context;

    static{
        context = FileManager.getContext();
        am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    }

    public static void registerNewNotification(String action){
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, MyTimeFormat.getHoursFromString(action));
        cal.set(Calendar.MINUTE, MyTimeFormat.getMinutesFromString(action));
        cal.set(Calendar.SECOND, 0);
        if(cal.getTimeInMillis()<=System.currentTimeMillis())
            cal.add(Calendar.DATE,1);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)
            am.setWindow(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 10000, createPendingIntentWithAction(action));
        else am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), createPendingIntentWithAction(action));
    }
    public static void cancelNotification(String action){
        am.cancel(createPendingIntentWithAction(action));
    }

    public static void cancelFastAccessNotification(){
        nm.cancel(0);
    }
    public static void saveNotifications(ArrayList<String> notifications){
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
    }
    public static ArrayList<String> downloadNotifications(){
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
        } catch (JSONException e) {
            L.w("JSON parse fails. JSON is not exist maybe");
            return answer;
        }
        return answer;
    }

    public static void sendUsualNotification(){
        String msg=null;

        if(FileManager.countOfFiles()==0){
            if(Settings.getSendIfHaveNoNotes())
                msg="You have no notes";
            else return;
        }else msg = "Notes:"+FileManager.countOfFiles();

        Uri soundUri= Uri.parse("android.resource://veeronten.actualnotes/" + R.raw.notification_sound);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, ExploreActivity.class), 0))
                .setSound(soundUri)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_DEFAULT)
                // большая картинка
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                .setTicker("There is something here!")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                .setContentTitle(msg)
                //.setContentText(res.getString(R.string.notifytext))
                .setContentText(""); // Текст уведомления
        if(Settings.getUseDefaultNotificationSound())
            builder.setDefaults(Notification.DEFAULT_SOUND);
        // Notification notification = builder.getNotification(); // до API 16
        Notification notification = builder.build();
        //notification.flags |= Notification.FLAG_INSISTENT;
        nm.notify(1, notification);
    }
    public static void sendFastAccessNotification(){
        sendUsualNotification();
        nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Intent imageIntent = new Intent(context, ImageActivity.class);
        imageIntent.setAction("actualnotes.intent.action.START_CAM");
        Intent textIntent = new Intent(context, TextEditActivity.class);
        Intent audioIntent = new Intent(context, AudioRecordActivity.class);
        audioIntent.setAction("actualnotes.intent.action.START_DICTAPHONE");
        PendingIntent imagePIntent = PendingIntent.getActivity(context, 0, imageIntent, 0);
        PendingIntent textPIntent = PendingIntent.getActivity(context, 0, textIntent, 0);
        PendingIntent audioPIntent = PendingIntent.getActivity(context, 0, audioIntent, 0);
        Notification notification=null;
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            notification = new NotificationCompat.Builder(context)
                    .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, ExploreActivity.class), 0))
                    .setTicker("Actual Notes fast access")
                    .setContentTitle("Actual Notes")
                    .setContentText("")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .addAction(R.mipmap.ic_camera_notif, "", imagePIntent)
                    .addAction(R.mipmap.ic_text_notif, "", textPIntent)
                    .addAction(R.mipmap.ic_audio_notif, "", audioPIntent)
                    .build();
        }else{
            notification = new NotificationCompat.Builder(context)
                    .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, ExploreActivity.class), 0))
                    .setTicker("Actual Notes fast access")
                    .setContentTitle("Actual Notes")
                    .setContentText("")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .addAction(R.mipmap.ic_camera_notif, "camera                                    ", imagePIntent)
                    .addAction(R.mipmap.ic_text_notif, "text                                      ", textPIntent)
                    .addAction(R.mipmap.ic_audio_notif, "audio                                     ", audioPIntent)
                    .build();
        }
        notification.flags |= Notification.FLAG_NO_CLEAR;
        nm.notify(0, notification);
    }
    private static PendingIntent createPendingIntentWithAction(String action){
        Intent intent = new Intent(context, MyBroadcastReceiver.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
