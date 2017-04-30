package veeronten.actualnotes.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import veeronten.actualnotes.L;
import veeronten.actualnotes.R;
import veeronten.actualnotes.managers.FileManager;
import veeronten.actualnotes.managers.MyImageManager;

public class ImageActivity extends AppCompatActivity implements View.OnClickListener {
    FileManager fileManager;
    SurfaceView surfaceView;
    ViewGroup vg;
    Camera camera;
    //Boolean meteringAreaSupported;
    Boolean took;
    Bitmap BitmapMain;
    ImageButton btnOk;
    ImageButton btnAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image);

        //meteringAreaSupported=false;
        // если хотим, чтобы приложение было полноэкранным
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        if(FileManager.getInstance()==null)
            new FileManager(getApplicationContext());
        fileManager = fileManager.getInstance();

        vg=(ViewGroup)findViewById(R.id.activity_image);
            vg.setOnClickListener(this);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);

        btnOk = (ImageButton)findViewById(R.id.btnOk);
            btnOk.setVisibility(View.INVISIBLE);
            btnOk.setOnClickListener(this);
        btnAgain = (ImageButton)findViewById(R.id.btnAgain);
            btnAgain.setVisibility(View.INVISIBLE);
            btnAgain.setOnClickListener(this);

        took=false;

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    Camera.Parameters params = camera.getParameters();
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    camera.setParameters(params);
                    camera.setPreviewDisplay(holder);
                    camera.setDisplayOrientation(90);
                    Camera.Size previewSize = camera.getParameters().getPreviewSize();
//
                    ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();
                    L.d("camH " + previewSize.height);
                    L.d("camW " + previewSize.width);
                    L.d("surH " + surfaceView.getHeight());
                    L.d("surW " + surfaceView.getWidth());

                    int camH = previewSize.width;
                    int camW = previewSize.height;
//
                    int dif = surfaceView.getWidth() - camW;
                    float coaf = (float) dif / (float) camW;

                    L.d(coaf + "");
//                    // здесь корректируем размер отображаемого preview, чтобы не было искажений
//

                    // портретный вид

                    lp.height = previewSize.width + dif;
                    lp.width = (int) (previewSize.height * (1 + coaf));
                    // (int) (previewSurfaceHeight / aspect);

//
                    surfaceView.setLayoutParams(lp);


                    camera.startPreview();
                } catch (Exception e) {
                    L.printStackTrace(e);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
                Camera.Parameters p = camera.getParameters();
//                if (p.getMaxNumMeteringAreas() > 0) {
//                    meteringAreaSupported = true;
//                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera = Camera.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null)
            camera.release();
        camera = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnOk:
                if(MyImageManager.savePhoto(BitmapMain))
                    Toast.makeText(this, "File was saved", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.btnAgain:

                recreate();
                break;
            case R.id.activity_image:
                if(took)
                    break;
                took=true;
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        try {
                            Bitmap originBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                            Matrix matrix = new Matrix();
                            matrix.postRotate(90);

                            BitmapMain = Bitmap.createBitmap(originBitmap , 0, 0, originBitmap .getWidth(), originBitmap .getHeight(), matrix, true);

                            btnOk.setVisibility(View.VISIBLE);
                            btnAgain.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            L.d("omg", e);
                        }
                    }
                });

                break;
        }

    }

}
