package com.adhoc.swapit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class NetStatus {
	
	public static final int UNKNOWN = 0;
	public static final int WIFI_OFF = 1;
	public static final int NO_ROUTER = 2;
	public static final int CONNECTED = 3;
	Context cont;

	public NetStatus(Context c) {
		this.cont = c;
	}
	
	public int check () {
		WifiManager wifi = (WifiManager) cont.getSystemService(Context.WIFI_SERVICE);
		NetworkInfo info = ((ConnectivityManager) cont.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if(!wifi.isWifiEnabled()) {
			return WIFI_OFF;
		}
		else if(info == null || info.getType() != ConnectivityManager.TYPE_WIFI) {
			return NO_ROUTER;
		}
		else if(info.isConnected()) {
			return CONNECTED;
		}
		return UNKNOWN;
	}
}