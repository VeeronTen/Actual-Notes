package veeronten.actualnotes;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
import veeronten.actualnotes.activities.AudioRecordActivity;
import veeronten.actualnotes.activities.ExploreActivity;
import veeronten.actualnotes.activities.NotifyActivity;
import veeronten.actualnotes.activities.TextEditActivity;
import veeronten.actualnotes.managers.FileManager;
import veeronten.actualnotes.managers.MyNotificationManager;
import veeronten.actualnotes.managers.MyTextManager;

import static android.content.Context.MODE_PRIVATE;
import static veeronten.actualnotes.activities.NotifyActivity.notifyList;

public class Tutorial {
    private static SharedPreferences sPref;
    private static SharedPreferences.Editor tutorialEditor;
    private final static String tutorialFile = "tutorial";
    private static ShowcaseConfig config;
    private static String exploreActName;
    private static String textEditActName;
    private static String audioRecordActName;
    private static String notifyActName;
    private static View allScreen;
    static{
        config= new ShowcaseConfig();
        config.setDelay(0);
        config.setMaskColor(Color.argb(220,30,30,30));

        exploreActName="activities."+ExploreActivity.class.getSimpleName();
        textEditActName="activities."+TextEditActivity.class.getSimpleName();
        audioRecordActName="activities."+AudioRecordActivity.class.getSimpleName();
        notifyActName="activities."+NotifyActivity.class.getSimpleName();

        sPref = FileManager.getContext().getSharedPreferences(tutorialFile, MODE_PRIVATE);
        tutorialEditor = sPref.edit();
        hack();
    }

    public Tutorial(){}

    public static Boolean isFirstLaunch(Activity activity){
        return isFirstLaunch(activity, 0);
    }
    public static Boolean isFirstLaunch(Activity activity, int code){
        String activityName = activity.getLocalClassName();
        Boolean answer = false;
        if(exploreActName.equals(activityName)){
            if(code==0) answer = sPref.getBoolean(exploreActName, true);
            else answer = sPref.getBoolean(exploreActName+"sp", true);
        }else if(textEditActName.equals(activityName))
            answer = sPref.getBoolean(textEditActName, true);
        else if(audioRecordActName.equals(activityName))
            answer = sPref.getBoolean(audioRecordActName, true);
        else if(notifyActName.equals(activityName))
            answer = sPref.getBoolean(notifyActName, true);
        return answer;
    }

    public static void restartTutorial(){
        tutorialEditor.putBoolean(exploreActName, true);
        tutorialEditor.putBoolean(exploreActName+"sp", true);
        tutorialEditor.putBoolean(textEditActName, true);
        tutorialEditor.putBoolean(audioRecordActName, true);
        tutorialEditor.putBoolean(notifyActName, true);

        tutorialEditor.apply();
    }

    public static void prepare(Activity activity){
        String activityName = activity.getLocalClassName();

        if(exploreActName.equals(activityName)){
            if(ExploreActivity.modeFiles.isEmpty()){
                File tutorFile = FileManager.createNewFile(FileManager.FileType.TEXT);
                MyTextManager.saveChanges(tutorFile,"Nice to see you, traveler");
                ExploreActivity.modeFiles.add(tutorFile);
            }

        }else if(notifyActName.equals(activityName)){
            if(notifyList.isEmpty()){
                MyNotificationManager.registerNewNotification("12:00");
                NotifyActivity.notifyList.add("12:00");
                MyNotificationManager.saveNotifications(NotifyActivity.notifyList);
            }
        }
    }

    public static void startTutorial(Activity activity){startTutorial(activity, 0);}
    public static void startTutorial(Activity activity, int code){
        String activityName = activity.getLocalClassName();

        if(exploreActName.equals(activityName)){
            if(code==0) expTutor(activity);
            else specTutor(activity, code);
        }else if(textEditActName.equals(activityName)) textTutor(activity);
        else if(audioRecordActName.equals(activityName)) audioTutor(activity);
        else if(notifyActName.equals(activityName)) notifyTutor(activity);
    }

