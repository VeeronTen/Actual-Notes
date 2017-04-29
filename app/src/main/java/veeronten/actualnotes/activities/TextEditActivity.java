package veeronten.actualnotes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import veeronten.actualnotes.R;
import veeronten.actualnotes.managers.FileManager;


public class TextEditActivity extends AppCompatActivity{
    EditText textEditor;
    FileManager fileManager;
    File fileToEdit;
    Boolean saveWhenClosing;
    //ViewGroup layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textedit);

        if(FileManager.getInstance()==null)
            new FileManager(getApplicationContext());
        fileManager = fileManager.getInstance();

        //layout = (LinearLayout) findViewById(R.id.activity_textedit);
        textEditor =(EditText) findViewById(R.id.textEditor);

        saveWhenClosing = true;



        textEditor.setText(getText());

        //layout.setBackgroundColor(getResources().getColor(R.color.textBack));
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_textedit, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.item_save) {
            fileManager.text.saveChanges(fileToEdit, textEditor.getText().toString());
            Toast.makeText(this, "File was saved", Toast.LENGTH_SHORT).show();
        }else if(item.getItemId()==R.id.item_close) {
            saveWhenClosing=false;
            finish();
        }else if(item.getItemId()==R.id.item_delete){
            saveWhenClosing=false;
            fileManager.text.removeFile(fileToEdit);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onPause() {
        super.onPause();
        if(saveWhenClosing) {
            if(textEditor.getText().toString().equals(""))
                fileManager.text.removeFile(fileToEdit);
            else {
                fileManager.text.saveChanges(fileToEdit, textEditor.getText().toString());
                Toast.makeText(this, "File was saved", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getText(){
        Intent intent = getIntent();
        String answer;
        String path;

        answer= intent.getStringExtra(Intent.EXTRA_TEXT);

        if(answer!=null){
            fileToEdit = fileManager.text.createNewFile();
            return answer;
        }


        path = intent.getStringExtra("path");
        if(path == null)
            fileToEdit = fileManager.text.createNewFile();
        else
            fileToEdit = new File(path);
        answer = fileManager.text.readFile(fileToEdit);
        return  answer;
    }
}
