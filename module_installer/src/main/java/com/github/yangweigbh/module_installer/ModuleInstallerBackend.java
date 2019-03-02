package com.github.yangweigbh.module_installer;


import java.util.List;
import java.util.Set;

/** A backend for installing dynamic feature modules that contain the actual install logic. */
/* package */ abstract class ModuleInstallerBackend {
    private final OnFinishedListener mListener;

    /** Listener for when a module install has finished. */
    interface OnFinishedListener {
        /**
         * Called when the module install has finished.
         * @param success True if the install was successful.
         * @param moduleNames Names of modules whose install is finished.
         */
        void onFinished(boolean success, List<String> moduleNames);
    }

    public ModuleInstallerBackend(OnFinishedListener listener) {
        ThreadUtils.assertOnUiThread();
        mListener = listener;
    }

    /**
     * Asynchronously installs module.
     * @param moduleName Name of the module.
     */
    public abstract void install(String moduleName);

    /**
     * Asynchronously installs module in the background.
     * @param moduleName Name of the module.
     */
    public abstract void installDeferred(String moduleName);

    /**
     * Returns which modules are installed (excluding the base module).
     * @return installed modules
     */
    public abstract Set<String> getInstalledModules();

    /**
     * Asynchronously uninstalls modules in the background.
     * @param moduleNames Name of the modules.
     */
    public abstract void unInstallDeferred(List<String> moduleNames);

    /**
     * Releases resources of this backend. Calling this method an install is in progress results in
     * undefined behavior. Calling any other method on this backend after closing results in
     * undefined behavior, too.
     */
    public abstract void close();

    /** To be called when module install has finished. */
    protected void onFinished(boolean success, List<String> moduleNames) {
        mListener.onFinished(success, moduleNames);
    }
}
