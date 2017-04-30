package veeronten.actualnotes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import veeronten.actualnotes.managers.FileManager;
import veeronten.actualnotes.managers.MyNotificationManager;


public class MyBroadcastReceiver extends BroadcastReceiver {
    FileManager fileManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        L.d("ПРИНЯТО");
        if(FileManager.getInstance()==null)
            new FileManager(context);
        fileManager = FileManager.getInstance();

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            L.i("Soft was restarted");
            MyNotificationManager.sendFastAccessNotification();
            MyNotificationManager.downloadNotifications();
            return;
        }
        L.i("Signal was received");
        MyNotificationManager.sendUsualNotification(fileManager.countOfFiles());
    }
}
