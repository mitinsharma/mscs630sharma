package com.adhoc.swapit;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.util.Log;

public class AppDB extends IntentService {

	public static final String TAG = "SQLite";
	public static final String UPDATE = "SWAPIT_UPDATE_DATABASE";
	public static final String DELETE = "DROP_TABLE:";
	public static final String KEY = "TABLE";
	public static final String MSG = "MESSAGE";
	public static final String FLAG = "TYPE";
	SQLiteDatabase chatDB = null;
	boolean db_flag = true;
	BroadcastReceiver stop_cmd = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "STOP_DB command received");
			db_flag = false;
		}
	};
	BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "Database Broadcast received");
			accessDB(KEY+intent.getStringExtra(KEY), intent.getStringExtra(MSG), intent.getBooleanExtra(FLAG, false));
		}
	};

	public AppDB() {
		super(AppDB.class.getName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		IntentFilter filter = new IntentFilter();
		filter.addAction(UPDATE);
		registerReceiver(receiver, filter);
		filter = new IntentFilter();
		filter.addAction(MainActivity.STOP_DB);
		registerReceiver(stop_cmd, filter);
		Log.d(TAG, "Database loop started");
		while(db_flag) {
			SystemClock.sleep(100);
		}
		Log.d(TAG, "Database loop stopped");
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		unregisterReceiver(stop_cmd);
		if(chatDB != null && chatDB.isOpen()) {
			chatDB.close();
		}
		Log.i(TAG, "Database service stopped");
		super.onDestroy();
	}
	
	public void accessDB(String table, String msg, boolean flag) {
		Log.i(TAG, "Database called");
		if(chatDB == null || !chatDB.isOpen()) {
			chatDB = openOrCreateDatabase("SwapItChat", MODE_PRIVATE, null);
			Log.d(TAG, "Database Opened");
		}
		if(chatDB.isOpen()) {

			chatDB.execSQL("CREATE TABLE IF NOT EXISTS "+table+"(message TEXT);");
			if(flag) {
				insertDB(table, msg);
			} else {
				retriveDB(table, msg);
			}
			chatDB.close();

		} else {
			Log.e(TAG, "Database Missing");
		}
	}
	
	public void insertDB(String table, String msg) {
		chatDB.execSQL("INSERT INTO "+table+" VALUES ('"+msg+"');");
		Intent i = new Intent(table);
		i.putExtra(MSG, msg);
		i.putExtra(FLAG, true);
		AppDB.this.sendBroadcast(i);
		Log.d(TAG, msg);
	}
	
	public void retriveDB(String table, String msg) {
		String[] arr = {SendSoc.SENT_MSG+":NO CHAT HISTORY"};
		if(msg.equals(DELETE+table)) {
			chatDB.execSQL("DROP TABLE IF EXISTS "+table+";");
		} else {
			Cursor cur = chatDB.rawQuery("SELECT * FROM "+table, null);
			if(cur != null) {
				int col = cur.getColumnIndex("message");
				int len = cur.getCount();
				if(len > 0) {
					arr = new String[len];
					int pos = 0;
					cur.moveToFirst();
					do {
						arr[pos] = cur.getString(col);
						pos++;
					}while(cur.moveToNext() && pos < len);
				}
			}
		}
		Intent i = new Intent(table);
		i.putExtra(MSG, arr);
		i.putExtra(FLAG, false);
		AppDB.this.sendBroadcast(i);
	}
}