package veeronten.actualnotes;

import android.content.SharedPreferences;

import veeronten.actualnotes.managers.FileManager;
import veeronten.actualnotes.managers.MyNotificationManager;

import static android.content.Context.MODE_PRIVATE;
import static veeronten.actualnotes.managers.MyNotificationManager.sendFastAccessNotification;

public class Settings {
    private static SharedPreferences sPref;
    private static SharedPreferences.Editor settingsEditor;
    private final static String settingsFile = "settings";

    static{
        sPref = FileManager.getContext().getSharedPreferences(settingsFile, MODE_PRIVATE);
        settingsEditor = sPref.edit();
    }

    public Settings(){}

    public static void setFastAccessStatus(Boolean value){
        settingsEditor.putBoolean("fastAccessStatus", value);
        if(value)
            sendFastAccessNotification();
        else
            MyNotificationManager.cancelFastAccessNotification();
        settingsEditor.apply();
    }
    public static Boolean getFastAccessStatus(){
        return sPref.getBoolean("fastAccessStatus", true);
    }

    public static void setSendIfHaveNoNotes(Boolean value){
        settingsEditor.putBoolean("sendIfHaveNoNotes", value);
        settingsEditor.apply();
    }
    public static Boolean getSendIfHaveNoNotes(){
        return sPref.getBoolean("sendIfHaveNoNotes", true);
    }

    public static void setUseDefaultNotificationSound(Boolean value){
        settingsEditor.putBoolean("useDefaultNotificationSound", value);
        settingsEditor.apply();
    }
    public static Boolean getUseDefaultNotificationSound(){
        return sPref.getBoolean("useDefaultNotificationSound", false);
    }
}