    private static void expTutor(Activity activity){
        tutorialEditor.putBoolean(exploreActName, false);
        tutorialEditor.apply();

        ListView list = (ListView)activity.findViewById(R.id.list);
        TextView view = (TextView) list.getAdapter().getView(0, null, list).findViewById(R.id.textView2);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity);
        sequence.setConfig(config);

        sequence.addSequenceItem(allScreen, "Main screen","Here is list of your notes. In the lower right corner of every notes you will see " +
                "its age. At the moment age of the first note is "+view.getText(), "OK");
        sequence.addSequenceItem(allScreen,"Press to interact, hold to share or delete", "OK");
        sequence.addSequenceItem(activity.findViewById(R.id.buttons), "Choose mode to show only photo, text or audio notes, or choose " +
                "common mode to show them all", "OK");
        sequence.addSequenceItem(allScreen,"There are reminder and settings in the upper right corner of the screen", "OK");

        sequence.start();
    }
    private static void specTutor(Activity activity, int code){
        tutorialEditor.putBoolean(exploreActName+"sp", false);
        tutorialEditor.apply();

        ImageButton btnToFocus = null;
        String word = null;
        switch (code){
            case 1: word = "Photo"; btnToFocus = (ImageButton) activity.findViewById(R.id.btnImageMode); break;
            case 2: word = "Text"; btnToFocus = (ImageButton) activity.findViewById(R.id.btnTextMode); break;
            case 3: word = "Audio"; btnToFocus = (ImageButton) activity.findViewById(R.id.btnAudioMode); break;
        }
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity);
        sequence.setConfig(config);


        sequence.addSequenceItem(allScreen, "specific modes","in specific modes you can see notes of certain type only. "+word+" notes for this mode", "OK");
        sequence.addSequenceItem(btnToFocus,"press PLUS to create a new one", "OK");

        sequence.start();
    }
    private static void textTutor(Activity activity){
        tutorialEditor.putBoolean(textEditActName, false);
        tutorialEditor.apply();

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity);
        sequence.setConfig(config);

        sequence.addSequenceItem(allScreen, "Text note","Use menu to save, close or delete yor text note e.g.", "OK");

        sequence.start();


    }
    private static void audioTutor(Activity activity){
        tutorialEditor.putBoolean(audioRecordActName, false);
        tutorialEditor.apply();

        ImageButton btnPlay = (ImageButton)activity.findViewById(R.id.btnPlay);
        ImageButton btnSave = (ImageButton)activity.findViewById(R.id.btnSave);
        ImageButton btnCancel = (ImageButton)activity.findViewById(R.id.btnCancel);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity);
        sequence.setConfig(config);

        sequence.addSequenceItem(btnPlay, "Audio note","play/stop", "OK");
        sequence.addSequenceItem(btnSave,"save", "OK");
        sequence.addSequenceItem(btnCancel,"restart", "OK");

        sequence.start();
    }
    private static void notifyTutor(Activity activity){
        tutorialEditor.putBoolean(notifyActName, false);
        tutorialEditor.apply();

        ListView list = (ListView)activity.findViewById(R.id.lvNotifications);
        TextView view = (TextView) list.getAdapter().getView(0, null, list);
        ImageButton btnCreate = (ImageButton)activity.findViewById(R.id.btnCreate);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity);
        sequence.setConfig(config);

        sequence.addSequenceItem(allScreen,"Notifications", "You will get notification about count of your notes everyday at the set time. Now "
                +view.getText()+" is available", "OK");
        sequence.addSequenceItem(btnCreate,"To set another reminder ", "OK");
        sequence.addSequenceItem(allScreen,"press to edit\nhold to delete", "OK");
        sequence.addSequenceItem(allScreen,"Troubleshoot","If notifications don't work (don't make sounds e.g.) try to change priority " +
                "of Actual Notes notifications through the notification settings in your device", "KO");
        sequence.start();
    }

    private static void hack(){
        allScreen = View.inflate(FileManager.getContext(),R.layout.item_text, null);
    }
}
