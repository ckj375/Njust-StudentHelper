package com.ckj.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import com.ckj.njustclassroom.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class UpdateManager extends Thread {
	private static final int DOWNLOAD = 1;
	private static final int DOWNLOAD_FINISH = 2;
	private Context mContext;
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;
	HashMap<String, String> mHashMap;
	private int progress;
	private boolean cancelUpdate = false;
	private String mSavePath;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case DOWNLOAD:

				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:

				installApk();
				break;

			default:
				break;
			}
		};
	};

	public UpdateManager(Context context) {
		this.mContext = context;
		checkUpdate();
	}

	// 检查更新
	public void checkUpdate() {
		if (isNetworkAvailable(mContext)) {

			if (isUpdate()) {

				showNoticeDialog();
			} else {

				Toast.makeText(mContext, R.string.soft_update_no,
						Toast.LENGTH_SHORT).show();

			}
		} else {
			Toast toast = Toast.makeText(mContext, "请检查网络连接是否正常",
					Toast.LENGTH_SHORT);
			toast.show();
		}

	}

	// 判断网络是否连接
	private boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {

			NetworkInfo info = cm.getActiveNetworkInfo();
			if (info != null) {
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	// 判断是否有新版本
	private boolean isUpdate() {
		int versionCode = getVersionCode(mContext);
		InputStream inStream = getIn(mContext);
		ParseXmlService service = new ParseXmlService();
		try {
			mHashMap = service.parseXml(inStream);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (null != mHashMap) {
			int serviceCode = Integer.valueOf(mHashMap.get("version"));

			if (serviceCode > versionCode) {
				return true;
			}
		}
		return false;
	}

	// 获取当前版本号
	private int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			versionCode = context.getPackageManager().getPackageInfo(
					"com.ckj.njustclassroom", 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;

	}

	// 获取远程对象的输入流
	private InputStream getIn(Context context) {
		InputStream in = null;
		try {
			URL url = new URL(
					"http://friends.aliapp.com/njustclassroom/version.xml");
			in = url.openStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return in;
	}

	// 提示软件更新
	private void showNoticeDialog() {

		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_update_title);
		builder.setMessage(R.string.soft_update_info);

		builder.setPositiveButton(R.string.soft_update_updatebtn,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						showDownloadDialog();
					}
				});

		builder.setNegativeButton(R.string.soft_update_later,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		Dialog noticeDialog = builder.create();
		noticeDialog.show();

	}

	// 显示下载进度对话框
	private void showDownloadDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_updating);

		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		builder.setView(v);

		builder.setNegativeButton(R.string.soft_update_cancel,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						cancelUpdate = true;
					}
				});
		mDownloadDialog = builder.create();
		mDownloadDialog.show();

		downloadApk();

	}

	// 下载软件
	private void downloadApk() {
		new downloadApkThread().start();

	}

	private class downloadApkThread extends Thread {
		@Override
		public void run() {
			try {

				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {

					String sdpath = Environment.getExternalStorageDirectory()
							+ "/";
					mSavePath = sdpath + "njustclassroom";
					URL url = new URL(mHashMap.get("url"));

					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.connect();

					int length = conn.getContentLength();

					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);

					if (!file.exists()) {
						file.mkdir();
					}
					File apkFile = new File(mSavePath, mHashMap.get("name"));
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;

					byte buf[] = new byte[1024];

					do {
						int numread = is.read(buf);
						count += numread;

						progress = (int) (((float) count / length) * 100);

						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0) {

							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}

						fos.write(buf, 0, numread);
					} while (!cancelUpdate);
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			mDownloadDialog.dismiss();
		}
	};

	// 安装软件
	private void installApk() {
		File apkfile = new File(mSavePath, mHashMap.get("name"));
		if (!apkfile.exists()) {
			return;
		}

		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
	}

}
