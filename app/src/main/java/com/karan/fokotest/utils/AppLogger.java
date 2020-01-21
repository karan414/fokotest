package com.karan.fokotest.utils;

import android.util.Log;

public class AppLogger {

    public static void LogD(String TAG, String message) {
        Log.d(TAG, message);
    }

    public static void LogE(String TAG, String message) {
        Log.e(TAG, message);
    }

    public static void LogI(String TAG, String message) {
        Log.i(TAG, message);
    }

    public static void LogV(String TAG, String message) {
        Log.v(TAG, message);
    }

}
