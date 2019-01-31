package com.example.apurvchaudhary.cameratest.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.Display;
import android.view.WindowManager;



public class Utils {

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int getScreenHeight(Context c) {
        WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    public static int getScreenWidth(Context c) {
        WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static float getScreenRatio(Context c) {
        DisplayMetrics metrics = c.getResources().getDisplayMetrics();
        return ((float)metrics.heightPixels / (float)metrics.widthPixels);
    }

    public static int getScreenRotation(Context c) {
        WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getRotation();
    }

    public static int distancePointsF(PointF p1, PointF p2) {
        return (int) Math.sqrt((p1.x - p2.x) *  (p1.x - p2.x) + (p1.y - p2.y) *  (p1.y - p2.y));
    }

    public static PointF middlePoint(PointF p1, PointF p2) {
        if(p1 == null || p2 == null)
            return null;
        return new PointF((p1.x+p2.x)/2, (p1.y+p2.y)/2);
    }

    public static com.google.android.gms.common.images.Size[] sizeToSize(Size[] sizes) {
        com.google.android.gms.common.images.Size[] size = new com.google.android.gms.common.images.Size[sizes.length];
        for(int i=0; i<sizes.length; i++) {
            size[i] = new com.google.android.gms.common.images.Size(sizes[i].getWidth(), sizes[i].getHeight());
        }
        return size;
    }

}
