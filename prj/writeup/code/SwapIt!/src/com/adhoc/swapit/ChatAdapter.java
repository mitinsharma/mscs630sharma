package com.adhoc.swapit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChatAdapter extends ArrayAdapter<String> {
	
	private final Context cont;
	private final String[] names;
	private TextView textview;
	private LinearLayout wrapper;

	public ChatAdapter(Context c, String[] s) {
		super(c, R.layout.chat_msgs, s);
		this.cont = c;
		this.names = s;
	}
	
	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.chat_msgs, parent, false);
		
		wrapper = (LinearLayout) rowView.findViewById(R.id.wrapper);
		textview = (TextView) rowView.findViewById(R.id.chatbub);
		String[] arr = names[position].split("[:]");
		textview.setText(arr[1]);
		
		if(arr[0].equals(SendSoc.SENT_MSG)) {
			textview.setBackgroundResource(R.drawable.msg_sent);
			wrapper.setGravity(Gravity.RIGHT);
		} else if(arr[0].equals(RecvSoc.RECV_MSG)) {
			textview.setBackgroundResource(R.drawable.msg_recv);
			wrapper.setGravity(Gravity.LEFT);
		}

		return rowView;
	}
}