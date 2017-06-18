package veeronten.actualnotes.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import veeronten.actualnotes.L;
import veeronten.actualnotes.managers.FileManager;
import veeronten.actualnotes.managers.MyImageManager;

public class ImageActivity extends AppCompatActivity{
    File newImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FileManager.start(getApplicationContext());
        Intent receiveIntent=getIntent();
        if(receiveIntent.getAction().equals("android.intent.action.SEND")) {
            receiveImage();
            finish();
        }else if(receiveIntent.getAction().equals("actualnotes.intent.action.START_CAM")){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            newImg = FileManager.createNewFile(FileManager.FileType.IMAGE);
            Uri photoURI = FileProvider.getUriForFile(this,
                    "veeronten.actualnotes.fileProvider",
                    newImg);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            List<ResolveInfo> resInfoList = getApplicationContext().getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                getApplicationContext().grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            startActivityForResult(takePictureIntent, 1);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            FileManager.deleteLastFromDCIM();
            MyImageManager.rotateImage(newImg);
            MyImageManager.matchMini(newImg);
        }else
            FileManager.removeFile(newImg);
        finish();
    }
    private void receiveImage() {
        Uri uri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
        L.i("source uri: "+uri.getPath());
        File destinationFile = FileManager.createNewFile(FileManager.FileType.IMAGE);
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(getContentResolver().openInputStream(uri));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFile, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while(bis.read(buf) != -1);
            L.i("image was received");
            Toast.makeText(this, "Image was received",Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            L.printStackTrace(e);
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                L.printStackTrace(e);
            }
        }
        MyImageManager.matchMini(destinationFile);
    }
}