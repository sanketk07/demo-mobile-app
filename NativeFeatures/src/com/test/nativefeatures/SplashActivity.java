package com.test.nativefeatures;

import java.util.EmptyStackException;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("SetJavaScriptEnabled")
public class SplashActivity extends Activity {
	
	private Context context;
	
	private static final String TAG = "WebviewActivity";
	WebView webview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		
		
		webview=(WebView) findViewById(R.id.activity_splash_webview);
		
		WebSettings websettings=webview.getSettings();
		websettings.setJavaScriptEnabled(true);
		webview.setWebViewClient(new  WebViewClient());
		
		webview.loadUrl("file:///android_asset/WebContent/pages/splash.html");
		this.context = this;

		if (DeviceUtilities.isWorkingInternetPersent(SplashActivity.this)) {
			
			showBasicSplash();
			
		} else {
			
			DeviceUtilities.showNetworkDialog(SplashActivity.this);
			
		}

	}

	private void showBasicSplash() {

		Thread thread = new Thread() {

			@Override
			public void run() {
				synchronized (this) {
					try {
						
						wait(10000);
					}
					catch(EmptyStackException e)
					{
						Log.e(TAG,"Empty Stack Exception");
					}catch(InterruptedException e){
						Log.e(TAG,"Interrupted Exception");
					}
					catch (Exception e) {
						Log.e(TAG,"Exception");
					} finally {
						startActivity(new Intent(context, MainActivity.class));
						
					}
				}
			}
		};

	
		thread.start();

	}

	
	@Override
	public void onBackPressed() {
		finish();
	}

}
