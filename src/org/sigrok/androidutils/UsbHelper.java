package org.sigrok.androidutils;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;
import java.util.HashMap;

public final class UsbHelper
{
    private static UsbManager manager;

    public static void setContext(Context ctx)
    {
	if (ctx == null)
	    manager = null;
	else
	    manager = (UsbManager) ctx.getSystemService(Context.USB_SERVICE);
    }

    private static int open(UsbManager manager, String name, int mode)
    {
	Log.i("UsbHelper", "open("+name+","+mode+") called");
	if (manager == null) {
	    Log.i("UsbHelper", "no manager");
	    return -1;
	}
	HashMap<String,UsbDevice> devlist = manager.getDeviceList();
	Log.i("UsbHelper", "devlist = "+devlist);
	UsbDevice dev = (devlist == null? null : devlist.get(name));
	if (dev == null) {
	    Log.i("UsbHelper", "dev not found");
	    return -1;
	}
	if (!manager.hasPermission(dev)) {
	    Log.i("UsbHelper", "no permission for dev");
	    return -1;
	}
	UsbDeviceConnection conn = manager.openDevice(dev);
	return (conn == null? -1 : conn.getFileDescriptor());
    }

    public static int open(String name, int mode)
    {
	try {
	    return open(manager, name, mode);
	} catch(Exception e) {
	    Log.i("UsbHelper", "caught exception "+e);
	    return -1;
	}
    }
}

