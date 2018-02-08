package cn.bertsir.cameralibary;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

import cn.bertsir.cameralibary.InterFace.PreviewFrameListener;

/**
 * Created by Bert on 2017/2/21.
 */
public class CameraHelper {
    private static CameraHelper instance = null;
    private Camera mCamera;
    private String camResolution;
    private String camPicResolution;
    private float proportion = 0.f;
    private static final String TAG = "CameraHelper";
    public static int CAMERA_BACK = 0;
    public static int CAMERA_FRONT = 1;
    private int current_camrea = CAMERA_BACK;

    public void setmPreviewFrameListener(PreviewFrameListener mPreviewFrameListener) {
        this.mPreviewFrameListener = mPreviewFrameListener;
    }

    private PreviewFrameListener mPreviewFrameListener;


    public static CameraHelper getInstance() {
        if (instance == null) {
            instance = new CameraHelper();
        }
        return instance;
    }


    /**
     * 打开或者关闭摄像头
     *
     * @param open
     * @param activity
     * @param sv
     * @param mSurfaceholder
     */
    public void operationCamera(boolean open, Activity activity, SurfaceView sv, SurfaceHolder mSurfaceholder,int
            back) {

        if(Camera.getNumberOfCameras() >1){
            current_camrea = back;
        }else {
            current_camrea = Camera.CameraInfo.CAMERA_FACING_BACK;
            Log.e(TAG, "This phone only have one camera" );
        }
        if (open) {
            try {
                if(current_camrea == 0){
                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                }else {
                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                }
//
//                setCameraDisplayOrientation(activity, back,
//                        mCamera);
                Camera.Parameters parameters = mCamera.getParameters();
                if(current_camrea == CAMERA_BACK){
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                }
                    calculateRatio(activity);
                    calculatePicRatio(activity);
                    String[] xes = camResolution.split("x");
                    if(Integer.valueOf(xes[0]) > Integer.valueOf(xes[1])){
                        parameters.setPreviewSize(Integer.valueOf(xes[0]) , Integer.valueOf(xes[1]));
                    }else {
                        parameters.setPreviewSize(Integer.valueOf(xes[1]) , Integer.valueOf(xes[0]));
                    }
                    String[] pes = camPicResolution.split("x");
                    if(Integer.valueOf(pes[0]) > Integer.valueOf(pes[1])){
                        parameters.setPictureSize(Integer.valueOf(pes[0]) , Integer.valueOf(pes[1]));
                    }else {
                        parameters.setPictureSize(Integer.valueOf(pes[1]) , Integer.valueOf(pes[0]));
                    }
                mCamera.setParameters(parameters);
                mCamera.setPreviewDisplay(mSurfaceholder);
                mCamera.setDisplayOrientation(90);
                mCamera.startPreview();

                mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        if(mPreviewFrameListener != null){
                            mPreviewFrameListener.onPreviewFrameListener(data,camera);
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                sv.setBackgroundColor(Color.BLACK);
                Log.e(TAG, "open camera error："+e.toString() );
            }
        } else {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);
                mCamera.release();
                mCamera = null;
                Log.e(TAG, "close camera");
            }

        }

    }


    /**
     * 旋转摄像头方向
     *
     * @param activity
     * @param cameraId
     * @param camera
     */
    private void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int degrees = getDisplayRotation(activity);
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }


    /**
     * 获得摄像头方向
     *
     * @param activity
     * @return
     */
    private int getDisplayRotation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
        }
        return 0;
    }

    /**
     * 计算适合的预览分辨率
     */
    public void calculateRatio(Context mContext) {
        String screen = CUtilts.getInstance().getScreen(mContext);
        String[] split = screen.split("---");
        int widthp = Integer.parseInt(split[0]);
        int heightp = Integer.parseInt(split[1]);
        if(widthp > heightp){
            proportion = (float)widthp/heightp;
        }else{
            proportion = (float)heightp/widthp;
        }
        float q = 0.f;
        float c = 1.f;
        Camera.Parameters pm = mCamera.getParameters();
        List<Camera.Size> supportedPreviewSizes = pm.getSupportedPreviewSizes();
        ArrayList<String> listSize = new ArrayList<String>();
        ArrayList<String> listpor = new ArrayList<String>();

        for (int i = 0; i < supportedPreviewSizes.size(); i++) {
            int width = supportedPreviewSizes.get(i).width;
            int height = supportedPreviewSizes.get(i).height;
            if (width > height) {
                q = (float) width / height;
                if (q == proportion) {
                    listSize.add(width + "x" + height);
                }
                if (Math.abs(proportion - q) < c) {
                    c = Math.abs(proportion - q);
                    listpor.add(width + "x" + height);
                }
            } else {
                q = (float) height / width;
                if (q == proportion) {
                    listSize.add(height + "x" + width);
                }
                if (Math.abs(proportion - q) < c) {
                    c = Math.abs(proportion - q);
                    listpor.add(width + "x" + height);
                }
            }
        }

        if (listSize.size() > 0) {
            String a = listSize.get(0);
            String b = listSize.get(listSize.size() - 1);
            String[] as = a.split("x");
            String[] bs = b.split("x");
            int min = Integer.valueOf(as[0]);
            int max = Integer.valueOf(bs[0]);
            if (min < max) {
                camResolution = listSize.get(0);
            } else {
                camResolution = listSize.get(listSize.size() - 1);
            }
        } else {
            camResolution = listpor.get(listpor.size() - 1);
        }
    }

    /**
     * 计算合适的拍照分辨率
     */
    public void calculatePicRatio(Context mContext) {
        String screen = CUtilts.getInstance().getScreen(mContext);
        String[] split = screen.split("---");
        int widthp = Integer.parseInt(split[0]);
        int heightp = Integer.parseInt(split[1]);
        if(widthp > heightp){
            proportion = (float)widthp/heightp;
        }else{
            proportion = (float)heightp/widthp;
        }
        float q = 0.f;
        float c = 1.f;
        Camera.Parameters pm = mCamera.getParameters();
        List<Camera.Size> supportedPreviewSizes = pm.getSupportedPictureSizes();
        ArrayList<String> listSize = new ArrayList<String>();
        ArrayList<String> listpor = new ArrayList<String>();

        for (int i = 0; i < supportedPreviewSizes.size(); i++) {
            int width = supportedPreviewSizes.get(i).width;
            int height = supportedPreviewSizes.get(i).height;
            if (width > height) {
                q = (float) width / height;
                if (q == proportion) {
                    listSize.add(width + "x" + height);
                }
                if (Math.abs(proportion - q) < c) {
                    c = Math.abs(proportion - q);
                    listpor.add(width + "x" + height);
                }
            } else {
                q = (float) height / width;
                if (q == proportion) {
                    listSize.add(height + "x" + width);
                }
                if (Math.abs(proportion - q) < c) {
                    c = Math.abs(proportion - q);
                    listpor.add(width + "x" + height);
                }
            }
        }

        if (listSize.size() > 0) {
            String a = listSize.get(0);
            String b = listSize.get(listSize.size() - 1);
            String[] as = a.split("x");
            String[] bs = b.split("x");
            int min = Integer.valueOf(as[0]);
            int max = Integer.valueOf(bs[0]);
            if (min < max) {
                camPicResolution = listSize.get(0);
            } else {
                camPicResolution = listSize.get(listSize.size() - 1);
            }
        } else {
            camPicResolution = listpor.get(listpor.size() - 1);
        }
    }

    /**
     * 拍照
     * @param mTakeSuccess
     * @return
     */
    public Bitmap takePhoto(final takeSuccess mTakeSuccess) {
        final Camera.Parameters cameraParams = mCamera.getParameters();
        cameraParams.setPictureFormat(ImageFormat.JPEG);

        if(current_camrea == CAMERA_BACK){
            cameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            cameraParams.setRotation(90);
        }else {
            cameraParams.setRotation(270);
        }
        mCamera.setParameters(cameraParams);
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Bitmap cameraBitmap = CUtilts.getInstance().Bytes2Bitmap(data);
                //防止三星等机型造成的照片方向不对
                if(cameraBitmap.getWidth() > cameraBitmap.getHeight()) {
                    if(checkCameraIsFront()){
                        cameraBitmap = CUtilts.getInstance().rotaingImageView(270, cameraBitmap);
                    }else {
                        cameraBitmap = CUtilts.getInstance().rotaingImageView(90, cameraBitmap);
                    }
                }
                mTakeSuccess.success(cameraBitmap);
                mCamera.startPreview();
            }
        });
        return null;
    }

    public  boolean checkCameraIsFront() {
       Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(current_camrea, info);
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return true;
        } else {  // 后置摄像头
            return false;
        }
    }

    public interface takeSuccess{
        void success(Bitmap mBitmap);
    }




}
