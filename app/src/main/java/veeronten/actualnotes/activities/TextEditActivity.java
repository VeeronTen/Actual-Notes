package veeronten.actualnotes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import veeronten.actualnotes.R;
import veeronten.actualnotes.Tutorial;
import veeronten.actualnotes.managers.FileManager;
import veeronten.actualnotes.managers.MyTextManager;

import static veeronten.actualnotes.managers.FileManager.FileType.TEXT;


public class TextEditActivity extends AppCompatActivity{
    EditText textEditor;
    File fileToEdit;
    Boolean saveWhenClosing;
    Boolean isChanged;
    Boolean isExternal;
    Menu mOptionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textedit);
        FileManager.start(getApplicationContext());

        saveWhenClosing = true;
        isChanged=false;
        isExternal=false;
        fileToEdit=null;

        textEditor =(EditText) findViewById(R.id.textEditor);
        textEditor.setText(getText());
        textEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                isChanged=true;
                menuUpdate();
            }
        });
        if(Tutorial.isFirstLaunch(this))
            Tutorial.startTutorial(this);
    }
    public boolean onCreateOptionsMenu(Menu menu){
        mOptionsMenu = menu;
        getMenuInflater().inflate(R.menu.menu_textedit, menu);
        menuUpdate();
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.item_save)
            save();
        else if(item.getItemId()==R.id.item_close) {
            saveWhenClosing=false;
            finish();
        }else if(item.getItemId()==R.id.item_delete){
            saveWhenClosing=false;
            FileManager.removeFile(fileToEdit);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onPause(){
        super.onPause();
        if(saveWhenClosing&isChanged&(!textEditor.getText().toString().equals(""))){
            save();
        }

        if(saveWhenClosing&isExternal&(!textEditor.getText().toString().equals(""))){
            save();
        }
    }
    private String getText(){
        Intent intent = getIntent();
        String answer;
        String path;

        answer= intent.getStringExtra(Intent.EXTRA_TEXT);

        if(answer!=null){
            isExternal=true;
            return answer;
        }
        path = intent.getStringExtra("path");
        if(path == null)
            answer = "";
        else {
            fileToEdit = new File(path);
            answer = MyTextManager.readFile(fileToEdit);
        }

        return  answer;
    }

    private void menuUpdate(){
        if(textEditor.getText().toString().equals("")||((!isExternal))&(!isChanged)) {
            mOptionsMenu.getItem(0).setEnabled(false);
            mOptionsMenu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_save_disabled));
            mOptionsMenu.getItem(1).setTitle("Close");
        }else if((!textEditor.getText().toString().equals(""))&(isChanged||isExternal)){
            mOptionsMenu.getItem(0).setEnabled(true);
            mOptionsMenu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_save));
            mOptionsMenu.getItem(1).setTitle("Close without saving");
        }
    }
    private void save(){
        if(fileToEdit==null)
            fileToEdit = FileManager.createNewFile(TEXT);
        MyTextManager.saveChanges(fileToEdit, textEditor.getText().toString());
        isChanged=false;
        isExternal=false;
        menuUpdate();
        Toast.makeText(this, "File was saved", Toast.LENGTH_SHORT).show();

    }
}
