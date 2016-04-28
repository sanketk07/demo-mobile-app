package com.test.nativefeatures;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
//import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StatFs;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class DeviceUtilities {
	
	private static Context mContext;
	private static String TAG = "DeviceUtilities";
	static AlertDialog alertDialog;
	
	public static String getDeviceID(Context context) {
		String deviceId = null;

		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		
		deviceId = tm.getDeviceId();
		if (deviceId == null) {
			WifiManager wifiMan = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInf = wifiMan.getConnectionInfo();
			deviceId = wifiInf.getMacAddress() + System.currentTimeMillis();
		}

		return deviceId;
	}
	
	public static String getAppName(Context context) {
		return context.getString(R.string.app_name);
	}
	
	public static String getIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}
	
	public static String getManufacturer() {
		return android.os.Build.MANUFACTURER;
	}

	public static String getModel() {
		return android.os.Build.MODEL;
	}
	

	@SuppressWarnings("deprecation")
	public static String getFreeSpaceInBytes(Context context) throws IOException {
		long availableSpace = -1L;
		try {
			StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
			availableSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
		}
		
		catch (Exception e) {
			Log.e("Inside Free Space", e.getMessage().toString());
		}
		return String.valueOf(availableSpace);
	}
	
	public static String getAppVersion(Context context) {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pInfo.versionName;
		} catch (NameNotFoundException e) {
			return "0";
		}

	}
	
	public static int getAndroidVersion() {
		return android.os.Build.VERSION.SDK_INT;
	}

	public static String generateUUID(Context context) {
		String android_id = Secure.getString(context.getContentResolver(),Secure.ANDROID_ID);
		Log.i("System out", "android_id : " + android_id);

		final TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		Log.i("System out", "tmDevice : " + tmDevice);
		tmSerial = "" + tm.getSimSerialNumber();
		Log.i("System out", "tmSerial : " + tmSerial);
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String UUID = deviceUuid.toString();
		Log.i("System out", "UUID : " + UUID);
		return UUID;
	}
	
	public static String getMacId(Context context) {
		WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = manager.getConnectionInfo();
		return wifiInfo.getMacAddress();
	}
	
	
	public static boolean isWorkingInternetPersent(Context context) {
		
		ConnectivityManager connectivityManager = (ConnectivityManager)context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
			if (info != null){
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
			}
		}
		return false;
	}
	
	public boolean isPackageExisted(String targetPackage) {

		PackageManager pm = mContext.getPackageManager();
		List<ApplicationInfo> packages = pm
				.getInstalledApplications(PackageManager.GET_META_DATA);
		Log.d("packages", "installed" + packages);
		for (ApplicationInfo packageInfo : packages) {
			if (packageInfo.packageName.equals(targetPackage))
				return true;
		}
		return false;
	}
	
	public JSONArray cur2Json(Cursor cursor) throws JSONException{

	    JSONArray resultSet = new JSONArray();
	    cursor.moveToFirst();
	    while (cursor.isAfterLast() == false) {
	        int totalColumn = cursor.getColumnCount();
	        JSONObject rowObject = new JSONObject();   
	        for (int i = 0; i < totalColumn; i++) {
	            if (cursor.getColumnName(i) != null) {
	                try {
	                    rowObject.put(cursor.getColumnName(i),
	                            cursor.getString(i));
	                } catch (Exception e) {
	                    Log.d("cur2Json in "+TAG, e.getMessage());
	                }
	            }
	        }
	        resultSet.put(rowObject);
	        cursor.moveToNext();
	    }

	    //cursor.close();
	    return resultSet;

	} 
	
	public void syncContactsToPhone(String contactName, String contactNumber){
		
		Log.d("syncContactsToPhone in "+ TAG, "contactName from controller.js : --->" + contactName ); 
		Log.d("syncContactsToPhone in "+ TAG, "contactNumber from controller.js : ---> " + contactNumber );
		
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex = ops.size();

        ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                .withValue(RawContacts.ACCOUNT_TYPE, "com.google")
                .withValue(RawContacts.ACCOUNT_NAME, "testvisits123@gmail.com").build());
        ops.add(ContentProviderOperation
                .newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID,rawContactInsertIndex)
                .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                .withValue(StructuredName.DISPLAY_NAME, contactName) // Name of the person
                .build());
        ops.add(ContentProviderOperation
                .newInsert(Data.CONTENT_URI)
                .withValueBackReference(
                        ContactsContract.Data.RAW_CONTACT_ID,   rawContactInsertIndex)
                .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER, contactNumber) // Number of the person
                .withValue(Phone.TYPE, Phone.TYPE_MOBILE).build()); // Type of mobile number                    
        try
        {
        	mContext.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        }
        catch (RemoteException e)
        { 
            // error
        }
        catch (OperationApplicationException e) 
        {
            // error
        } 
        
        Log.d("syncContactsToPhone in "+TAG, "Contact Inserted successfully!" );
        Toast.makeText(mContext, "Contact Inserted successfully!", Toast.LENGTH_SHORT).show();
		
	}
	
	public Cursor accessPhone() throws JSONException{
		
		JSONObject obj;
		Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
		while (phones.moveToNext())
		{
		  String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
		  String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		 // String email = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.));
		  Log.d("Web","Names: "+name);
		  Log.d("Web","Phone number: "+phoneNumber);
		  Log.d("Web","Phone cursor: "+phones);
		  
		}
		
		
		JSONArray contactList=cur2Json(phones);
		Log.d("","length of contacts "+contactList.length());
		for(int i=0;i<contactList.length();i++){
			Log.d(""," contacts "+contactList.get(i));
			obj=(JSONObject)contactList.get(i);
			Iterator<?> keys = obj.keys();
			while( keys.hasNext() ) {
			    String key = (String)keys.next();
			    if ( obj.get(key) instanceof JSONObject ) {
			    	Log.d(TAG, "key:"+key+"   & value:"+obj.get(key));
			    }
			}
		}
		
		
		Log.d("Printing", "contactList is"+contactList);
	
		return phones;
		
		
	}
	
	public void showToast(String toast) {
		Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
	}
	
	@SuppressLint("SimpleDateFormat")
	public String convertDateFromJSON2String(String rawData){
		
		String convertedDate = null;
		
		try {
			JSONObject jsonObj = new JSONObject(rawData);
			
			Long dateFromJSON = Long.parseLong(jsonObj.getString("key"));
			Date tempDate = new Date(dateFromJSON);
			convertedDate = new SimpleDateFormat("MMM dd, yyyy").format(tempDate);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return convertedDate;
		
	}
	
	/*public Bundle loadGESS(String rawData){
		
		
		
			try {


				boolean packageExisted = isPackageExisted("com.ultimatix.touch");

				if (packageExisted) {
					Intent touchTravelIntent = new Intent("com.ultimatix.touch.worklist.LAUNCHTRAVEL");

					Bundle mBundle = new Bundle();

					mBundle.putString("visitId", jsonObj.getString("visitId"));
					mBundle.putString("travelFromDate", visitStartDate);
					mBundle.putString("travelToDate", visitEndDate);
					mBundle.putString("applicationFlag",jsonObj.getString("applicationFlag"));
					mBundle.putString("swon", jsonObj.getString("swon"));
					mBundle.putString("returnFlag", jsonObj.getString("returnFlag"));
					mBundle.putString("fromCity", jsonObj.getString("fromCity"));
					mBundle.putString("toCity", jsonObj.getString("toCity"));

					Log.d("Bundle value : ", "" + mBundle);

					if (null != touchTravelIntent) {
						touchTravelIntent.putExtra("catch_bundle", mBundle);
						mContext.startActivity(touchTravelIntent);
					}

				} else {
					DialogHelper.touchNotInstalledDialog(mContext);
				}

				Log.d("Reached WebAppInterface", "From controller.js");
			} catch (Exception e) {
				e.printStackTrace();
			}


		return null;
		
	}*/
	
    public void openPhoneDialog(String telephoneNumber) {
    
    	Log.d("WebAppInterface", "Inside callDialog");
    	Intent intent = new Intent(Intent.ACTION_DIAL);
    	intent.setData(Uri.parse(telephoneNumber));
    	mContext.startActivity(intent);
    	String url="tel:1234567890";
    	
    	if (url.indexOf("tel:") > -1) {
            mContext.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
           
        } 
     }
    

	
	public static void showNetworkDialog(final Activity activity) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
 
			// set title
			alertDialogBuilder.setTitle("No Network detected");
 
			// set dialog message
			alertDialogBuilder
				.setMessage("Please ensure an active network connection.")
				.setCancelable(false)
				.setNegativeButton("Exit",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						activity.finish();
						
					}
				  })
				  
			.setNeutralButton("Wifi Settings", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				
					activity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
				}
			})
			
			.setPositiveButton("Data Settings", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					
					alertDialog.dismiss();
					activity.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
					
            }
        }
				  );
 
				// create alert dialog
				alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();
				
			
	}
	
	public static void showBackPressedDialog(final Activity activity) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
 
			// set dialog message
			alertDialogBuilder
				.setMessage("Message to be displayed on back press")
				.setCancelable(false)
				.setNegativeButton("Exit",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						
						activity.finish();
						
					}
				  })
			
			.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					
					alertDialog.dismiss();
					
            }
        });
 
		// create alert dialog
		alertDialog = alertDialogBuilder.create();
 
		// show it
		alertDialog.show();
				
			
	}
	
	/*public static void showRequestTimeoutDialog(final Activity activity) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

 
			// set dialog message
			alertDialogBuilder
				.setMessage(activity.getResources().getString(R.string.back_press_message))
				.setCancelable(false)
				.setNegativeButton("Okay",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, close
						// current activity
						SSOLibrary.deleteToken(activity);
						activity.finish();
						
						
						
					}
				  });
 
		// create alert dialog
		alertDialog = alertDialogBuilder.create();
 
		// show it
		alertDialog.show();
				
			
	}*/
	
	public static void appplicationNotInstalledDialog(final Context context){
		AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(context);
		
		alertDialogBuilder
		.setTitle("Install application")
		/*.setIcon(context.getResources().getDrawable(R.drawable.ultimatix_touch_icon_small))*/
		.setMessage("Application is not installed")
		.setCancelable(true)
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
			}
		})
		
		.setPositiveButton("Open third application", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				DeviceUtilities objDeviceUtilities=new DeviceUtilities();
				objDeviceUtilities.launchApplication(mContext, "package name");
				
			 }
		});
		
		// create alert dialog
		alertDialog = alertDialogBuilder.create();
 
		// show it
		alertDialog.show();
		
	}
	
	public void launchApplication(final Context context, String packageName) {
		Intent applicationIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
		if (null != applicationIntent) {
			context.startActivity(applicationIntent);
			}
	}
	
	
}
