package com.adhoc.swapit;

import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static final String STOP_DB = "STOP_DATABASE_SERVICE";
	EditText nameET;
	ImageButton picIB;
	Animation anime;
	String user_name = null, user_id = null;
	int pic_id = 8;
	static final int acc_code = 111;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		nameET = (EditText) findViewById(R.id.username);
		picIB = (ImageButton) findViewById(R.id.userpic);
		anime = AnimationUtils.loadAnimation(this, R.layout.animate);
		user_id = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress().replaceAll("[^A-Fa-f0-9]", "");
		user_name = getPreferences(Context.MODE_PRIVATE).getString("USERNAME", null);
		nameET.setText(user_name);
		pic_id = getPreferences(Context.MODE_PRIVATE).getInt("PICID", 8);
		setPic(pic_id);
		startService(new Intent(getApplicationContext(), AppDB.class));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == acc_code) {
			pic_id = resultCode;
			setPic(pic_id);
		}
	}
	
	@Override
	protected void onDestroy() {
		MainActivity.this.sendBroadcast(new Intent(STOP_DB));
		super.onDestroy();
	}

	public void acceptData(View v) {
		v.startAnimation(anime);
		if (user_id == null) {
			Toast.makeText(this, "Unable to access MAC_ADDRESS", Toast.LENGTH_SHORT).show();
			return;
		}
		user_name = nameET.getText().toString();
		int len = user_name.length();
		if(len > 5 && len < 13) {
			if(Pattern.matches("[A-Za-z0-9]*", user_name)) {
				if(checkStatus()) {
					Intent i = new Intent(this, DeviceList.class);
					i.putExtra("USER_NAME", saveData(acc_code));
					startActivity(i);
				}
			}
			else {
				nameET.setText(null);
				Toast.makeText(this, "Enter only Alphabets and Numbers", Toast.LENGTH_SHORT).show();
			}
		}
		else {
			nameET.setText(null);
			Toast.makeText(this, "Username must contain 6-12 characters", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void resetData(View v) {
		v.startAnimation(anime);
		nameET.setText(null);
		pic_id = 8;
		picIB.setImageResource(R.drawable.up8l);
	}

	public void picMenu(View v) {
		Intent i = new Intent(this, UserPic.class);
		startActivityForResult(i, acc_code);
	}

	public void infoClicked(View v) {
		
	}

	public String saveData(int requestCode) {
		if(requestCode == acc_code) {
			SharedPreferences.Editor spEdit = getPreferences(Context.MODE_PRIVATE).edit();
			spEdit.putString("USERNAME", user_name).commit();
			spEdit.putInt("PICID", pic_id).commit();
			return user_name+":"+user_id+":"+Integer.toString(pic_id);
		}
		return null;
	}

    public boolean checkStatus() {
    	NetStatus status = new NetStatus(getApplicationContext());
    	switch (status.check()) {
		case NetStatus.WIFI_OFF:
			Toast.makeText(this, "WiFi is OFF - Try Again", Toast.LENGTH_LONG).show();
			return false;
		case NetStatus.NO_ROUTER:
			Toast.makeText(this, "Connect your device to a WiFi Router", Toast.LENGTH_LONG).show();
			return false;
		case NetStatus.CONNECTED:
			return true;
		default:
			Toast.makeText(this, "Unknown Network Status Error", Toast.LENGTH_LONG).show();
			return false;
		}
    }

	public void setPic(int code) {
		switch(code) {
		case 1: picIB.setImageResource(R.drawable.up1l);
				break;
		case 2: picIB.setImageResource(R.drawable.up2l);
				break;
		case 3: picIB.setImageResource(R.drawable.up3l);
				break;
		case 4: picIB.setImageResource(R.drawable.up4l);
				break;
		case 5: picIB.setImageResource(R.drawable.up5l);
				break;
		case 6: picIB.setImageResource(R.drawable.up6l);
				break;
		case 7: picIB.setImageResource(R.drawable.up7l);
				break;
		case 8:	picIB.setImageResource(R.drawable.up8l);
				break;
		}
	}
}