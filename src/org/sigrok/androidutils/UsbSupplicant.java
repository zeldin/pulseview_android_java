package org.sigrok.androidutils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import java.util.HashMap;
import java.util.HashSet;

public class UsbSupplicant
{
    private static final String ACTION_USB_PERMISSION =
	"org.sigrok.androidutils.USB_PERMISSION";

    protected final Context context;
    protected final UsbManager manager;
    private final BroadcastReceiver permReceiver;
    private final BroadcastReceiver hotplugReceiver;
    private final IntentFilter permFilter;
    private final IntentFilter hotplugFilter;
    private final HashMap<Integer,HashSet<Integer>> usbIds;

    public UsbSupplicant(Context ctx, int usb_ids_resource)
    {
	context = ctx;
	manager = (UsbManager) ctx.getSystemService(Context.USB_SERVICE);
	permReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
		    String action = intent.getAction();
		    if (ACTION_USB_PERMISSION.equals(action)) {
			permissionCallback((UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE),
					   intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false));
		    }
		}
	    };
	hotplugReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
		    if (intent != null &&
			UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(intent.getAction())) {
			attachCallback((UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE));
		    } else if (intent != null &&
			       UsbManager.ACTION_USB_DEVICE_DETACHED.equals(intent.getAction())) {
			detachCallback((UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE));
		    }
		}
	    };
	permFilter = new IntentFilter(ACTION_USB_PERMISSION);
	hotplugFilter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
	hotplugFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
	usbIds = new HashMap<Integer,HashSet<Integer>>();
	addUsbIds(ctx.getResources(), usb_ids_resource);
    }

    private void addUsbIds(Resources res, int res_id)
    {
	usbIds.clear();
	TypedArray vendor_ids = res.obtainTypedArray(res_id);
	try {
	    int i, cnt = vendor_ids.length();
	    for (i=0; i<cnt; i+=2) {
		int vid = vendor_ids.getInt(i, -1);
		int prod_res_id = vendor_ids.getResourceId(i+1, 0);
		if (vid >= 0 && res_id != 0) {
		    HashSet<Integer> vendor_set = new HashSet<Integer>();
		    int[] prod_ids = res.getIntArray(prod_res_id);
		    for (int pid : prod_ids)
			vendor_set.add(pid);
		    usbIds.put(vid, vendor_set);
		}
	    }
	} finally {
	    vendor_ids.recycle();
	}
    }

    protected boolean interresting(int vid, int pid)
    {
	HashSet<Integer> vendorSet = usbIds.get(vid);

	if (vendorSet != null &&
	    vendorSet.contains(pid))
	    return true;

	if (pid != 0 && interresting(vid, 0))
	    return true;

	if (vid != 0 && interresting(0, pid))
	    return true;

	return false;
    }

    protected boolean interresting(UsbDevice dev)
    {
	return dev != null &&
	    interresting(dev.getVendorId(), dev.getProductId());
    }

    protected void askFor(UsbDevice dev)
    {
	manager.requestPermission(dev, PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0));
    }

    public void start()
    {
	Log.i("UsbSupplicant", "start");
	context.registerReceiver(permReceiver, permFilter);
	context.registerReceiver(hotplugReceiver, hotplugFilter);
	HashMap<String,UsbDevice> devlist = manager.getDeviceList();
	for (UsbDevice dev : devlist.values()) {
	    if (interresting(dev) && !manager.hasPermission(dev)) {
		Log.i("UsbSupplicant", "found interresting device "+dev);
		askFor(dev);
	    }
	}
    }

    public void stop()
    {
	Log.i("UsbSupplicant", "stop");
	context.unregisterReceiver(hotplugReceiver);
	context.unregisterReceiver(permReceiver);
    }

    protected void permissionCallback(UsbDevice dev, boolean granted)
    {
	Log.d("UsbSupplicant", "permission " + (granted? "granted" : "denied") + " for device " + dev);
    }

    protected void attachCallback(UsbDevice dev)
    {
	Log.d("UsbSupplicant", "attached device " + dev);
	if (interresting(dev) && !manager.hasPermission(dev)) {
	    askFor(dev);
	}
    }

    protected void detachCallback(UsbDevice dev)
    {
	Log.d("UsbSupplicant", "detached device " + dev);
    }
}
