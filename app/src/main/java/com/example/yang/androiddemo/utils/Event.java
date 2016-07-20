package com.example.yang.androiddemo.utils;

import android.os.Environment;

import java.io.File;

/**
 * author: yangjiajia
 * create time: 2016/7/20.
 * description:
 * modify time: 2016/7/20 15:10
 */
public class Event {
    public static String IMG_PATH = getSDPath() + File.separator + "androidDemo" + File.separator;
    public static String IMG_TEMP_PATH = getSDPath() + File.separator + "androidDemo" + File.separator + "temp" + File.separator;

    public static String getSDPath() {
        File sdDir;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        } else {
            return null;
        }
        return sdDir.toString();
    }
}
