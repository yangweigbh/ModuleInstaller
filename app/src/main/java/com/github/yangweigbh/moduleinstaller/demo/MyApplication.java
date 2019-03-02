package com.github.yangweigbh.moduleinstaller.demo;

import android.app.Application;
import android.content.Context;

import com.github.yangweigbh.module_installer.ModuleInstaller;

public class MyApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        ModuleInstaller.init(this);
    }
}
