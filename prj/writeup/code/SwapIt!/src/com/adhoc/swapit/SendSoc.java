package com.adhoc.swapit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class SendSoc extends IntentService {

	private static final String TAG = "SendSoc";
	public static final String CODE = "MSG";
	public static final String SENT_MSG = "SENT";
	String my_key;
	String msg;
	String to_key;
	File file;
	InetAddress ip;
	int port;

	public SendSoc() {
		super(SendSoc.class.getName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(TAG, "Send Service Started");
		DataPac pac = (DataPac) intent.getSerializableExtra("DATA");
		my_key = pac.my_key;
		ip = pac.ip;
		port = pac.port;
		to_key = pac.to_key;
		if(pac.flag) {
			msg = pac.msg;
			sendMsg();
		} else {
			file = pac.file;
			sendFile();
		}
	}
	public void sendMsg() {
		String id = my_key+":"+CODE+":";
		byte[] a = id.getBytes();
		byte[] b = msg.getBytes();
		int len = a.length + b.length;
		try {
			Log.d(TAG, "sendMsg Method");
			ByteArrayOutputStream baos = new ByteArrayOutputStream(len);
			baos.write(a);
			baos.write(b);
			StreamPac pack = new StreamPac(baos.toByteArray());

			Socket client = new Socket(ip, port);
			ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
			oos.writeObject(pack);
			oos.close();
			client.close();
			Log.d(TAG, "Connection closed");

			Intent i = new Intent(AppDB.UPDATE);
			i.putExtra(AppDB.KEY, to_key);
			i.putExtra(AppDB.MSG, SENT_MSG+":"+msg);
			i.putExtra(AppDB.FLAG, true);
			SendSoc.this.sendBroadcast(i);

		} catch (IOException e) {
			Log.e(TAG, "Caught IOException");
			e.printStackTrace();
		}
	}
	
	public void sendFile() {
		String path = file.getAbsolutePath();
		int pos = path.lastIndexOf("/");
		String msg = path.substring(pos+1);
		String id = my_key+":"+msg+":";
		byte[] a = id.getBytes();
		try {
			Log.d(TAG, "sendMsg Method");
			byte[] b = new byte[10000000];
			FileInputStream fis = new FileInputStream(file);
			fis.read(b);
			fis.close();
			int len = a.length + b.length;
			ByteArrayOutputStream baos = new ByteArrayOutputStream(len);
			baos.write(a);
			baos.write(b);
			StreamPac pack = new StreamPac(baos.toByteArray());

			Socket client = new Socket(ip, port);
			ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
			oos.writeObject(pack);
			oos.close();
			client.close();
			Log.d(TAG, "Connection closed");

			Intent i = new Intent(AppDB.UPDATE);
			i.putExtra(AppDB.KEY, to_key);
			i.putExtra(AppDB.MSG, SENT_MSG+":"+"Sent File - "+msg);
			i.putExtra(AppDB.FLAG, true);
			SendSoc.this.sendBroadcast(i);

		} catch (IOException e) {
			Log.e(TAG, "Caught IOException");
			e.printStackTrace();
		}
	}
}