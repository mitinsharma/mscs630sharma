package com.adhoc.swapit;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class UserPic extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userpic);
	}
	
	public void picSet(View v) {
		switch(v.getId()) {
		case R.id.up1:	setResult(1);
						break;
		case R.id.up2:	setResult(2);
						break;
		case R.id.up3:	setResult(3);
						break;
		case R.id.up4:	setResult(4);
						break;
		case R.id.up5:	setResult(5);
						break;
		case R.id.up6:	setResult(6);
						break;
		case R.id.up7:	setResult(7);
						break;
		case R.id.up8:	setResult(8);
						break;
		}
		finish();
	}
}