# ModuleInstaller
### Helper for dynamic feature modules installation of App bundle

[![](https://jitpack.io/v/yangweigbh/ModuleInstaller.svg)](https://jitpack.io/#yangweigbh/ModuleInstaller)

## Setup:

add it in your root build.gradle

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

in your app build.gradle

```
dependencies {
        implementation 'com.github.yangweigbh:ModuleInstaller:0.1'
}
```

## Usage:

in your Application class

```java
public class MyApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        ModuleInstaller.init(this);
    }
}
```

in your Activity clas that will use dynamic module:

```java
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);

        ModuleInstaller.initActivity(this);
    }
```

to install a dynamic module, usually when your app in foreground:

```java
ModuleInstaller.install("test_module", new OnModuleInstallFinishedListener() {
            @Override
            public void onFinished(boolean success) {
                Log.d(TAG, "onFinished() called with: success = [" + success + "]");
            }
        });
```

to install a module when your app in background 

```java
ModuleInstaller.installDeferred("test_module");
```

to get all installed modules:

```java
Set<String> installedModules = ModuleInstaller.getInstalledModules();
```
to uninstall modules:

```java
ModuleInstaller.unInstallDeferred(Arrays.asList("test_module1", "test_module2"));
```

