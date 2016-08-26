package com.ckj.njustclassroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class ShowRoomsActivity extends Activity {
	protected static final int FINISH = 0;
	TextView tv4;
	String result;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// È¥³ý±êÌâÀ¸
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_showrooms);
		Intent intent = getIntent();
		result = intent.getStringExtra("result");
		tv4 = (TextView) findViewById(R.id.tv4);
		tv4.setText(result);
	}
}
