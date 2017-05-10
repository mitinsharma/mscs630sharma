package com.adhoc.swapit;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class DeviceList extends Activity {
		
	public static final String NSD_REQUEST = "BROADCAST_NSD_ACTIVITY";
	public static final String STOP_SERVER = "KILL_RECEIVE_SOCKET";
	String[] list = {"NO ACTIVE DEVICES"};
	String username = null;
	NsdServiceInfo[] NSD_Array = null;
	ListView devLV;
	int port = 0;
	private boolean flag_single = true, flag_null = true, nsd_stopped = true;
	IntentFilter filter;
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("DeviceList", "NSD Activity Recieved");
			updateList(intent.getParcelableArrayExtra("DEVICES"));
		}
	};
	private BroadcastReceiver port_receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			port = intent.getIntExtra("PORT", 0);
			Log.i("DeviceList", "Received Port: "+port);
			launchNSD();
			unregisterReceiver(port_receiver);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_devicelist);
		username = getIntent().getStringExtra("USER_NAME");
		choiceNull();
		filter = new IntentFilter();
		filter.addAction(LocalNSD.NSD_ACTIVITY);
		IntentFilter port_filter = new IntentFilter();
		port_filter.addAction(RecvSoc.SERVER_PORT);
		registerReceiver(port_receiver, port_filter);
		startService(new Intent(getApplicationContext(), RecvSoc.class));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    if(flag_single)
	    	inflater.inflate(R.menu.actions_devicelist_1, menu);
	    else
	    	inflater.inflate(R.menu.actions_devicelist_2, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch(item.getItemId()) {
	    	case R.id.group:	choiceMultiple();
	    						break;
	    	case R.id.history:	chatHistory();
	    						break;
	    	case R.id.next:		nextClicked();
								break;
	    	case R.id.cancel:	choiceSingle();
								break;
	    }
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onPause() {
    	choiceNull();
    	unregisterReceiver(receiver);
		super.onPause();
	}
   
    @Override
    protected void onResume() {
		registerReceiver(receiver, filter);
		super.onResume();
		launchNSD();
    }
    
    @Override
    protected void onDestroy() {
    	if(!nsd_stopped) {
    		stopService(new Intent(getApplicationContext(), LocalNSD.class));
	    	nsd_stopped = true;
    	}
		DeviceList.this.sendBroadcast(new Intent(STOP_SERVER));
    	super.onDestroy();
    }
    
    public void launchNSD() {
    	if(nsd_stopped && port > 0) {
    		Intent i = new Intent(getApplicationContext(), LocalNSD.class);
    		i.putExtra("USER_NAME", username);
    		i.putExtra("PORT", port);
    		startService(i);
    		nsd_stopped = false;    	
    	}
    	else {
    		Intent i = new Intent(NSD_REQUEST);
    		DeviceList.this.sendBroadcast(i);
    	}
    }
   
    public void updateList(Parcelable[] parcel) {
    	int len = parcel.length;
    	final String str = "NULL_SERVICE";
    	if(len < 1) {
    		choiceNull();
    		return;
    	}
    	NSD_Array = new NsdServiceInfo[len];
    	for(int i = 0; i < len; i++) {
    		NSD_Array[i] = (NsdServiceInfo) parcel[i];
    	}
    	list = new String[len];
    	for(int i = 0; i < len; i++) {
    		list[i] = NSD_Array[i].getServiceName();
    	}
    	if(len == 1 && list[0].equals(str)) {
    		choiceNull();
    		return;
    	}
   		flag_null = false;
    	if(flag_single) {
    		choiceSingle();
    	}
    	else {
    		choiceMultiple();
    	}
    }
    
    public void choiceNull() {
   		list = new String[1];
   		list[0] = "NO ACTIVE DEVICES";
  		NSD_Array = null;
  		devLV = null;
		devLV = (ListView) findViewById(R.id.devlist);
    	devLV.setAdapter(new ArrayAdapter<String>(this, R.layout.user_null, list));
    	devLV.setChoiceMode(ListView.CHOICE_MODE_NONE);
		devLV.setOnItemClickListener(null);
		flag_null = true;
		flag_single = true;
    	this.invalidateOptionsMenu();
    }
    public void choiceSingle() {
		if(flag_null)
			return;
  		devLV = null;
		devLV = (ListView) findViewById(R.id.devlist);
		devLV.setAdapter(new UserAdapter(this, list));
		devLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		devLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos, long l) {
				NsdServiceInfo parcel = NSD_Array[pos];
				Intent i = new Intent(DeviceList.this, Chat.class);
				i.putExtra("USER_NAME", username);
				i.putExtra("PERSON", parcel);
				startActivity(i);
			}
		});
		flag_single = true;
    	this.invalidateOptionsMenu();
	}
	
	public void choiceMultiple() {
		if(flag_null)
			return;
		int len = list.length;
		String[] mlist = new String[len];
		for(int i = 0; i < len; i++) {
			String[] arr = list[i].split("[:]");
			mlist[i] = arr[0];
		}
  		devLV = null;
		devLV = (ListView) findViewById(R.id.devlist);
		devLV.setAdapter(new ArrayAdapter<String>(this, R.layout.user_multi, mlist));
		devLV.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		devLV.setOnItemClickListener(null);
		flag_single = false;
		this.invalidateOptionsMenu();
	}
	
	public void nextClicked() {
		if(flag_null)
			return;
		SparseBooleanArray checked = devLV.getCheckedItemPositions();
		int pos = 0, count = 0;
		for(pos = 0; pos < checked.size(); pos++) {
			if(checked.valueAt(pos)) {
				count++;
			}
		}
		if(count > 1) {
			NsdServiceInfo[] parcel = new NsdServiceInfo[count];
			pos = 0;
			for(int i = 0; i < checked.size(); i++) {
				if(checked.valueAt(i) && pos < count) {
					parcel[pos] = NSD_Array[checked.keyAt(i)];
					pos++;
				}
			}
			flag_single = true;
//			Intent i = new Intent(this, Chat.class);
//			i.putExtra("USER_NAME", username);
//			i.putExtra("MEMBERS", parcel);
//			startActivity(i);
		}
		else {
			Toast.makeText(this, "Select atleast 2 People", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void chatHistory() {
		
	}
}