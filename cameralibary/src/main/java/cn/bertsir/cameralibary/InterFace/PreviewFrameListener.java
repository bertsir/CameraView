package cn.bertsir.cameralibary.InterFace;

import android.hardware.Camera;

/**
 * Created by Bert on 2017/11/28.
 */

public interface PreviewFrameListener {

    void onPreviewFrameListener(byte[] data, Camera camera);
}
