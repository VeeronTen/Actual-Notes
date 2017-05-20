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
import veeronten.actualnotes.managers.MyTextManager;

import static veeronten.actualnotes.managers.FileManager.FileType.TEXT;


public class TextEditActivity extends AppCompatActivity{
    EditText textEditor;
    File fileToEdit;
    Boolean saveWhenClosing;
    //ViewGroup layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textedit);

        FileManager.start(getApplicationContext());

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
            MyTextManager.saveChanges(fileToEdit, textEditor.getText().toString());
            Toast.makeText(this, "File was saved", Toast.LENGTH_SHORT).show();
        }else if(item.getItemId()==R.id.item_close) {
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
    public void onPause() {
        super.onPause();
        if(saveWhenClosing) {
            if(textEditor.getText().toString().equals(""))
                FileManager.removeFile(fileToEdit);
            else {
                MyTextManager.saveChanges(fileToEdit, textEditor.getText().toString());
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
            fileToEdit = FileManager.createNewFile(TEXT);
            return answer;
        }


        path = intent.getStringExtra("path");
        if(path == null)
            fileToEdit = FileManager.createNewFile(TEXT);
        else
            fileToEdit = new File(path);
        answer = MyTextManager.readFile(fileToEdit);
        return  answer;
    }
}
