package com.ckj.njustclassroom;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ckj.update.UpdateManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/* 
 * 软件名称：南理工学生助手
 * 作者：陈凯健
 * 时间：2012/08/07
 * 主界面
 */

public class MainActivity extends Activity {
	protected static final int DOWNLOAD_FINISH = 0;
	private int week = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去除标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		// 图片资源数组
		int[] imageIds = { R.drawable.kebiao, R.drawable.chengji,
				R.drawable.jiaoshi, R.drawable.shezhi };
		// String资源数组
		String[] arr = { "课表", "成绩", "教室", "设置" };
		// 创建一个List，元素是map
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < imageIds.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("image", imageIds[i]);
			listItem.put("tv", arr[i]);
			listItems.add(listItem);
		}
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
				R.layout.cell, new String[] { "image", "tv" }, new int[] {
						R.id.image, R.id.tv });
		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(simpleAdapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				switch (position) {
				case 0:
					showMyclass();
					break;
				case 1:
					showMyGrade();
					break;
				case 2:
					showMyClassroom();
					break;
				case 3:
					showSet();
					break;
				default:
					break;
				}

			}
		});

		new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				new UpdateManager(MainActivity.this).start();
				Looper.loop();
			}
		}).start();
		// 日期设置
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int date = calendar.get(Calendar.DATE);
		int weekday = calendar.get(Calendar.DAY_OF_WEEK);

		SharedPreferences pres = getSharedPreferences("sp", MODE_WORLD_READABLE);
		String strDate = pres.getString("schooldate", "2013-03-04");
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Date dateBegin = null;

		TextView tv2 = (TextView) findViewById(R.id.tv2);
		String str2 = null;
		String str = null;
		try {
			// dateBegin = myFormatter.parse("2013-03-04");
			dateBegin = myFormatter.parse(strDate);
			Date dateNow = new Date();

			long dayBetween = (dateNow.getTime() - dateBegin.getTime())
					/ (24 * 60 * 60 * 1000 * 7);
			if (dayBetween >= 0) {
				week += dayBetween;

			} else {
				week = 1;
			}
			switch (weekday) {
			case 1:
				str2 = "Sun.";
				break;
			case 2:
				str2 = "Mon.";
				break;
			case 3:
				str2 = "Tues";
				break;
			case 4:
				str2 = "Wed.";
				break;
			case 5:
				str2 = "Thur.";
				break;
			case 6:
				str2 = "Fri.";
				break;
			case 7:
				str2 = "Sat.";
				break;
			}
			str = year + "/" + (month + 1) + "/" + date + "/" + str2 + "第"
					+ week + "周";

		} catch (ParseException e) {
			str="日期格式不正确！";
		}
		tv2.setText(str);

	}

	// 转入课表查询界面
	private void showMyclass() {
		Intent intent = new Intent();
		intent.setClass(this, MyScheduleActivity.class);
		this.startActivity(intent);
		// overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	// 转入成绩查询界面
	private void showMyGrade() {
		Intent intent = new Intent();
		intent.setClass(this, MyGradeActivity.class);
		this.startActivity(intent);
		// overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	// 转入教室查询界面
	private void showMyClassroom() {
		int p = week;
		Intent intent = new Intent();
		intent.putExtra("week", p);
		intent.setClass(this, MyClassroomActivity.class);
		this.startActivity(intent);
		// overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	// 转入设置界面
	private void showSet() {
		Intent intent = new Intent();
		intent.setClass(this, SetActivity.class);
		this.startActivity(intent);
		// overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	// 退出程序
	private void showQuit() {
		finish();
	}

	// 重写返回键
	public void onBackPressed() {
		showQuit();
	}

}