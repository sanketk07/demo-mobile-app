package com.test.nativefeatures;

//import org.apache.http.util.EncodingUtils;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.annotation.SuppressLint;
import android.app.Activity;

@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//String postData = null;
		WebView webview;
		webview=(WebView) findViewById(R.id.activity_main_webview);
		
		WebSettings websettings=webview.getSettings();
		websettings.setJavaScriptEnabled(true);
		webview.setWebViewClient(new  WebViewClient());
		webview.addJavascriptInterface(new WebAppInterface(this), "click");
		
		//webview.postUrl("", EncodingUtils.getBytes(postData, "UTF-8"));
		webview.loadUrl("file:///android_asset/WebContent/pages/index.html");
		
	}
	
	
	

}
