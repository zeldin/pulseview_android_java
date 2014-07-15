package org.sigrok.pulseview;

import org.qtproject.qt5.android.bindings.QtApplication;
import org.sigrok.androidutils.Environment;
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
	super.onCreate();
    }
}

