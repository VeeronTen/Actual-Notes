package veeronten.actualnotes.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

import veeronten.actualnotes.managers.FileManager;
import veeronten.actualnotes.managers.MyImageManager;

public class ImageActivity extends AppCompatActivity{
    File newImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        newImg = FileManager.createNewFile(FileManager.FileType.IMAGE);
        Uri photoURI = FileProvider.getUriForFile(this,
                "veeronten.actualnotes.fileProvider",
                newImg);

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(takePictureIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(newImg.toString(), options);

            Matrix matrix = new Matrix();
            matrix.postRotate(270);

            bitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap .getWidth(), bitmap .getHeight(), matrix, true);
            MyImageManager.savePhoto(bitmap);
        }
        FileManager.removeFile(newImg);
        finish();
    }

}