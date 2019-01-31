
package com.example.apurvchaudhary.cameratest.others;

import android.graphics.Bitmap;
import android.media.Image;
import android.support.annotation.NonNull;

import com.example.apurvchaudhary.cameratest.faceListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;


public abstract class VisionProcessorBase<T> implements VisionImageProcessor {

    // Whether we should ignore process(). This is usually caused by feeding input data faster than
    // the model can handle.
    private final AtomicBoolean shouldThrottle = new AtomicBoolean(false);

    public VisionProcessorBase() {
    }

    @Override
    public void process(
            ByteBuffer data, final FrameMetadata frameMetadata, final GraphicOverlay
            graphicOverlay) {
        if (shouldThrottle.get()) {
            return;
        }
//        FirebaseVisionImageMetadata metadata =
//                new FirebaseVisionImageMetadata.Builder()
//                        .setWidth(frameMetadata.getWidth())
//                        .setHeight(frameMetadata.getHeight())
//                        .setRotation(frameMetadata.getRotation())
//                        .build();
//
//        detectInVisionImage(
//                FirebaseVisionImage.fromByteBuffer(data, metadata), frameMetadata, graphicOverlay);
    }

    // Bitmap version
    @Override
    public void process(Bitmap bitmap, final GraphicOverlay
            graphicOverlay) {
        if (shouldThrottle.get()) {
            return;
        }
        detectInVisionImage(FirebaseVisionImage.fromBitmap(bitmap),bitmap, null, graphicOverlay, null);
    }

    /**
     * Detects feature from given media.Image
     *
     * @return created FirebaseVisionImage
     */
    @Override
    public void process(Image image, int rotation, final GraphicOverlay graphicOverlay) {
        if (shouldThrottle.get()) {
            return;
        }
        // This is for overlay display's usage
//        FrameMetadata frameMetadata =
//                new FrameMetadata.Builder().setWidth(image.getWidth()).setHeight(image.getHeight
//                        ()).build();
//        FirebaseVisionImage fbVisionImage =
//                FirebaseVisionImage.fromMediaImage(image, rotation);
//        detectInVisionImage(fbVisionImage, frameMetadata, graphicOverlay);
    }

    private void detectInVisionImage(
            FirebaseVisionImage image,
            final Bitmap bitmap,
            final FrameMetadata metadata,
            final GraphicOverlay graphicOverlay, final faceListener faceListener) {
        detectInImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<T>() {
                            @Override
                            public void onSuccess(T results) {
                                shouldThrottle.set(false);
                                VisionProcessorBase.this.onSuccess(bitmap,results, metadata,
                                        graphicOverlay,faceListener);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                shouldThrottle.set(false);
                                VisionProcessorBase.this.onFailure(e);
                            }
                        });
        // Begin throttling until this frame of input has been processed, either in onSuccess or
        // onFailure.
        shouldThrottle.set(true);
    }

    @Override
    public void stop() {
    }

    protected abstract Task<T> detectInImage(FirebaseVisionImage image);

    protected abstract void onSuccess(
            @NonNull Bitmap bitmap,
            @NonNull T results,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay,
            @NonNull faceListener faceListener);

    protected abstract void onFailure(@NonNull Exception e);

    public  void process(Bitmap bitmapForDebugging, GraphicOverlay mGraphicOverlay, faceListener faceListener){
        if (shouldThrottle.get()) {
            return;
        }
        detectInVisionImage(FirebaseVisionImage.fromBitmap(bitmapForDebugging),bitmapForDebugging,null, mGraphicOverlay,faceListener);
    }
}
