package com.github.yangweigbh.module_installer;

/** Listener for when a module install has finished. */
public interface OnModuleInstallFinishedListener {
    /**
     * Called when the install has finished.
     *
     * @param success True if the module was installed successfully.
     */
    void onFinished(boolean success);
}
