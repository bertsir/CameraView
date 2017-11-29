package cn.bertsir.cameralibary.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;


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
            float scaleX = getMeasuredHeight() / (float) mBitmap.getWidth();
            float scaleY = getMeasuredWidth() / (float) mBitmap.getHeight();
            matrix.postScale(scaleX, scaleY);
            matrix.postRotate(90);
            Bitmap dstbmp = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(),
                    matrix, true);
            canvas.drawBitmap(dstbmp, 0, 0, null);
        }
    }

    public void setBlurFrame(Bitmap mBitmap){
        this.mBitmap = mBitmap;
        invalidate();
    }
}
