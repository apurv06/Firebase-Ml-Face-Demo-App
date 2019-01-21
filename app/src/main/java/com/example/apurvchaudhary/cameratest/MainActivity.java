package com.example.apurvchaudhary.cameratest;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.apurvchaudhary.cameratest.models.APIFaceData;
import com.example.apurvchaudhary.cameratest.others.Camera2Source;
import com.example.apurvchaudhary.cameratest.others.CameraSource;
import com.example.apurvchaudhary.cameratest.others.CameraSourcePreview;
import com.example.apurvchaudhary.cameratest.others.FaceDetectionProcessor;
import com.example.apurvchaudhary.cameratest.others.FaceGraphic;
import com.example.apurvchaudhary.cameratest.others.GraphicOverlay;
import com.example.apurvchaudhary.cameratest.retrofit.APIClient;
import com.example.apurvchaudhary.cameratest.retrofit.APIInterface;
import com.example.apurvchaudhary.cameratest.utils.Preferences;
import com.example.apurvchaudhary.cameratest.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Camera";
    private Context context;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int REQUEST_STORAGE_PERMISSION = 201;
    private TextView cameraVersion;
    private ImageView ivAutoFocus;

    DatabaseReference mDbReference;


    private String mInTime;
    private String mOutTime;

    // CAMERA VERSION ONE DECLARATIONS
    private CameraSource mCameraSource = null;

    // CAMERA VERSION TWO DECLARATIONS
    private Camera2Source mCamera2Source = null;

    // COMMON TO BOTH CAMERAS
    private CameraSourcePreview mPreview;
    private FirebaseVisionFaceDetector previewFaceDetector = null;
    private GraphicOverlay mGraphicOverlay;
    private FaceGraphic mFaceGraphic;
    private boolean wasActivityResumed = false;
    private boolean isRecordingVideo = false;
    private Button switchButton;
    Button takeImage;


    // DEFAULT CAMERA BEING OPENED
    private boolean usingFrontCamera = true;

    // MUST BE CAREFUL USING THIS VARIABLE.
    // ANY ATTEMPT TO START CAMERA2 ON API < 21 WILL CRASH.
    private boolean useCamera2 = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        FirebaseApp.initializeApp(this);
        switchButton = (Button) findViewById(R.id.fab_switch);
        takeImage=findViewById(R.id.fab);
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);
        ivAutoFocus = (ImageView) findViewById(R.id.ivAutoFocus);
        if(checkGooglePlayAvailability()) {
            requestPermissionThenOpenCamera();

            takeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchButton.setEnabled(false);
                    if(useCamera2) {
                        if(mCamera2Source != null)mCamera2Source.takePicture(camera2SourceShutterCallback, camera2SourcePictureCallback);
                    } else {
                        if(mCameraSource != null)mCameraSource.takePicture(cameraSourceShutterCallback, cameraSourcePictureCallback);
                    }
                }
                });

            switchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(usingFrontCamera) {
                        stopCameraSource();
                        createCameraSourceBack();
                        usingFrontCamera = false;
                    } else {
                        stopCameraSource();
                        createCameraSourceFront();
                        usingFrontCamera = true;
                    }
                }
            });


            mPreview.setOnTouchListener(CameraPreviewTouchListener);
        }
    }

    final Camera2Source.PictureCallback camera2SourcePictureCallback = new Camera2Source.PictureCallback() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onPictureTaken(Image img) {
            Log.d(TAG, "Taken picture is here!");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switchButton.setEnabled(true);
                }
            });
            ByteBuffer buffer = img.getPlanes()[0].getBuffer();

            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            final Bitmap picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
            img.close();
            FileOutputStream out = null;
            try {


                FirebaseVisionFaceDetectorOptions options = new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
                        .enableTracking()
                        .setMinFaceSize(0.15f).build();
                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(picture);
                FirebaseVisionFaceDetector detector = FirebaseVision.getInstance().getVisionFaceDetector(options);
                detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {

                    private byte[] getCroppedImage(Rect rect){

                        Bitmap croppedBmp;
                        int x=rect.left;
                        int y=rect.top;
                        int w=rect.right - rect.left;
                        int h=rect.bottom - rect.top;
                        int maxH=picture.getHeight();
                        int maxW=picture.getWidth();

                        if(x<0)
                            x=0;
                        if(y<0)
                            y=0;

                        if(x+w<=maxW&&y+h<=maxH)
                            croppedBmp = Bitmap.createBitmap(picture, x, y,w,h );
                        else
                        {
                            if (x+w>maxW&&y+h>maxH)
                            {
                                croppedBmp=Bitmap.createBitmap(picture,0,0,maxW,maxH);
                            }
                            else if(x+w>maxW)
                            {
                                croppedBmp=Bitmap.createBitmap(picture,0,y,maxW,h);
                            }
                            else
                            {
                                croppedBmp=Bitmap.createBitmap(picture,x,0,w,maxH);
                            }
                        }
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        return stream.toByteArray();
                    }

                    @Override
                    public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                        Toast.makeText(MainActivity.this, firebaseVisionFaces.size() + " Faces",
                                Toast.LENGTH_SHORT).show();
                        for(FirebaseVisionFace face : firebaseVisionFaces) {
                            MediaType mediaType = MediaType.parse("multipart/form-data");
                            RequestBody requestBody = RequestBody.create(mediaType, getCroppedImage(face.getBoundingBox()));
                            MultipartBody.Part body = MultipartBody.Part.createFormData("img1", "a.jpg", requestBody);
                            RequestBody apiKey = RequestBody.create(mediaType, Constant.API_KEY);
                            RequestBody secretKey = RequestBody.create(mediaType, Constant.API_SECRET);
                            RequestBody folderKey = RequestBody.create(mediaType, "DEMO");

                            APIInterface apiInterface = APIClient.getRetrofit(MainActivity.this).create(APIInterface.class);
                            Call<APIFaceData> data = apiInterface.faceSearch(body, secretKey, apiKey, folderKey);
                            data.enqueue(new Callback<APIFaceData>() {
                                @Override
                                public void onResponse(@NonNull Call<APIFaceData> call, @NonNull Response<APIFaceData> response) {
                                    if(response.body() == null || response.body().getResult().size() == 0) {
                                        Toast.makeText(MainActivity.this, "Not Detected", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    final String faceTag = response.body().getResult().get(0).getPerson();
                                    View parentLayout = findViewById(R.id.parent_home);
                                    final Snackbar snackbar = Snackbar.make(parentLayout, "Attendance for employee Id "+faceTag+" registered", Snackbar.LENGTH_INDEFINITE);
                                    snackbar.setAction("Ok", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (snackbar.isShown())
                                            {
                                                snackbar.dismiss();
                                            }
                                        }
                                    });
                                    snackbar.show();

                                }

                                @Override
                                public void onFailure(@NonNull Call<APIFaceData> call, @NonNull Throwable t) {
                                    Utils.showErrorDialog(MainActivity.this, "API Error!", t.getLocalizedMessage());
                                }
                            });
                        }
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    final CameraSource.ShutterCallback cameraSourceShutterCallback = new CameraSource.ShutterCallback() {@Override public void onShutter() {Log.d(TAG, "Shutter Callback!");}};
    final CameraSource.PictureCallback cameraSourcePictureCallback = new CameraSource.PictureCallback() {
        @Override
        public void onPictureTaken(Bitmap picture) {
            Log.d(TAG, "Taken picture is here!");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switchButton.setEnabled(true);

                }
            });
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), "/camera_picture.png"));
                picture.compress(Bitmap.CompressFormat.JPEG, 95, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    final CameraSource.VideoStartCallback cameraSourceVideoStartCallback = new CameraSource.VideoStartCallback() {
        @Override
        public void onVideoStart() {
            isRecordingVideo = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
            Toast.makeText(context, "Video STARTED!", Toast.LENGTH_SHORT).show();
        }
    };

    final Camera2Source.ShutterCallback camera2SourceShutterCallback = new Camera2Source.ShutterCallback() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onShutter() {Log.d(TAG, "Shutter Callback for CAMERA2");}
    };



    private boolean checkGooglePlayAvailability() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        if(resultCode == ConnectionResult.SUCCESS) {
            return true;
        } else {
            if(googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(MainActivity.this, resultCode, 2404).show();
            }
        }
        return false;
    }

    private void requestPermissionThenOpenCamera() {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                useCamera2 = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
                createCameraSourceFront();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    private void createCameraSourceFront() {

        if(useCamera2) {
            mCamera2Source = new Camera2Source.Builder(context, new FaceDetectionProcessor(this), mGraphicOverlay)
                    .setFocusMode(Camera2Source.CAMERA_AF_AUTO)
                    .setFlashMode(Camera2Source.CAMERA_FLASH_AUTO)
                    .setFacing(Camera2Source.CAMERA_FACING_FRONT)
                    .build();

            //IF CAMERA2 HARDWARE LEVEL IS LEGACY, CAMERA2 IS NOT NATIVE.
            //WE WILL USE CAMERA1.
            if(mCamera2Source.isCamera2Native()) {
                startCameraSource();
            } else {
                useCamera2 = false;
                if(usingFrontCamera) createCameraSourceFront(); else createCameraSourceBack();
            }
        } else {
            mCameraSource = new CameraSource.Builder(context, new FaceDetectionProcessor(this), mGraphicOverlay)
                    .setFacing(CameraSource.CAMERA_FACING_FRONT)
                    .setRequestedFps(30.0f)
                    .build();

            startCameraSource();
        }
    }

    private void createCameraSourceBack() {
        if(useCamera2) {
            mCamera2Source = new Camera2Source.Builder(context, new FaceDetectionProcessor(this),mGraphicOverlay)
                    .setFocusMode(Camera2Source.CAMERA_AF_AUTO)
                    .setFlashMode(Camera2Source.CAMERA_FLASH_AUTO)
                    .setFacing(Camera2Source.CAMERA_FACING_BACK)
                    .build();

            //IF CAMERA2 HARDWARE LEVEL IS LEGACY, CAMERA2 IS NOT NATIVE.
            //WE WILL USE CAMERA1.
            if(mCamera2Source.isCamera2Native()) {
                startCameraSource();
            } else {
                useCamera2 = false;
                if(usingFrontCamera) createCameraSourceFront(); else createCameraSourceBack();
            }
        } else {
            mCameraSource = new CameraSource.Builder(context, new FaceDetectionProcessor(this),mGraphicOverlay)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedFps(30.0f)
                    .build();

            startCameraSource();
        }
    }

    private void startCameraSource() {
        if(useCamera2) {
            if(mCamera2Source != null) {
                try {mPreview.start(mCamera2Source, mGraphicOverlay);
                } catch (IOException e) {
                    Log.e(TAG, "Unable to start camera source 2.", e);
                    mCamera2Source.release();
                    mCamera2Source = null;
                }
            }
        } else {
            if (mCameraSource != null) {
                cameraVersion.setText("Camera 1");
                try {mPreview.start(mCameraSource, mGraphicOverlay);
                } catch (IOException e) {
                    Log.e(TAG, "Unable to start camera source.", e);
                    mCameraSource.release();
                    mCameraSource = null;
                }
            }
        }
    }

    private void stopCameraSource() {
        mPreview.stop();
    }



    private final CameraSourcePreview.OnTouchListener CameraPreviewTouchListener = new CameraSourcePreview.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent pEvent) {
            v.onTouchEvent(pEvent);
            if (pEvent.getAction() == MotionEvent.ACTION_DOWN) {
                int autoFocusX = (int) (pEvent.getX() - Utils.dpToPx(60)/2);
                int autoFocusY = (int) (pEvent.getY() - Utils.dpToPx(60)/2);
                ivAutoFocus.setTranslationX(autoFocusX);
                ivAutoFocus.setTranslationY(autoFocusY);
                ivAutoFocus.setVisibility(View.VISIBLE);
                ivAutoFocus.bringToFront();
                if(useCamera2) {
                    if(mCamera2Source != null) {
                        mCamera2Source.autoFocus(new Camera2Source.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success) {
                                runOnUiThread(new Runnable() {
                                    @Override public void run() {ivAutoFocus.setVisibility(View.GONE);}
                                });
                            }
                        }, pEvent, v.getWidth(), v.getHeight());
                    } else {
                        ivAutoFocus.setVisibility(View.GONE);
                    }
                } else {
                    if(mCameraSource != null) {
                        mCameraSource.autoFocus(new CameraSource.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success) {
                                runOnUiThread(new Runnable() {
                                    @Override public void run() {ivAutoFocus.setVisibility(View.GONE);}
                                });
                            }
                        });
                    } else {
                        ivAutoFocus.setVisibility(View.GONE);
                    }
                }
            }
            return false;
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestPermissionThenOpenCamera();
            } else {
                Toast.makeText(MainActivity.this, "CAMERA PERMISSION REQUIRED", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        if(requestCode == REQUEST_STORAGE_PERMISSION) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestPermissionThenOpenCamera();
            } else {
                Toast.makeText(MainActivity.this, "STORAGE PERMISSION REQUIRED", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(wasActivityResumed)
        	//If the CAMERA2 is paused then resumed, it won't start again unless creating the whole camera again.
        	if(useCamera2) {
        		if(usingFrontCamera) {
        			createCameraSourceFront();
        		} else {
        			createCameraSourceBack();
        		}
        	} else {
        		startCameraSource();
        	}

        mInTime = String.format(Locale.US, "%02d", Preferences.getInHour(this)) + ":" +
                String.format(Locale.US, "%02d", Preferences.getInMin(this));
        mOutTime = String.format(Locale.US, "%02d", Preferences.getOutHour(this)) + ":" +
                String.format(Locale.US, "%02d", Preferences.getOutMin(this));
    }

    @Override
    protected void onPause() {
        super.onPause();
        wasActivityResumed = true;
        stopCameraSource();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCameraSource();
        if(previewFaceDetector != null) {
            try {
                previewFaceDetector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
