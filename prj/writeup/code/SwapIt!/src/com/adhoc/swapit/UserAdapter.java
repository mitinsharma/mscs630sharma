package com.adhoc.swapit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UserAdapter extends ArrayAdapter<String> {
	
	private final Context cont;
	private final String[] names;

	public UserAdapter(Context c, String[] s) {
		super(c, R.layout.user_list, s);
		this.cont = c;
		this.names = s;
	}
	
	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.user_list, parent, false);
		
		TextView textview = (TextView) rowView.findViewById(R.id.displayname);
		ImageView imageview = (ImageView) rowView.findViewById(R.id.displaypic);
		
		String[] arr = names[position].split("[:]");
		textview.setText(arr[0]);
		int i = Integer.parseInt(arr[2]);
		
		switch(i) {
		case 1: imageview.setImageResource(R.drawable.up1m);
				break;
		case 2: imageview.setImageResource(R.drawable.up2m);
				break;
		case 3: imageview.setImageResource(R.drawable.up3m);
				break;
		case 4: imageview.setImageResource(R.drawable.up4m);
				break;
		case 5: imageview.setImageResource(R.drawable.up5m);
				break;
		case 6: imageview.setImageResource(R.drawable.up6m);
				break;
		case 7: imageview.setImageResource(R.drawable.up7m);
				break;
		case 8:	imageview.setImageResource(R.drawable.up8m);
				break;
		default:imageview.setImageResource(R.drawable.up8m);
		}
		return rowView;
	}
}