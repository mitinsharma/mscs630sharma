package com.adhoc.swapit;

import java.io.File;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class Chat extends Activity{
	
	private static final int CODE = 111;
	EditText chatET;
	ListView chatLV;
	NsdServiceInfo info;
	String my_key;
	Animation anime;
	String[] list = null;
	BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("Chat", "Database broadcast received");
			if(intent.getBooleanExtra(AppDB.FLAG, false)) {
				String str = intent.getStringExtra(AppDB.MSG);
				if(list != null && list.length > 0) {
					int len = list.length;
					String[] arr = new String[len+1];
					for(int i = 0; i < len; i++) {
						arr[i] = list[i];
					}
					arr[len] = str;
					list = arr;
				} else {
					list = new String[1];
					list[0] = str;
				}
			} else {
				list = intent.getStringArrayExtra(AppDB.MSG);
			}
			updateList();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		String[] arr = getIntent().getStringExtra("USER_NAME").split("[:]");
		my_key = arr[1];
		info = (NsdServiceInfo) getIntent().getParcelableExtra("PERSON");
		anime = AnimationUtils.loadAnimation(this, R.layout.animate);
		chatET = (EditText) findViewById(R.id.chatmsg);
		arr = info.getServiceName().split("[:]");
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(AppDB.KEY+arr[1]);
		registerReceiver(receiver, filter);

		String usr[] = info.getServiceName().split("[:]");
		Intent i = new Intent(AppDB.UPDATE);
		i.putExtra(AppDB.KEY, usr[1]);
		i.putExtra(AppDB.MSG, "");
		i.putExtra(AppDB.FLAG, false);
		Chat.this.sendBroadcast(i);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.actions_chat, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.clip)
			attachFile();
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == CODE && resultCode == Activity.RESULT_OK) {
			File file = (File) data.getSerializableExtra("FILE");
			Log.i("CHAT", file.getAbsolutePath());
			sendFile(file);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	public void sendText(View v) {
		v.startAnimation(anime);
		String msg = chatET.getText().toString();
		if(msg.isEmpty()) {
			Toast.makeText(this, "Type a message before sending", Toast.LENGTH_SHORT).show();
			return;
		}
		msg = msg.replaceAll("[^A-Za-z0-9 ]", "");
		DataPac pac = new DataPac(true, my_key, msg, info, null);
		Intent i = new Intent(getApplicationContext(), SendSoc.class);
		i.putExtra("DATA", pac);
		startService(i);
		chatET.setText(null);
	}
	
	public void sendFile(File file) {
		DataPac pac = new DataPac(false, my_key, null, info, file);
		Intent i = new Intent(getApplicationContext(), SendSoc.class);
		i.putExtra("DATA", pac);
		startService(i);
		chatET.setText(null);		
	}
	
	public void attachFile() {
		Intent i = new Intent(this, FileExplore.class);
		startActivityForResult(i, CODE);
	}
	
	public void updateList() {
		chatLV = null;
		chatLV = (ListView) findViewById(R.id.chatview);
		ChatAdapter chatA = new ChatAdapter(getApplicationContext(), list);
		chatLV.setAdapter(chatA);
		chatLV.setSelection(chatA.getCount()-1);
	}
	
	public void deleteChat() {
		
	}
}