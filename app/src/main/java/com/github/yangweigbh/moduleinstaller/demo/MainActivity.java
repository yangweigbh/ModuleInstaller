package com.github.yangweigbh.moduleinstaller.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.github.yangweigbh.module_installer.ModuleInstaller;
import com.github.yangweigbh.module_installer.OnModuleInstallFinishedListener;
import com.github.yangweigbh.moduleinstaller.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);

        ModuleInstaller.initActivity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ModuleInstaller.install("test_module", new OnModuleInstallFinishedListener() {
            @Override
            public void onFinished(boolean success) {
                Log.d(TAG, "onFinished() called with: success = [" + success + "]");
            }
        });
    }

}
