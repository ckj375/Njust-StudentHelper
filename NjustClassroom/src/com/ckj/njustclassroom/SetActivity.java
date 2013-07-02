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

// �������Activity
public class SetActivity extends Activity {
	// ����SharedPreferences���ڱ��濪ѧ����
	public static SharedPreferences preferences;
	ListView list;
	private ProgressDialog mDialog;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ȥ��������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_set);

		// ��ȡSharedPreferences,������sp.xml�ļ���
		preferences = getSharedPreferences("sp", Context.MODE_WORLD_WRITEABLE);

		list = (ListView) findViewById(R.id.list1);
		String arr[] = { "��������", "������", "�������" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, arr);

		list.setAdapter(adapter);

		// Ϊ�б�����¼�������
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
					mDialog.setTitle("����");
					mDialog.setMessage("���ڼ����£����Ժ�...");
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

	// ��ȡ����schooldate
	public static String getSchoolDate() {
		return preferences.getString("schooldate", "2013-03-04");
	}

	// ���ÿ�ѧ����
	public void setDate() {

		LayoutInflater inflater = LayoutInflater.from(SetActivity.this);
		final View v = inflater.inflate(R.layout.serverip, null);
		final EditText et = (EditText) v.findViewById(R.id.schooldate);
		et.setText(getSchoolDate());
		AlertDialog.Builder builder = new AlertDialog.Builder(SetActivity.this);
		builder
		// ���ñ���
		.setTitle("��ѧ��������(XXXX-XX-XX)")
				.setView(v)
				// ����ȷ����ť
				.setPositiveButton("ȷ��",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								String str = et.getText().toString()
										.trim();
								// ��ȡ�༭��
								Editor editor = preferences.edit();
								editor.putString("schooldate", str);
								editor.commit();
								// ���������
								InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
								if (imm.isActive()) {
									imm.hideSoftInputFromWindow(
											v.getApplicationWindowToken(), 0);
								}
								System.exit(0);
							}
						})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						return;

					}
				});
		AlertDialog alert = builder.create();
		alert.show();

	}

	// newThread�߳���
	class newThread implements Runnable {

		@Override
		public void run() {
			Looper.prepare();
			new UpdateManager(SetActivity.this).start();
			mDialog.cancel();
			Looper.loop();
		}
	}

	// ��ʾ������Ϣ
	public void showAbout() {
		AlertDialog alertDialog = new AlertDialog.Builder(SetActivity.this)
				.setTitle("�������")
				.setMessage(
						"����ѧ������2.9.1(16)\n\n" + "�˰汾������Android2.1�����Ϲ̼�\n\n"
								+ "���κ�������黶ӭ���������䣺895515692@qq.com\n\n"
								+ "All rights reserved by CKJ @ 2012")
				.setPositiveButton("ȷ��", new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						return;
					}
				}).create();

		alertDialog.show();

	}

}
