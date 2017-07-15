package com.why.project.antziputilsdemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.why.project.antziputilsdemo.utils.AntZipUtils;

public class MainActivity extends AppCompatActivity {

	private Button btn_makeZip;
	private Button btn_unZip;
	private TextView tv_show;

	private MakeZipTask makeZipTask;//生成zip文件的异步请求类
	private UnZipTask unZipTask;//解压zip文件的异步请求类

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initViews();
		initEvents();
	}

	@Override
	public void onPause() {
		super.onPause();
		//cancel方法只是将对应的AsyncTask标记为cancel状态，并不是真正的取消线程的执行，在Java中并不能粗暴的停止线程，只能等线程执行完之后做后面的操作
		if (makeZipTask != null && makeZipTask.getStatus() == AsyncTask.Status.RUNNING) {
			makeZipTask.cancel(true);
		}
		if (unZipTask != null && unZipTask.getStatus() == AsyncTask.Status.RUNNING) {
			unZipTask.cancel(true);
		}
	}

	private void initViews() {
		btn_makeZip = (Button) findViewById(R.id.btn_makeZip);
		btn_unZip = (Button) findViewById(R.id.btn_unZip);

		tv_show = (TextView) findViewById(R.id.tv_show);
	}

	private void initEvents() {
		btn_makeZip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//生成ZIP压缩包【建议异步执行】
				makeZipTask = new MakeZipTask();
				makeZipTask.execute();
			}
		});

		btn_unZip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//解压ZIP包【建议异步执行】
				unZipTask = new UnZipTask();
				unZipTask.execute();

			}
		});
	}


	/**
	 * 压缩文件的异步请求任务
	 *
	 */
	public class MakeZipTask extends AsyncTask<String, Void, String>{

		@Override
		protected void onPreExecute() {
			//显示进度对话框
			//showProgressDialog("");
			tv_show.setText("正在压缩...");
		}

		@Override
		protected String doInBackground(String... params) {
			String data = "";
			if(! isCancelled()){
				try {
					String[] srcFilePaths = new String[1];
					srcFilePaths[0] = Environment.getExternalStorageDirectory() + "/why";
					String zipPath = Environment.getExternalStorageDirectory() + "/why.zip";
					AntZipUtils.makeZip(srcFilePaths,zipPath);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return data;
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(isCancelled()){
				return;
			}
			try {
				Log.w("MainActivity","result="+result);
			}catch (Exception e) {
				if(! isCancelled()){
					//showShortToast("文件压缩失败");
					tv_show.setText("文件压缩失败");
				}
			} finally {
				if(! isCancelled()){
					//隐藏对话框
					//dismissProgressDialog();
					tv_show.setText("压缩完成");
				}
			}
		}
	}

	/**
	 * 解压文件的异步请求任务
	 *
	 */
	public class UnZipTask extends AsyncTask<String, Void, String>{

		@Override
		protected void onPreExecute() {
			//显示进度对话框
			//showProgressDialog("");
			tv_show.setText("正在解压...");
		}

		@Override
		protected String doInBackground(String... params) {
			String data = "";
			if(! isCancelled()){
				try {
					String zipPath = Environment.getExternalStorageDirectory() + "/why.zip";
					String targetDirPath = Environment.getExternalStorageDirectory() + "/why";
					AntZipUtils.unZip(zipPath,targetDirPath);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return data;
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(isCancelled()){
				return;
			}
			try {
				Log.w("MainActivity","result="+result);
			}catch (Exception e) {
				if(! isCancelled()){
					//showShortToast("文件解压失败");
					tv_show.setText("文件解压失败");
				}
			} finally {
				if(! isCancelled()){
					//隐藏对话框
					//dismissProgressDialog();
					tv_show.setText("解压完成");
				}
			}
		}
	}
}
