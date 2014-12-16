package com.example.first;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.anim;
import android.R.integer;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class PushService extends Service {

	// 获取消息线程
	private MessageThread messageThread = null;
	private static final int RESULT_OK = -1;
	private static final int RESULT_CANCELED = 0;

	// 通知栏消息
	private int messageNotificationID = 1000;
	private NotificationManager messageNotificatioManager = null;
	private String serverMessage;
	private Long id, did;
	private String send, receive, table, note, contents, summary, executor,
			name;
	private int currentapi = android.os.Build.VERSION.SDK_INT;
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 初始化
		name = intent.getStringExtra("name");
		System.out.println("in service, name is " + name);
		messageNotificatioManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// 开启线程
		messageThread = new MessageThread();
		messageThread.isRunning = true;
		messageThread.start();

		return super.onStartCommand(intent, flags, startId);
	}
	
	public void onDestroy() {
		System.out.println("destroy");
		messageThread.isRunning = false;
		super.onDestroy();
	};
	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RESULT_OK:
				showNotification(send, receive, table, note, contents,
						summary, executor, id, did);
				break;
			}
			super.handleMessage(msg);
		}
	};
	public void showNotification(String send, String receive, String table,
			String note, String contents, String summary, String executor,
			Long pid, Long did) {
		Notification messageNotification = new Notification();
		messageNotification.icon = R.drawable.ic_launcher;
		messageNotification.tickerText = "新消息";
		messageNotification.defaults = Notification.DEFAULT_SOUND;
		
		messageNotification.flags|=Notification.FLAG_AUTO_CANCEL;
		Intent messageIntent = new Intent(this, EditNotify.class);
		System.out.println("in notifi: " + table + " " + send + " " + summary);
		System.out.println("id: "+messageNotificationID);
		messageIntent.putExtra("id", pid);
		messageIntent.putExtra("did", did);
		messageIntent.putExtra("send", send);
		messageIntent.putExtra("receive", receive);
		messageIntent.putExtra("table", table);
		messageIntent.putExtra("note", note);
		messageIntent.putExtra("contents", contents);
		messageIntent.putExtra("summary", summary);
		messageIntent.putExtra("executor", executor);
		messageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		messageIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		PendingIntent messagePendingIntent = PendingIntent.getActivity(this, messageNotificationID,
				messageIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		
		messageNotification.setLatestEventInfo(
				PushService.this, "新修改", serverMessage+"修改了你的笔记",
				messagePendingIntent);
		messageNotificatioManager.notify(messageNotificationID,
			messageNotification);
	}

	/**
	 * 从服务器端获取消息
	 * 
	 */
	class MessageThread extends Thread {
		// 运行状态，下一步骤有大用
		public boolean isRunning = true;

		public void run() {
			while (isRunning) {
				try {

					// 获取服务器消息
					serverMessage = getServerMessage();
					if (serverMessage != null && !"".equals(serverMessage)) {
						Message msg = new Message();
						msg.what = RESULT_OK;
						myHandler.sendMessage(msg);
						// 每次通知完，通知ID递增一下，避免消息覆盖掉
						messageNotificationID++;
					}
					// 休息10分钟
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 这里以此方法为服务器Demo，仅作示例
	 * 
	 * @return 返回服务器要推送的消息，否则如果为空的话，不推送
	 */
	public String getServerMessage() {
		/* 存放http请求得到的结果 */
		String result = "";
		send = null;
		id = null;
		/* 将要发送的数据封包 */
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		System.out.println("request: name = " + name);
		nameValuePairs.add(new BasicNameValuePair("name", name));
		nameValuePairs.add(new BasicNameValuePair("option", "7"));
		InputStream is = null;
		// http post
		try {
			/* 创建一个HttpClient的一个对象 */
			HttpClient httpclient = new DefaultHttpClient();
			/* 创建一个HttpPost的对象 */
			HttpPost httppost = new HttpPost(NotesDbAdapter.url);
			/* 设置请求的数据 */
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
			/* 创建HttpResponse对象 */
			HttpResponse response = httpclient.execute(httppost);
			/* 获取这次回应的消息实体 */
			HttpEntity entity = response.getEntity();
			/* 创建一个指向对象实体的数据流 */
			is = entity.getContent();
		} catch (Exception e) {
			System.out.println("Connectiong Error");
			e.printStackTrace();
			return send;
		}
		// convert response to string
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "utf-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
			System.out.println("get = " + result);
		} catch (Exception e) {
			System.out.println("Error converting to String");
			return send;
		}
		// parse json data
		try {
			/* 从字符串result创建一个JSONArray对象 */
			JSONArray jArray = new JSONArray(result);
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject json_data = jArray.getJSONObject(i);
				System.out.println("Success");
				System.out.println("result " + json_data.toString());
				if (i == 0) {
					send = json_data.getString("send");
					receive = json_data.getString("receive");
					table = json_data.getString("tname");
					note = json_data.getString("note");
					summary = json_data.getString("summary");
					contents = json_data.getString("contents");
					executor = json_data.getString("executor");
					id = json_data.getLong("id");
					did = json_data.getLong("did");
					System.out.println("get id: " + id);
				}
			}
		} catch (JSONException e) {
			System.out.println("Error parsing json");
			return send;
		}
		if (id != null) {
			change(id);
		}
		return send;
	}

	public void change(Long id) {
		/* 存放http请求得到的结果 */
		/* 将要发送的数据封包 */
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("id", id.toString()));
		nameValuePairs.add(new BasicNameValuePair("option", "8"));
		InputStream is = null;
		// http post
		try {
			/* 创建一个HttpClient的一个对象 */
			HttpClient httpclient = new DefaultHttpClient();
			/* 创建一个HttpPost的对象 */
			HttpPost httppost = new HttpPost(NotesDbAdapter.url);
			/* 设置请求的数据 */
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
			/* 创建HttpResponse对象 */
			HttpResponse response = httpclient.execute(httppost);
			/* 获取这次回应的消息实体 */
			HttpEntity entity = response.getEntity();
			/* 创建一个指向对象实体的数据流 */
			is = entity.getContent();
		} catch (Exception e) {
			System.out.println("Connectiong Error");
			e.printStackTrace();
		}
	}
}
