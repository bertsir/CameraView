package cn.bertsir.cameralibary.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import static android.content.ContentValues.TAG;


/**
 * Created by Bert on 2017/11/29.
 */

public class BlurPreviewView extends ImageView {


    private Bitmap mBitmap;

    public BlurPreviewView(Context context) {
        super(context);
    }

    public BlurPreviewView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void onDraw(Canvas canvas)
    {
        if(mBitmap != null){
            Matrix matrix = new Matrix();
            float scaleX = getScreenHeight() / (float) mBitmap.getWidth();
            float scaleY = getScreenWidth() / (float) mBitmap.getHeight();
            matrix.postRotate(90);
            matrix.preTranslate(0, -mBitmap.getHeight());
            matrix.postTranslate(0,mBitmap.getHeight());
            matrix.postTranslate(0,-mBitmap.getHeight());
            matrix.postScale(scaleX, scaleY);
            canvas.drawBitmap(mBitmap, matrix,null);
        }

    }

    public void setBlurFrame(Bitmap mBitmap){
        this.mBitmap = mBitmap;
        invalidate();
    }

    public int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) {
            return getContext().getResources().getDisplayMetrics().widthPixels;
        }
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.x;
    }

    public int getScreenHeight() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) {
            return getContext().getResources().getDisplayMetrics().heightPixels;
        }
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.y;
    }
}
