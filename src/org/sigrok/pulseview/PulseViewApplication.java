package org.sigrok.pulseview;

import org.qtproject.qt5.android.bindings.QtApplication;
import org.sigrok.androidutils.Environment;
import org.sigrok.androidutils.LibWrangler;
import org.sigrok.androidutils.UsbHelper;

import java.io.File;
import java.io.IOException;

public class PulseViewApplication extends QtApplication
{
    @Override
    public void onCreate()
    {
	Environment.initEnvironment(getApplicationInfo().sourceDir);
	UsbHelper.setContext(getApplicationContext());

	try {
	    System.loadLibrary("gnustl_shared");
	    LibWrangler.setupLibs(getAssets().open("liblist.txt"),
				  new File(getApplicationInfo().nativeLibraryDir),
				  new File(getFilesDir(), "lib"));
	} catch(IOException e) {
            e.printStackTrace();
	}

	super.onCreate();
    }
}

