package cn.bertsir.cameralibary;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Bert on 2017/2/21.
 */
public class CameraUtils {

    public static CameraUtils instance = null;
    private SurfaceHolder mSurfaceholder;
    private SurfaceHolder.Callback callback;
    private static final String TAG = "CameraUtils";
    public static int CAMERA_BACK = 0;
    public static int CAMERA_FRONT = 1;
    private int current_camrea = CAMERA_BACK;

    public static CameraUtils getInstance() {
        if(instance == null){
            instance = new CameraUtils();
        }
        return instance;
    }

    public void openCamera(final SurfaceView mSv, final Context mContext){
        mSurfaceholder = mSv.getHolder();
        mSurfaceholder.setFormat(PixelFormat.TRANSPARENT);//translucent半透明 transparent透明
        mSurfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        callback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                CameraHelper.getInstance().operationCamera(true, (Activity) mContext,mSv,mSurfaceholder,current_camrea);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                CameraHelper.getInstance().operationCamera(false, (Activity) mContext,mSv,mSurfaceholder,current_camrea);
            }
        };
        mSurfaceholder.addCallback(callback);
    }

    public void openCamera(final SurfaceView mSv, final Context mContext, int is_front){
        current_camrea = is_front;
        mSurfaceholder = mSv.getHolder();
        mSurfaceholder.setFormat(PixelFormat.TRANSPARENT);//translucent半透明 transparent透明
        mSurfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        callback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                CameraHelper.getInstance().operationCamera(true, (Activity) mContext,mSv,mSurfaceholder,current_camrea);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                CameraHelper.getInstance().operationCamera(false, (Activity) mContext,mSv,mSurfaceholder,current_camrea);
            }
        };
        mSurfaceholder.addCallback(callback);
    }



    public void changeCamera(final SurfaceView mSv, final Context mContext){
        closeCamera(mSv,mContext);
        if(current_camrea == CAMERA_BACK){
            current_camrea = CAMERA_FRONT;
            CameraHelper.getInstance().operationCamera(true, (Activity) mContext,mSv,mSurfaceholder,current_camrea);
        }else {
            current_camrea = CAMERA_BACK;
            CameraHelper.getInstance().operationCamera(true, (Activity) mContext,mSv,mSurfaceholder,current_camrea);
        }

    }

    public void changeCamera(final SurfaceView mSv, final Context mContext,int direction){
        closeCamera(mSv,mContext);
            CameraHelper.getInstance().operationCamera(true, (Activity) mContext,mSv,mSurfaceholder,direction);
        current_camrea = direction;
    }



    public void closeCamera(final SurfaceView mSv, final Context mContext){
        CameraHelper.getInstance().operationCamera(false, (Activity) mContext,mSv,mSurfaceholder,current_camrea);
    }

}
