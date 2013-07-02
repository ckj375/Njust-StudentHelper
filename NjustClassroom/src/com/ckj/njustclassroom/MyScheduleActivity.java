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
	// ��������е������༭��
	EditText etName, etPass;
	private CheckBox checkbox = null;
	private CheckBox checkbox2 = null;
	private SharedPreferences sp = null;
	public static final String PREFS_NAME = "prefs"; // ����preference����
	public static final String REMEMBER_USERNAME = "remember"; // ��ס�û���
	public static final String REMEMBER_PASSWORD = "remember2"; // ��ס����
	public static final String USERNAME = "username";// �û������
	public static final String PASSWORD = "password";// ������

	// ��������а�ť
	Button bnLogin, get;
	private ProgressDialog mDialog;
	// ��������ʱ5����
	private static final int REQUEST_TIMEOUT = 5 * 1000;

	// ��ʼ��HttpClient�������ó�ʱ
	public static HttpClient getHttpClient() {
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		HttpClient client = new DefaultHttpClient(httpParams);
		return client;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ȥ��������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_myschedule);
		// ��ȡHttpClient����
		httpClient = getHttpClient();

		// ��ȡ�����е������༭��
		etName = (EditText) findViewById(R.id.username);
		etPass = (EditText) findViewById(R.id.password);
		// ��ȡ�����е�������ť
		bnLogin = (Button) findViewById(R.id.bnLogin);

		// Ϊ��¼��ť���¼�������
		bnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (validate()) {
					mDialog = new ProgressDialog(MyScheduleActivity.this);
					mDialog.setTitle("��½");
					mDialog.setMessage("���ڵ�½�����Ժ�...");
					mDialog.show();
					// ��ס�û���ѡ��
					if (checkbox.isChecked()) {
						saveRemember(true);
						saveUserName(etName.getText().toString().trim());
					} else {
						saveRemember(false);
						saveUserName("");
					}
					// ��ס����ѡ��
					if (checkbox2.isChecked()) {
						saveRemember2(true);
						savePassword(etPass.getText().toString().trim());
					} else {
						saveRemember2(false);
						savePassword("");
					}
					// ���������
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

	// �����û���
	private void saveUserName(String username) {
		Editor editor = sp.edit();
		// ��ȡ�༭��
		editor.putString(USERNAME, username);
		editor.commit();// ��������
		// editor.clear();// �������
	}

	// �����Ƿ񱣴��û���
	private void saveRemember(boolean remember) {
		Editor editor = sp.edit();
		// ��ȡ�༭��
		editor.putBoolean(REMEMBER_USERNAME, remember);
		editor.commit();
	}

	// ��ȡ������û���
	private String getUserName() {
		return sp.getString(USERNAME, "");
	}

	// �Ƿ�ѡ
	private boolean getRemember() {
		return sp.getBoolean(REMEMBER_USERNAME, false);
	}

	// ��������
	private void savePassword(String password) {
		Editor editor = sp.edit();
		// ��ȡ�༭��
		editor.putString(PASSWORD, password);
		editor.commit();// ��������
		// editor.clear();// �������
	}

	// �����Ƿ񱣴�����
	private void saveRemember2(boolean remember2) {
		Editor editor = sp.edit();
		// ��ȡ�༭��
		editor.putBoolean(REMEMBER_PASSWORD, remember2);
		editor.commit();
	}

	// ��ȡ���������
	private String getPassword() {
		return sp.getString(PASSWORD, "");
	}

	// �Ƿ�ѡ
	private boolean getRemember2() {
		return sp.getBoolean(REMEMBER_PASSWORD, false);
	}

	// LoginThread�߳���
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
						.equals("<HTML><BODY bgColor=#FFFCF1>�������˴����ѧ�Ż����룬���ܵ�¼��</BODY></HTML>")) {
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
				Toast.makeText(getApplicationContext(), "��������ʱ�޷�����...",
						Toast.LENGTH_LONG).show();
				break;
			case 1:
				mDialog.cancel();
				Toast.makeText(MyScheduleActivity.this, "�û���������������������룡",
						Toast.LENGTH_LONG).show();
				break;
			case 2:
				mDialog.cancel();
				// �л������ļ�
				setContentView(R.layout.myschedule2);
				// ��ȡ�ҵĿα�ť
				get = (Button) findViewById(R.id.get);

				// ���¼�������
				get.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						mDialog = new ProgressDialog(MyScheduleActivity.this);
						mDialog.setTitle("��ѯ");
						mDialog.setMessage("���ڲ�ѯ�����Ժ�...");
						mDialog.show();

						Thread thread1 = new Thread(new newThread());
						thread1.start();
					}
				});

				break;
			}

		}
	};

	// newThread�߳���
	class newThread implements Runnable {

		@Override
		public void run() {
			// ����һ��HttpGet����
			HttpGet get = new HttpGet(
					"http://219.230.100.200:6666/pls/wwwxk/xk.CourseView");
			try {
				// ����GET����
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

	// ���û�������û���,�������У���Ƿ�Ϊ��
	private boolean validate() {
		String username = etName.getText().toString().trim();
		if (username.equals("")) {
			Toast.makeText(this, "������ѧ�ţ�", Toast.LENGTH_LONG).show();
			return false;
		}
		String password = etPass.getText().toString().trim();
		if (password.equals("")) {
			Toast.makeText(this, "���������룡", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	// ����������Ͳ�ѯ����
	public static String loginServer(String username, String password) {
		String result = null;
		String url = "http://219.230.100.200:6666/pls/wwwxk/xk.login";
		HttpPost request = new HttpPost(url);
		// �Դ��ݵĲ������з�װ
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// ����û���������
		params.add(new BasicNameValuePair("stuid", username));
		params.add(new BasicNameValuePair("pwd", password));
		try {
			// �������������
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			// ִ�����󷵻���Ӧ
			HttpResponse response = httpClient.execute(request);

			// �ж��Ƿ�����ɹ�
			if (response.getStatusLine().getStatusCode() == 200) {
				// �����Ӧ��Ϣ
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

	// ��д���ؼ�
	public void onBackPressed() {
		finish();
	}

}
