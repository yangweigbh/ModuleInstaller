package com.github.yangweigbh.module_installer;

import android.os.Looper;

class ThreadUtils {
    public static void assertOnUiThread() {
        if (!(Looper.getMainLooper() == Looper.myLooper())) {
            throw new RuntimeException("should be called on UI thread");
        }
    }
}
