package cn.bertsir.cameralibary;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import cn.bertsir.cameralibary.InterFace.WaterImageListener;

/**
 * Created by Bert on 2017/5/5.
 */

public class CameraView extends FrameLayout {

    private View rootView;
    private SurfaceView sfv_camera_view;
    private Activity mActivity;
    public static int CAMERA_BACK = 0;
    public static int CAMERA_FRONT = 1;
    private int current_camrea = CAMERA_BACK;
    private  Bitmap waterMaskBitmap = null;
    private boolean camera_is_open = false;


    private enum Position {
        BACK,
        FRONT,
    }


    public CameraView(@NonNull Context context) {
        super(context,null);
    }

    public CameraView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CameraView);
        current_camrea = a.getInt(R.styleable.CameraView_direction, Position.BACK.ordinal());
        a.recycle();
        initView(context);
    }

    private void initView(Context context){
        rootView = View.inflate(context, R.layout.layout_camera_view, this);
        sfv_camera_view = (SurfaceView) rootView.findViewById(R.id.sfv_camera_view);
    }

    /**
     * 打开相机
     * @param mActivity
     */
    public void open(Activity mActivity){
        this.mActivity = mActivity;
        CameraUtils.getInstance().openCamera(sfv_camera_view,mActivity,current_camrea);
        camera_is_open = true;
    }

    /**
     * 关闭相机
     * @param mActivity
     */
    public void close(Activity mActivity){
        if(cameraIsOpen()){
            CameraUtils.getInstance().closeCamera(sfv_camera_view,mActivity);
        }

    }

    /**
     * 切换摄像头（相反切换）
     */
    public void ChangeCamera(){
        if(cameraIsOpen())
        CameraUtils.getInstance().changeCamera(sfv_camera_view,mActivity);
    }

    /**
     * 切换摄像头（指定切换）
     */
    public void ChangeCamera(int direction){
        if(cameraIsOpen())
        CameraUtils.getInstance().changeCamera(sfv_camera_view,mActivity,direction);
        current_camrea = direction;
    }

    /**
     * 拍照
     * @param mTakeSuccess
     * @return
     */
    public Bitmap takePhoto(CameraHelper.takeSuccess mTakeSuccess){
        return CameraHelper.getInstance().takePhoto(mTakeSuccess);
    }

    /**
     * 拍照
     * @param ImgPath
     * @return
     */
    public void takePhoto(final String ImgPath){
        CameraHelper.getInstance().takePhoto(new CameraHelper.takeSuccess() {
            @Override
            public void success(Bitmap mBitmap) {
                 CUtilts.getInstance().saveBitmap(mBitmap, ImgPath);
            }
        });
    }

    /**
     * 拍照并添加水印
     * @param waterMask
     * @param paddingLeft
     * @param paddingTop
     * @return
     */
    public void takePhotoAddWaterMask(final Bitmap waterMask, final int paddingLeft, final int paddingTop,
                                      final WaterImageListener mWaterImageListener){
        CameraHelper.getInstance().takePhoto(new CameraHelper.takeSuccess() {
            @Override
            public void success(Bitmap mBitmap) {
                waterMaskBitmap = CUtilts.getInstance().createWaterMaskBitmap(mBitmap, waterMask, paddingLeft, paddingTop);
                mWaterImageListener.Success(waterMaskBitmap);
            }
        });

    }

    /**
     * 拍照并添加水印
     * @param waterMask
     * @param paddingLeft
     * @param paddingTop
     * @return
     */
    public void takePhotoAddWaterMask(final Bitmap waterMask, final int paddingLeft, final int paddingTop, final String
            savePath){
        takePhotoAddWaterMask(waterMask, paddingLeft, paddingTop, new WaterImageListener() {
            @Override
            public void Success(Bitmap mBitmap) {
                CUtilts.getInstance().saveBitmap(mBitmap,savePath);
            }
        });
    }

    /**
     * 判断当前相机是否是前置摄像头
     * @return
     */
    public Boolean currentCameraIsFront(){
       return CameraHelper.getInstance().checkCameraIsFront();
    }


    private boolean cameraIsOpen(){
        return camera_is_open;
    }
}
