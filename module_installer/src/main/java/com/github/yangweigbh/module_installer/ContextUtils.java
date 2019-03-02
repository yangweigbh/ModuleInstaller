package com.github.yangweigbh.module_installer;

import android.content.Context;

class ContextUtils {
    private static Context sApplicationContext;

    public static void initApplicationContext(Context appContext) {
        if (appContext == null) {
            throw new RuntimeException("Global application context cannot be set to null.");
        }
        sApplicationContext = appContext;
    }

    public static Context getApplicationContext() {
        return sApplicationContext;
    }
}
