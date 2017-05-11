# CameraView
An Android CameraView

###使用方法：

引入：

compile 'cn.bertsir.Cameralibary:cameralibary:1.0.1'

布局:


        <cn.bertsir.cameralibary.CameraView
            android:id="@+id/cv"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:direction="BACK"
            ></cn.bertsir.cameralibary.CameraView>


代码：

<pre>
 cv = (CameraView) findViewById(R.id.cv);
 cv.open(MainActivity.this);
</pre>


OK,就这么简单，一个摄像头View就创建好了

支持的功能：

    /**
     * 关闭相机
     * @param mActivity
     */
    public void close(Activity mActivity)

    /**
     * 切换摄像头（相反切换）
     */
    public void ChangeCamera()

    /**
     * 切换摄像头（指定切换）
     */
    public void ChangeCamera(int direction)
    /**
     * 拍照
     * @param mTakeSuccess
     * @return
     */
    public Bitmap takePhoto(CameraHelper.takeSuccess mTakeSuccess)

    /**
     * 拍照并添加水印
     * @param waterMask
     * @param paddingLeft
     * @param paddingTop
     * @return
     */
    public void takePhotoAddWaterMask(final Bitmap waterMask, final int paddingLeft, final int paddingTop,
                                      final WaterImageListener mWaterImageListener
	  /**
     * 判断当前相机是否是前置摄像头
     * @return
     */
    public Boolean currentCameraIsFront()


具体使用方法参考Demo


