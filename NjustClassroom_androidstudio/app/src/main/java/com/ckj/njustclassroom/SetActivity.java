package com.ckj.njustclassroom;

import com.ckj.update.UpdateManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

// 软件设置Activity
public class SetActivity extends Activity {
	// 定义SharedPreferences用于保存开学日期
	public static SharedPreferences preferences;
	ListView list;
	private ProgressDialog mDialog;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去除标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_set);

		// 获取SharedPreferences,保存在sp.xml文件里
		preferences = getSharedPreferences("sp", Context.MODE_WORLD_WRITEABLE);

		list = (ListView) findViewById(R.id.list1);
		String arr[] = { "日期设置", "检查更新", "关于软件" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, arr);

		list.setAdapter(adapter);

		// 为列表项绑定事件监听器
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				switch (position) {
				case 0:
					setDate();
					break;
				case 1:
					mDialog = new ProgressDialog(SetActivity.this);
					mDialog.setTitle("更新");
					mDialog.setMessage("正在检查更新，请稍后...");
					mDialog.show();
					Thread newThread = new Thread(new newThread());
					newThread.start();
					break;
				case 2:
					showAbout();
					break;
				}
			}
		});

	}

	// 获取保存schooldate
	public static String getSchoolDate() {
		return preferences.getString("schooldate", "2013-03-04");
	}

	// 设置开学日期
	public void setDate() {

		LayoutInflater inflater = LayoutInflater.from(SetActivity.this);
		final View v = inflater.inflate(R.layout.serverip, null);
		final EditText et = (EditText) v.findViewById(R.id.schooldate);
		et.setText(getSchoolDate());
		AlertDialog.Builder builder = new AlertDialog.Builder(SetActivity.this);
		builder
		// 设置标题
		.setTitle("开学日期设置(XXXX-XX-XX)")
				.setView(v)
				// 设置确定按钮
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								String str = et.getText().toString()
										.trim();
								// 获取编辑器
								Editor editor = preferences.edit();
								editor.putString("schooldate", str);
								editor.commit();
								// 隐藏软键盘
								InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
								if (imm.isActive()) {
									imm.hideSoftInputFromWindow(
											v.getApplicationWindowToken(), 0);
								}
								System.exit(0);
							}
						})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						return;

					}
				});
		AlertDialog alert = builder.create();
		alert.show();

	}

	// newThread线程类
	class newThread implements Runnable {

		@Override
		public void run() {
			Looper.prepare();
			new UpdateManager(SetActivity.this).start();
			mDialog.cancel();
			Looper.loop();
		}
	}

	// 显示关于信息
	public void showAbout() {
		AlertDialog alertDialog = new AlertDialog.Builder(SetActivity.this)
				.setTitle("关于软件")
				.setMessage(
						"南理工学生助手2.9.1(16)\n\n" + "此版本适用于Android2.1及以上固件\n\n"
								+ "有任何问题或建议欢迎发送至邮箱：895515692@qq.com\n\n"
								+ "All rights reserved by CKJ @ 2012")
				.setPositiveButton("确定", new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						return;
					}
				}).create();

		alertDialog.show();

	}

}
