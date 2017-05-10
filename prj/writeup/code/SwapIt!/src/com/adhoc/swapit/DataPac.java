package com.adhoc.swapit;

import java.io.File;
import java.io.Serializable;
import java.net.InetAddress;

import android.net.nsd.NsdServiceInfo;

public class DataPac implements Serializable{

	private static final long serialVersionUID = 1L;
	public final boolean flag;
	public final String my_key;
	public final String msg;
	public final String to_key;
	public final File file;
	public final InetAddress ip;
	public final int port;

	public DataPac(boolean f, String k, String s, NsdServiceInfo i, File a) {
		this.flag = f;
		this.my_key = k;
		this.msg = s;
		this.file = a;
		this.ip = i.getHost();
		this.port = i.getPort();
		String[] arr = i.getServiceName().split("[:]");
		to_key = arr[1];
	}
}