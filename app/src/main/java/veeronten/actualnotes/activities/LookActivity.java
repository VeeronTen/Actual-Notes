package veeronten.actualnotes.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;

import uk.co.senab.photoview.PhotoViewAttacher;
import veeronten.actualnotes.R;

public class LookActivity extends AppCompatActivity {

    PhotoViewAttacher mAttacher;
    ImageView ivBigImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        ivBigImage = (ImageView)findViewById(R.id.ivBigImage);

        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        Bitmap bitmap = BitmapFactory.decodeFile(path);

        ivBigImage.setImageBitmap(bitmap);
        mAttacher = new PhotoViewAttacher(ivBigImage);
    }
}
