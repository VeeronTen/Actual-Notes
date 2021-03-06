package veeronten.actualnotes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import veeronten.actualnotes.managers.FileManager;
import veeronten.actualnotes.managers.MyNotificationManager;


public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        L.i("Signal was received");

        FileManager.start(context);

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            L.i("That was a BOOT signal");
            MyNotificationManager.downloadNotifications();
            return;
        }

        MyNotificationManager.sendUsualNotification();
        MyNotificationManager.registerNewNotification(intent.getAction());
    }
}
