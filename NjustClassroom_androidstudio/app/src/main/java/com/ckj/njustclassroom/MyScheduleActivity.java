package com.ckj.njustclassroom;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MyScheduleActivity extends Activity {
	TextView tv3;
	static HttpClient httpClient;
	String html;
	// 定义界面中的两个编辑框
	EditText etName, etPass;
	private CheckBox checkbox = null;
	private CheckBox checkbox2 = null;
	private SharedPreferences sp = null;
	public static final String PREFS_NAME = "prefs"; // 设置preference名称
	public static final String REMEMBER_USERNAME = "remember"; // 记住用户名
	public static final String REMEMBER_PASSWORD = "remember2"; // 记住密码
	public static final String USERNAME = "username";// 用户名标记
	public static final String PASSWORD = "password";// 密码标记

	// 定义界面中按钮
	Button bnLogin, get;
	private ProgressDialog mDialog;
	// 设置请求超时5秒钟
	private static final int REQUEST_TIMEOUT = 5 * 1000;

	// 初始化HttpClient，并设置超时
	public static HttpClient getHttpClient() {
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		HttpClient client = new DefaultHttpClient(httpParams);
		return client;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去除标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_myschedule);
		// 获取HttpClient对象
		httpClient = getHttpClient();

		// 获取界面中的两个编辑框
		etName = (EditText) findViewById(R.id.username);
		etPass = (EditText) findViewById(R.id.password);
		// 获取界面中的两个按钮
		bnLogin = (Button) findViewById(R.id.bnLogin);

		// 为登录按钮绑定事件监听器
		bnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (validate()) {
					mDialog = new ProgressDialog(MyScheduleActivity.this);
					mDialog.setTitle("登陆");
					mDialog.setMessage("正在登陆，请稍后...");
					mDialog.show();
					// 记住用户名选项
					if (checkbox.isChecked()) {
						saveRemember(true);
						saveUserName(etName.getText().toString().trim());
					} else {
						saveRemember(false);
						saveUserName("");
					}
					// 记住密码选项
					if (checkbox2.isChecked()) {
						saveRemember2(true);
						savePassword(etPass.getText().toString().trim());
					} else {
						saveRemember2(false);
						savePassword("");
					}
					// 隐藏软键盘
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					if (imm.isActive()) {
						imm.hideSoftInputFromWindow(
								v.getApplicationWindowToken(), 0);
					}
					Thread loginThread = new Thread(new LoginThread());
					loginThread.start();
				}
			}
		});

		sp = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		checkbox = (CheckBox) findViewById(R.id.checkbox);
		checkbox2 = (CheckBox) findViewById(R.id.checkbox2);
		etName.setText(getUserName());
		etPass.setText(getPassword());
		checkbox.setChecked(getRemember());
		checkbox2.setChecked(getRemember2());

	}

	// 保存用户名
	private void saveUserName(String username) {
		Editor editor = sp.edit();
		// 获取编辑器
		editor.putString(USERNAME, username);
		editor.commit();// 保存数据
		// editor.clear();// 清除数据
	}

	// 设置是否保存用户名
	private void saveRemember(boolean remember) {
		Editor editor = sp.edit();
		// 获取编辑器
		editor.putBoolean(REMEMBER_USERNAME, remember);
		editor.commit();
	}

	// 获取保存的用户名
	private String getUserName() {
		return sp.getString(USERNAME, "");
	}

	// 是否勾选
	private boolean getRemember() {
		return sp.getBoolean(REMEMBER_USERNAME, false);
	}

	// 保存密码
	private void savePassword(String password) {
		Editor editor = sp.edit();
		// 获取编辑器
		editor.putString(PASSWORD, password);
		editor.commit();// 保存数据
		// editor.clear();// 清除数据
	}

	// 设置是否保存密码
	private void saveRemember2(boolean remember2) {
		Editor editor = sp.edit();
		// 获取编辑器
		editor.putBoolean(REMEMBER_PASSWORD, remember2);
		editor.commit();
	}

	// 获取保存的密码
	private String getPassword() {
		return sp.getString(PASSWORD, "");
	}

	// 是否勾选
	private boolean getRemember2() {
		return sp.getBoolean(REMEMBER_PASSWORD, false);
	}

	// LoginThread线程类
	class LoginThread implements Runnable {

		@Override
		public void run() {
			String username = etName.getText().toString().trim();
			String password = etPass.getText().toString().trim();
			Message msg = handler.obtainMessage();
			String result = loginServer(username, password);
			if (result == null) {
				msg.what = 0;
				handler.sendMessage(msg);
			} else {
				if (result
						.trim()
						.equals("<HTML><BODY bgColor=#FFFCF1>你输入了错误的学号或密码，不能登录！</BODY></HTML>")) {
					msg.what = 1;
					handler.sendMessage(msg);
				} else {
					msg.what = 2;
					handler.sendMessage(msg);
				}
			}
		}
	}

	// Handler
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				mDialog.cancel();
				Toast.makeText(getApplicationContext(), "服务器暂时无法访问...",
						Toast.LENGTH_LONG).show();
				break;
			case 1:
				mDialog.cancel();
				Toast.makeText(MyScheduleActivity.this, "用户名或密码错误，请重新输入！",
						Toast.LENGTH_LONG).show();
				break;
			case 2:
				mDialog.cancel();
				// 切换布局文件
				setContentView(R.layout.myschedule2);
				// 获取我的课表按钮
				get = (Button) findViewById(R.id.get);

				// 绑定事件监听器
				get.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						mDialog = new ProgressDialog(MyScheduleActivity.this);
						mDialog.setTitle("查询");
						mDialog.setMessage("正在查询，请稍后...");
						mDialog.show();

						Thread thread1 = new Thread(new newThread());
						thread1.start();
					}
				});

				break;
			}

		}
	};

	// newThread线程类
	class newThread implements Runnable {

		@Override
		public void run() {
			// 创建一个HttpGet对象
			HttpGet get = new HttpGet(
					"http://219.230.100.200:6666/pls/wwwxk/xk.CourseView");
			try {
				// 发送GET请求
				HttpResponse httpResponse = httpClient.execute(get);
				HttpEntity entity = httpResponse.getEntity();
				html = EntityUtils.toString(entity);
				if (html != null) {
					mDialog.cancel();
					showWebview();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 对用户输入的用户名,密码进行校验是否为空
	private boolean validate() {
		String username = etName.getText().toString().trim();
		if (username.equals("")) {
			Toast.makeText(this, "请输入学号！", Toast.LENGTH_LONG).show();
			return false;
		}
		String password = etPass.getText().toString().trim();
		if (password.equals("")) {
			Toast.makeText(this, "请输入密码！", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	// 向服务器发送查询请求
	public static String loginServer(String username, String password) {
		String result = null;
		String url = "http://219.230.100.200:6666/pls/wwwxk/xk.login";
		HttpPost request = new HttpPost(url);
		// 对传递的参数进行封装
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// 添加用户名和密码
		params.add(new BasicNameValuePair("stuid", username));
		params.add(new BasicNameValuePair("pwd", password));
		try {
			// 设置请求参数项
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			// 执行请求返回相应
			HttpResponse response = httpClient.execute(request);

			// 判断是否请求成功
			if (response.getStatusLine().getStatusCode() == 200) {
				// 获得响应信息
				result = EntityUtils.toString(response.getEntity());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	protected void showWebview() {

		String str = html;
		Intent intent = new Intent();
		intent.putExtra("html", str);

		intent.setClass(this, WebViewActivity.class);
		this.startActivity(intent);
	}

	// 重写返回键
	public void onBackPressed() {
		finish();
	}

}
