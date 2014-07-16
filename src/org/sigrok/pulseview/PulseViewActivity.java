package org.sigrok.pulseview;

import org.qtproject.qt5.android.bindings.QtActivity;
import org.sigrok.androidutils.UsbSupplicant;

import android.os.Bundle;

public class PulseViewActivity extends QtActivity
{
    private UsbSupplicant supplicant;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	supplicant = new UsbSupplicant(getApplicationContext(), R.xml.device_filter);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
	supplicant.start();
    }

    @Override
    protected void onStop()
    {
	supplicant.stop();
        super.onStop();
    }
}
