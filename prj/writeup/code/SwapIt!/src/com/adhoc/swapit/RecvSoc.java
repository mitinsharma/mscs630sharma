package com.adhoc.swapit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.util.Log;

public class RecvSoc extends IntentService {
	
	private static final String TAG = "RecvSoc";
	public static final String SERVER_PORT = "STORE_SOCKET_PORT";
	public static final String RECV_MSG = "RECV";
	boolean server_flag = false;
	byte[] received, processed;
	String from_key;
	ServerSocket server = null;
	Socket client;
	BroadcastReceiver stop_cmd = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "STOP_SERVER command received");
			server_flag = false;
		}
	};

	public RecvSoc() {
		super(RecvSoc.class.getName());
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		IntentFilter filter = new IntentFilter();
		filter.addAction(DeviceList.STOP_SERVER);
		registerReceiver(stop_cmd, filter);
		int port = 0;
		try {

			server = new ServerSocket(0);			
			port = server.getLocalPort();
			Log.i(TAG, "ServerSocket created: "+port);
			server_flag = true;

		} catch (IOException e) {
			server_flag = false;
			Log.e(TAG, "Caught IOException");
			e.printStackTrace();
		}		
		Intent i = new Intent(SERVER_PORT);
		i.putExtra("PORT", port);
		RecvSoc.this.sendBroadcast(i);
		while(server_flag) {
			startServer();
		}
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "Server onDestroy called");
		unregisterReceiver(stop_cmd);
		if (server != null) {
			try {
				if(!server.isClosed()) {
					server.close();
					Log.i(TAG, "Server Stopped");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		super.onDestroy();
	}

	public void startServer() {
		try {

			Log.d(TAG, "Server Listening");
			client = null;
			server.setSoTimeout(5000);
			client = server.accept();

			if(client != null) {
				Log.i(TAG, "Connection established");
				recvData();
				client.close();
				setNull();
				Log.d(TAG, "Connection closed");
			}

		} catch (SocketTimeoutException e) {
			Log.d(TAG, "Server Timeout");
			e.printStackTrace();
		} catch (SocketException e) {
			server_flag = false;
			Log.e(TAG, "Caught SocketException");
			e.printStackTrace();
		} catch (IOException e) {
			server_flag = false;
			Log.e(TAG, "Caught IOException 1");
			e.printStackTrace();
		}
	}
	
	public void recvData() {
		try {
			ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
			StreamPac pack = (StreamPac) ois.readObject();
			received = pack.byte_stream;
			ois.close();
			processData();

		} catch (StreamCorruptedException e) {
			Log.e(TAG, "Caught StreamCorruptedException");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			Log.e(TAG, "Caught ClassNotFoundException");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "Caught IOException 2");
			e.printStackTrace();
		}
	}
	
	public void textData() {
		Log.i(TAG, "textData called");
		char[] arr = new char[processed.length];
		for(int i = 0; i < processed.length; i++) {
			arr[i] = (char) processed[i];
		}
		String msg = String.copyValueOf(arr);
		Log.i(TAG, "Message recieved: "+msg);

		Intent i = new Intent(AppDB.UPDATE);
		i.putExtra(AppDB.KEY, from_key);
		i.putExtra(AppDB.MSG, RECV_MSG+":"+msg);
		i.putExtra(AppDB.FLAG, true);
		RecvSoc.this.sendBroadcast(i);
	}
	
	public void fileData(String filename) {
		Log.i(TAG, "fileData called");
		String loc = "SwapIt"+"/"+from_key;
		File folders = new File(Environment.getExternalStorageDirectory(), loc);
		folders.mkdirs();
		String out = folders.getAbsolutePath()+"/"+filename;
		
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(out);
			fos.write(processed);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "Caught FileNotFoundException");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "Caught IOException 3");
			e.printStackTrace();
		}

		Intent i = new Intent(AppDB.UPDATE);
		i.putExtra(AppDB.KEY, from_key);
		i.putExtra(AppDB.MSG, RECV_MSG+":"+"Received File - "+filename);
		i.putExtra(AppDB.FLAG, true);
		RecvSoc.this.sendBroadcast(i);
	}
	
	public void processData() {
		char c;
		int pos = 0, count = 0;
		while(count < 2) {
			c = (char) received[pos];
			if(c == ':') {
				count++;
			}
			pos++;
		}
		processed = Arrays.copyOfRange(received, pos, received.length);
		pos--;
		char[] arr = new char[pos];
		for(int i = 0; i < pos; i++) {
			arr[i] = (char) received[i];
		}
		String id_str = String.copyValueOf(arr);
		String[] id_arr = id_str.split("[:]");
		from_key = id_arr[0];
		if(id_arr[1].equals(SendSoc.CODE)) {
			textData();
		}
		else {
			fileData(id_arr[1]);
		}
	}
	
	public void setNull() {
		received = null;
		processed = null;
		from_key = null;
	}
}