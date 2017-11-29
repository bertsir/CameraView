package cn.bertsir.cameralibary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.support.v8.renderscript.ScriptIntrinsicYuvToRGB;
import android.support.v8.renderscript.Type;

/**
 * Created by Bert on 2017/11/28.
 */

public class CameraBlurUtils {

    private static CameraBlurUtils instance;
    private RenderScript rs;
    private ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
    private Type.Builder yuvType, rgbaType;
    private Allocation in, out;
    private ScriptIntrinsicBlur blurScript;


    public static CameraBlurUtils getInstance() {
        if(instance == null)
            instance = new CameraBlurUtils();
        return instance;
    }

    public void init(Context mContext){
        rs = RenderScript.create(mContext);
        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
        blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
    }

    public Bitmap blur(byte[] data, Camera camera,float blurvaule){
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        if (yuvType == null)
        {
            yuvType = new Type.Builder(rs, Element.U8(rs)).setX(data.length);
            in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

            rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(previewSize.width).setY(previewSize.height);
            out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);
        }

        in.copyFrom(data);

        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);

        Bitmap bmpout = Bitmap.createBitmap(previewSize.width, previewSize.height, Bitmap.Config.ARGB_8888);
        out.copyTo(bmpout);
        //return adjustPhotoRotation(blurBitmap(bmpout,blurvaule),90);
        return blurBitmap(bmpout,blurvaule);
    }


    /**
     * 模糊处理Bitmap
     * @param bitmap
     * @return
     */
    private Bitmap blurBitmap(Bitmap bitmap,float vaule) {
        // 用需要创建高斯模糊bitmap创建一个空的bitmap
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        // 初始化Renderscript，这个类提供了RenderScript context，
        // 在创建其他RS类之前必须要先创建这个类，他控制RenderScript的初始化，资源管理，释放
        // 创建高斯模糊对象

        // 创建Allocations，此类是将数据传递给RenderScript内核的主要方法，
        // 并制定一个后备类型存储给定类型
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
        // 设定模糊度
        blurScript.setRadius(vaule);
        // Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);
        // Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);
        // recycle the original bitmap
        bitmap.recycle();
        // After finishing everything, we destroy the Renderscript.
        rs.destroy();
        return outBitmap;
    }

    /**
     * 旋转Bitmap
     * @param bm
     * @param orientationDegree
     * @return
     */
    private Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree)
    {

        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        float targetX, targetY;
        if (orientationDegree == 90) {
            targetX = bm.getHeight();
            targetY = 0;
        } else {
            targetX = bm.getHeight();
            targetY = bm.getWidth();
        }

        final float[] values = new float[9];
        m.getValues(values);

        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];

        m.postTranslate(targetX - x1, targetY - y1);

        Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bm1);
        canvas.drawBitmap(bm, m, paint);
        return bm1;
    }
}
