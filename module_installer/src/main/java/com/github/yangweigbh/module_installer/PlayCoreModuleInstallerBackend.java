package com.github.yangweigbh.module_installer;

import android.util.Log;

import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.SplitInstallSessionState;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Backend that uses the Play Core SDK to download a module from Play and install it subsequently.
 */
/* package */ class PlayCoreModuleInstallerBackend
        extends ModuleInstallerBackend implements SplitInstallStateUpdatedListener {
    private static final String TAG = "PlayCoreModInBackend";
    private final SplitInstallManager mManager;
    private boolean mIsClosed;

    public PlayCoreModuleInstallerBackend(OnFinishedListener listener) {
        super(listener);
        mManager = SplitInstallManagerFactory.create(ContextUtils.getApplicationContext());
        mManager.registerListener(this);
    }

    @Override
    public void install(String moduleName) {
        assert !mIsClosed;

        SplitInstallRequest request =
                SplitInstallRequest.newBuilder().addModule(moduleName).build();
        mManager.startInstall(request).addOnFailureListener(errorCode -> {
            Log.e(TAG, String.format("Failed to request module '%s': error code %s", moduleName, errorCode));
            // If we reach this error condition |onStateUpdate| won't be called. Thus, call
            // |onFinished| here.
            finish(false, Collections.singletonList(moduleName));
        });
    }

    @Override
    public void installDeferred(String moduleName) {
        assert !mIsClosed;
        mManager.deferredInstall(Collections.singletonList(moduleName));
    }

    @Override
    public Set<String> getInstalledModules() {
        assert !mIsClosed;
        return mManager.getInstalledModules();
    }

    @Override
    public void unInstallDeferred(List<String> moduleNames) {
        assert !mIsClosed;
        mManager.deferredUninstall(moduleNames);
    }

    @Override
    public void close() {
        assert !mIsClosed;
        mManager.unregisterListener(this);
        mIsClosed = true;
    }

    @Override
    public void onStateUpdate(SplitInstallSessionState state) {
        assert !mIsClosed;
        switch (state.status()) {
            case SplitInstallSessionStatus.INSTALLED:
                finish(true, state.moduleNames());
                break;
            case SplitInstallSessionStatus.CANCELED:
            case SplitInstallSessionStatus.FAILED:
                Log.e(TAG, String.format("Failed to install modules '%s': error code %s", state.moduleNames(),
                        state.status()));
                finish(false, state.moduleNames());
                break;
        }
    }

    private void finish(boolean success, List<String> moduleNames) {
        onFinished(success, moduleNames);
    }
}
