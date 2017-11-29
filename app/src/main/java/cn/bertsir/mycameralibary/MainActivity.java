package cn.bertsir.mycameralibary;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import java.io.File;

import cn.bertsir.cameralibary.CameraBlurUtils;
import cn.bertsir.cameralibary.InterFace.PreviewFrameListener;
import cn.bertsir.cameralibary.view.BlurPreviewView;
import cn.bertsir.cameralibary.view.CameraView;

public class MainActivity extends Activity implements View.OnClickListener {

    private CameraView cv;
    private Button bt_change;
    private Button bt_take;
    private Button bt_takeandwater;
    private Button bt_blur;
    private BlurPreviewView bpv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        cv = (CameraView) findViewById(R.id.cv);
        cv.open(MainActivity.this);
        bt_change = (Button) findViewById(R.id.bt_change);
        bt_change.setOnClickListener(this);
        bt_take = (Button) findViewById(R.id.bt_take);
        bt_take.setOnClickListener(this);
        bt_takeandwater = (Button) findViewById(R.id.bt_takeandwater);
        bt_takeandwater.setOnClickListener(this);
        bt_blur = (Button) findViewById(R.id.bt_blur);
        bt_blur.setOnClickListener(this);
        bpv = (BlurPreviewView) findViewById(R.id.bpv);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_change:
                cv.ChangeCamera();
                break;
            case R.id.bt_take:
                cv.takePhoto(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + System.currentTimeMillis() +
                        ".jpg");
                break;
            case R.id.bt_takeandwater:
                String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + System
                        .currentTimeMillis() + ".jpg";
                cv.takePhotoAddWaterMask(BitmapFactory.decodeResource(getResources(), R.drawable.cb), 50, 50, savePath);
                break;
            case R.id.bt_blur:
                CameraBlurUtils.getInstance().init(this);
                cv.setPreviewFrameListener(new PreviewFrameListener() {
                    @Override
                    public void onPreviewFrameListener(byte[] data, Camera camera) {
                        bpv.setBlurFrame(CameraBlurUtils.getInstance().blur(data,camera,15f));
                    }
                });
                break;
        }
    }
}
