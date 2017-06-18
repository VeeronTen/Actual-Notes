package veeronten.actualnotes.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import veeronten.actualnotes.R;
import veeronten.actualnotes.Settings;
import veeronten.actualnotes.Tutorial;


public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    CheckBox chbFastAccessStatus;
    CheckBox chbSendIfHaveNoNotes;
    CheckBox chbUseDefaultNotificationSound;
    Button btnRestartTutorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        chbFastAccessStatus= (CheckBox)findViewById(R.id.chbFastAccessStatus);
            chbFastAccessStatus.setChecked(Settings.getFastAccessStatus());
            chbFastAccessStatus.setOnClickListener(this);
        chbSendIfHaveNoNotes= (CheckBox)findViewById(R.id.chbSendIfHaveNoNotes);
            chbSendIfHaveNoNotes.setChecked(Settings.getSendIfHaveNoNotes());
            chbSendIfHaveNoNotes.setOnClickListener(this);
        chbUseDefaultNotificationSound= (CheckBox)findViewById(R.id.chbUseDefaultNotificationSound);
            chbUseDefaultNotificationSound.setChecked(Settings.getUseDefaultNotificationSound());
            chbUseDefaultNotificationSound.setOnClickListener(this);
        btnRestartTutorial=(Button)findViewById(R.id.btnRestartTutorial);
            btnRestartTutorial.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chbFastAccessStatus:
                Settings.setFastAccessStatus(chbFastAccessStatus.isChecked());
                break;
            case R.id.chbSendIfHaveNoNotes:
                Settings.setSendIfHaveNoNotes(chbSendIfHaveNoNotes.isChecked());
                break;
            case R.id.chbUseDefaultNotificationSound:
                Settings.setUseDefaultNotificationSound(chbUseDefaultNotificationSound.isChecked());
                break;
            case R.id.btnRestartTutorial:
                Tutorial.restartTutorial();
                Toast.makeText(this,"Tutorial was restarted",Toast.LENGTH_SHORT).show();
                btnRestartTutorial.setEnabled(false);
                break;
        }
    }
}