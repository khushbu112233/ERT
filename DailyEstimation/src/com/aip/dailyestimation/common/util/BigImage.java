package com.aip.dailyestimation.common.util;

import android.graphics.Bitmap;

public class BigImage {

    private static Bitmap ImageBitmap;

    public static Bitmap getImageBitmap() {
        return ImageBitmap;
    }

    public static void setImageBitmap(Bitmap imageBitmap) {
        ImageBitmap = imageBitmap;
    }


}
