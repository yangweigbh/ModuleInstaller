package com.github.yangweigbh.module_installer;

import android.app.Application;
import android.content.Context;

import com.google.android.play.core.splitcompat.SplitCompat;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Installs dynamic feature modules (DFMs). */
public class ModuleInstaller {
    private static final Map<String, List<OnModuleInstallFinishedListener>> sModuleNameListenerMap =
            new HashMap<>();
    private static ModuleInstallerBackend sBackend;

    /** Needs to be called before trying to access a module. */
    public static void init(Application app) {
        ContextUtils.initApplicationContext(app.getApplicationContext());
        // SplitCompat.install may copy modules into app's internal folder or clean them up.
        try (StrictModeContext unused = StrictModeContext.allowDiskWrites()) {
            SplitCompat.install(app.getApplicationContext());
        }
    }

    /**
     * Needs to be called in attachBaseContext of the activities that want to have access to
     * splits prior to application restart.
     *
     * For details, see:
     * https://developer.android.com/reference/com/google/android/play/core/splitcompat/SplitCompat.html#install(android.content.Context)
     */
    public static void initActivity(Context context) {
        SplitCompat.install(context);
    }

    /**
     * Requests the install of a module. The install will be performed asynchronously.
     *
     * @param moduleName Name of the module as defined in GN.
     * @param onFinishedListener Listener to be called once installation is finished.
     */
    public static void install(
            String moduleName, OnModuleInstallFinishedListener onFinishedListener) {
        ThreadUtils.assertOnUiThread();

        if (!sModuleNameListenerMap.containsKey(moduleName)) {
            sModuleNameListenerMap.put(moduleName, new LinkedList<>());
        }
        List<OnModuleInstallFinishedListener> onFinishedListeners =
                sModuleNameListenerMap.get(moduleName);
        onFinishedListeners.add(onFinishedListener);
        if (onFinishedListeners.size() > 1) {
            // Request is already running.
            return;
        }
        getBackend().install(moduleName);
    }

    /**
     * Asynchronously installs module in the background when on unmetered connection and charging.
     * Install is best effort and may fail silently. Upon success, the module will only be available
     * after App restarts.
     *
     * @param moduleName Name of the module.
     */
    public static void installDeferred(String moduleName) {
        ThreadUtils.assertOnUiThread();
        getBackend().installDeferred(moduleName);
    }

    /**
     * Returns which modules are installed (excluding the base module).
     * @return installed modules
     */
    public static Set<String> getInstalledModules() {
        return getBackend().getInstalledModules();
    }

    /**
     * Returns which modules are installed (excluding the base module).
     * @return installed modules
     */
    public static void unInstallDeferred(List<String> moduleNames) {
        getBackend().unInstallDeferred(moduleNames);
    }

    private static void onFinished(boolean success, List<String> moduleNames) {
        ThreadUtils.assertOnUiThread();

        for (String moduleName : moduleNames) {
            List<OnModuleInstallFinishedListener> onFinishedListeners =
                    sModuleNameListenerMap.get(moduleName);
            if (onFinishedListeners == null) continue;

            for (OnModuleInstallFinishedListener listener : onFinishedListeners) {
                listener.onFinished(success);
            }
            sModuleNameListenerMap.remove(moduleName);
        }

        if (sModuleNameListenerMap.isEmpty()) {
            sBackend.close();
            sBackend = null;
        }
    }

    private static ModuleInstallerBackend getBackend() {
        if (sBackend == null) {
            ModuleInstallerBackend.OnFinishedListener listener = ModuleInstaller::onFinished;
            sBackend = new PlayCoreModuleInstallerBackend(listener);
        }
        return sBackend;
    }

    private ModuleInstaller() {}
}
