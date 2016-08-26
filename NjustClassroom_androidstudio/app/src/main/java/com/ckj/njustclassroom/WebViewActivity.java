package com.ckj.njustclassroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;

public class WebViewActivity extends Activity {
	WebView webview;
	Button get;
	String html;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ȥ��������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.webview);
		Intent intent = getIntent();
		html = intent.getStringExtra("html");

		webview = (WebView) findViewById(R.id.response);
		webview.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);

	}

	// ��д���ؼ�
	public void onBackPressed() {
		finish();
	}

}