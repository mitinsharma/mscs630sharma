package com.adhoc.swapit;

import java.io.Serializable;

public class StreamPac implements Serializable {
	
	private static final long serialVersionUID = 1L;
	final byte[] byte_stream;
	
	public StreamPac(byte[] s) {
		this.byte_stream = s;
	}
}