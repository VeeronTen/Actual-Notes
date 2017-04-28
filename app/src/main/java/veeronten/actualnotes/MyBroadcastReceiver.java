package veeronten.actualnotes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import veeronten.actualnotes.managers.MainManager;


public class MyBroadcastReceiver extends BroadcastReceiver {
    MainManager mainManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("MyLog","ПРИНЯТО");
        if(MainManager.getInstance()==null)
            new MainManager(context);
        mainManager = MainManager.getInstance();

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Log.i("MyLog","Soft was restarted");
            mainManager.notification.sendFastAccessNotification();
            mainManager.notification.downloadNotifications();
            return;
        }
        Log.i("MyLog","Signal was received");
        mainManager.notification.sendUsualNotification(mainManager.count());
    }
}
