package com.ckj.njustclassroom;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;

public class MyClassroomActivity extends Activity {
	private ExpandableListView elistview = null;
	private ExpandableListAdapter adapter = null;
	int m;
	int week;
	private ProgressDialog mDialog;

	String whichClass = null;
	String[] arr = { "firstClass", "secondClass", "thirdClass", "fourthClass",
			"fifthClass" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去除标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_myclassroom);

		Intent intent = getIntent();
		week = intent.getIntExtra("week", 0);

		elistview = (ExpandableListView) findViewById(R.id.list);
		adapter = new MyExpandableListAdapter(this);
		elistview.setAdapter(adapter);
		// 设置ExpandableLIstView左侧的小图标
		elistview.setGroupIndicator(getResources().getDrawable(
				R.drawable.icon_selector));
		// 为ExpandableLIstView绑定事件监听器
		elistview.setOnChildClickListener(new OnChildClickListenerImpl());

	}

	private class OnChildClickListenerImpl implements OnChildClickListener {

		@Override
		public boolean onChildClick(ExpandableListView arg0, View arg1,
				int groupPosition, int childPosition, long arg4) {
			m = groupPosition * 5 + childPosition;

			if (CheckNetworkState()) {
				mDialog = new ProgressDialog(MyClassroomActivity.this);
				mDialog.setTitle("更新");
				mDialog.setMessage("正在获取数据，请稍后...");
				mDialog.show();
				Thread thread1 = new Thread(new newThread());
				thread1.start();

			} else {
				Toast.makeText(MyClassroomActivity.this, "请检查网络连接是否正常...",
						Toast.LENGTH_SHORT).show();
			}

			return true;

		}
	}

	// newThread线程类
	class newThread implements Runnable {

		@Override
		public void run() {
			String result = "";
			switch (m % 5) {
			case 0:
				whichClass = arr[0];
				break;
			case 1:
				whichClass = arr[1];
				break;
			case 2:
				whichClass = arr[2];
				break;
			case 3:
				whichClass = arr[3];
				break;
			case 4:
				whichClass = arr[4];
				break;
			}
			SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = null;
			try {
				date = myFormatter.parse("2013-03-04");
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			date.setDate(date.getDate() + (week - 1) * 7 + m / 5);
			String strDate = myFormatter.format(date);

			String url = "http://www.vip9456.com/search/index.php?building=buildingFour"
					+ "&date=" + strDate + "&class=" + whichClass;
			try {
				Document doc = Jsoup.connect(url).timeout(6000).get();
				Elements arrs = doc.getElementsByTag("td");
				for (int i = 2; i < arrs.size() - 1; i++) {
					result += arrs.get(i).text() + "  ";
				}
				mDialog.cancel();
				ShowRooms(result);
			} catch (IOException e) {
				Toast.makeText(MyClassroomActivity.this, "网速不给力啊！", 5000)
						.show();
			}
		}
	}

	// 转入教室字符串显示Activity
	private void ShowRooms(String result) {
		Intent intent = new Intent();
		intent.putExtra("result", result);
		intent.setClass(this, ShowRoomsActivity.class);
		this.startActivity(intent);
	}

	// 检查网络状态
	public boolean CheckNetworkState() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		// 如果3G、wifi、2G等网络状态是连接的，则退出，否则显示提示信息进入网络设置界面
		if (mobile == State.CONNECTED || mobile == State.CONNECTING)
			return true;
		if (wifi == State.CONNECTED || wifi == State.CONNECTING)
			return true;
		return false;
	}

	// 重写返回键
	public void onBackPressed() {
		finish();
	}

}
