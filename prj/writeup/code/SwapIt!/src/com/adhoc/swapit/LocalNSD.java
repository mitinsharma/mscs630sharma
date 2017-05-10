package com.adhoc.swapit;

import java.util.ArrayList;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class LocalNSD extends Service {
	
	private static final String TAG = "LocalNSD";
	public static final String NSD_ACTIVITY = "UPDATE_DEVICE_LIST";
	static final String SERVICE_TYPE = "_swapit._tcp.";
	private boolean regis_flag = false, disco_flag = false;
	int port = 0;
	String SERVICE_NAME = null;
	ArrayList<NsdServiceInfo> NSD_List = new ArrayList<NsdServiceInfo>();
	NsdManager manager;
	NsdManager.RegistrationListener regis_listner;
	NsdManager.DiscoveryListener disco_listner;
	NsdManager.ResolveListener resol_listner;
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("DeviceList", "NSD Request Sent");
			parcelNSD();
		}
	};
	
	public LocalNSD() {
		initializeRegistrationListener();
		initializeDiscoveryListener();
		initializeResolveListener();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "NSD Service Starting..");
		SERVICE_NAME = intent.getStringExtra("USER_NAME");
		port = intent.getIntExtra("PORT", 0);
		IntentFilter filter = new IntentFilter();
		filter.addAction(DeviceList.NSD_REQUEST);
		registerReceiver(receiver, filter);
	    manager = (NsdManager) this.getSystemService(Context.NSD_SERVICE);
	    startNSD();
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "NSD Service Stopped");
		stopNSD();
        unregisterReceiver(receiver);
        SystemClock.sleep(1000);
		super.onDestroy();
	}

	public void startNSD() {
		if(port < 1) {
			Log.e(TAG, "Invalid PORT Number: "+port);
			return;
		}
		NsdServiceInfo serviceInfo  = new NsdServiceInfo();
		serviceInfo.setServiceName(SERVICE_NAME);
		serviceInfo.setServiceType(SERVICE_TYPE);
		serviceInfo.setPort(port);
		if(!disco_flag) {
			manager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, disco_listner);
		}
		if(!regis_flag) {
			manager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, regis_listner);
		}
	}
	
	public void stopNSD() {
        if(regis_flag) {
        	manager.unregisterService(regis_listner);
        }
        if(disco_flag) {
        	manager.stopServiceDiscovery(disco_listner);
        }
	}

	public void parcelNSD() {
		int len = NSD_List.size();
		NsdServiceInfo[] parcel;
		if(len < 1) {
			NsdServiceInfo info = new NsdServiceInfo();
		    info.setServiceName("NULL_SERVICE");
			parcel = new NsdServiceInfo[1];
			parcel[0] = info;
			NSD_List.clear();
		}
		else {
			parcel = new NsdServiceInfo[len];
			for(int j = 0; j < len; j++) {
				parcel[j] = NSD_List.get(j);
			}
		}
    	Intent i = new Intent(NSD_ACTIVITY);
    	i.putExtra("DEVICES", parcel);
    	LocalNSD.this.sendBroadcast(i);
	}

	public void initializeRegistrationListener() {
	    this.regis_listner = new NsdManager.RegistrationListener() {

	        @Override
	        public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
	            SERVICE_NAME = NsdServiceInfo.getServiceName();
	            regis_flag = true;
	            Log.i(TAG, "Registration successful");
	        }

	        @Override
	        public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
	        	Log.e(TAG, "Registration failed: " + errorCode);
	        }

	        @Override
	        public void onServiceUnregistered(NsdServiceInfo arg0) {
	        	regis_flag = false;
	        	Log.i(TAG, "Unregistration successful");
	        }

	        @Override
	        public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
	        	Log.e(TAG, "Unregistration failed: " + errorCode);
	        	manager.unregisterService(this);
	        }
	    };
	}

	public void initializeDiscoveryListener() {

		this.disco_listner = new NsdManager.DiscoveryListener() {

	        @Override
	        public void onDiscoveryStarted(String regType) {
	        	disco_flag = true;
	            Log.d(TAG, "Service discovery started");
	        }

	        @Override
	        public void onServiceFound(NsdServiceInfo service) {
	            Log.d(TAG, "Service discovery success: " + service);
	            
	            if (!service.getServiceType().equals(SERVICE_TYPE)) {
	                Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
	            }
	            else if (service.getServiceName().equals(SERVICE_NAME)) {
	                Log.d(TAG, "Same device: " + SERVICE_NAME);
	            }
	            else {
	                manager.resolveService(service, resol_listner);
	            }
	        }

	        @Override
	        public void onServiceLost(NsdServiceInfo service) {
	            Log.e(TAG, "Service lost: " + service);
	            String name = service.getServiceName();
	        	if (!name.equals(SERVICE_NAME)) {
	    			for(int i = 0; i < NSD_List.size(); i++) {
	    				if(NSD_List.get(i).getServiceName().equals(name)) {
	    					NSD_List.remove(i);
	    	    			Log.i("NSD_List", "NSD Service Info object removed @ "+i);
	    	    			break;
	    				}
	    			}
	    			parcelNSD();
	        	}
	        }

	        @Override
	        public void onDiscoveryStopped(String serviceType) {
	        	disco_flag = false;
	            Log.i(TAG, "Discovery stopped: " + serviceType);
	        }

	        @Override
	        public void onStartDiscoveryFailed(String serviceType, int errorCode) {
	            Log.e(TAG, "Discovery failed: " + errorCode);
	            manager.stopServiceDiscovery(this);
	        }

	        @Override
	        public void onStopDiscoveryFailed(String serviceType, int errorCode) {
	            Log.e(TAG, "Stop discovery failed: " + errorCode);
	            manager.stopServiceDiscovery(this);
	        }
	    };
	}
	
	public void initializeResolveListener() {
		this.resol_listner = new NsdManager.ResolveListener() {

	        @Override
	        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
	            Log.e(TAG, "Resolve failed: " + errorCode);
	        }

	        @Override
	        public void onServiceResolved(NsdServiceInfo serviceInfo) {
	            Log.d(TAG, "Resolve succeeded: " + serviceInfo);
	            String name = serviceInfo.getServiceName();
	            if (name.equals(SERVICE_NAME)) {
	                Log.d(TAG, "Same IP");
	                return;
	            }
	            else {
	            	Log.i(TAG, "*****  "+name+" @ "+serviceInfo.getHost().getHostAddress()+":"+serviceInfo.getPort()+"  *****");
	    			for(int i = 0; i < NSD_List.size(); i++) {
	    				if(NSD_List.get(i).getServiceName().equals(name))
	    					return;
	    			}
	    			NSD_List.add(serviceInfo);
	    			parcelNSD();
	    			Log.i("NSD_List", "NSD Service Info object added");
	            }
	        }
	    };
	}
}