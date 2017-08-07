package com.kingbox.jsbridge;


public interface JSBridge {
	
	void send(String data);
	void send(String data, CallBackFunction responseCallback);
	
	

}
