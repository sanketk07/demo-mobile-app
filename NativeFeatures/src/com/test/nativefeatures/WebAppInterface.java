package com.test.nativefeatures;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;

public class WebAppInterface extends Activity {

	private static Context mContext;
	private static final String TAG = "WebAppInterface";
	DeviceUtilities objDeviceUtilities=new DeviceUtilities();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mContext = getApplicationContext();
	}

	/** Instantiate the interface and set the context */
	public WebAppInterface(Context c) {
		mContext = c;
		Log.d(TAG, "Printing context "+mContext);
	}
	
	@JavascriptInterface
	public void addContactsToPhonebook(String contactName, String contactNumber){
		objDeviceUtilities.syncContactsToPhone(contactName, contactNumber);
		Log.d(TAG, "Contact added successfully");
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	public void makePhoneCall(String telephoneNumber){
		Log.d(TAG, "Entered makePhoneCall. Calling WebAppInterface");
		objDeviceUtilities.openPhoneDialog(telephoneNumber);
		Log.d(TAG, "Call action performed successfully");
	}

}
