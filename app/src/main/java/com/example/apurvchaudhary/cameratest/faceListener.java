package com.example.apurvchaudhary.cameratest;

import android.graphics.Bitmap;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;

public interface faceListener {

    void faceDetected(FirebaseVisionFace face, Bitmap bitmap);
}
