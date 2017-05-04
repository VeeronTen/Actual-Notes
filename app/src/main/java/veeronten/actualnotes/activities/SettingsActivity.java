package veeronten.actualnotes.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;

import veeronten.actualnotes.R;
import veeronten.actualnotes.managers.MyNotificationManager;


public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    CheckBox chbFastAccessStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        chbFastAccessStatus= (CheckBox)findViewById(R.id.chbFastAccessStatus);
            chbFastAccessStatus.setChecked(MyNotificationManager.getFastAccessStatus());
            chbFastAccessStatus.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chbFastAccessStatus:
                MyNotificationManager.setFastAccessStatus(chbFastAccessStatus.isChecked());
                break;
        }
    }
}