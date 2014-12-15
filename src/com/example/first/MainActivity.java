package com.example.first;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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

import android.R.menu;
import android.R.string;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.LoginFilter.UsernameFilterGeneric;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ListActivity {
	protected final int menuInsert = Menu.FIRST;
	protected final int menuDelete = Menu.FIRST + 1;
	protected final int menuSearch = Menu.FIRST + 2;
	protected final int menuLogin = Menu.FIRST + 3;
	protected final int menuRegister = Menu.FIRST + 4;
	protected final int menuUpload = Menu.FIRST + 5;
	protected final int menuDownload = Menu.FIRST + 6;
	protected final int menuLogout = Menu.FIRST + 7;
	protected final int menuSearchPeople = Menu.FIRST + 8;
	private static final int STEPLOGIN = 1;
	private static final int ACTIVITY_EDIT = 0x1001;
	private static final int SEARCH_OK = 0x1fed;
	private static final int SEARCH_FAILED = 0x1fee;
	private static final int Download_OK = 0x1324;
	private static final int Download_CANCELED = 0x1536;
	private static final int Download_FINISH = 0x1546;
	private NotesDbAdapter dbHelper;
	private Cursor cursor;
	static public String userName = null;
	private String input;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		registerForContextMenu(getListView());
		setAdapter();
		setTitle("ѡ��һ������");
	}

	private void setAdapter() {
		dbHelper = new NotesDbAdapter(this);
		dbHelper.open();
		fillData();
	}

	private void fillData() {
		cursor = dbHelper.getallTable();
		// startManagingCursor(cursor);

		String[] from = new String[] { "note" };
		int[] to = new int[] { android.R.id.text1 };

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, cursor, from, to,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		setListAdapter(adapter);
	}

	private void fillData(String item) {
		cursor = dbHelper.searchTable(item);
		// startManagingCursor(cursor);

		String[] from = new String[] { "note" };
		int[] to = new int[] { android.R.id.text1 };

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, cursor, from, to,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, menuInsert, 0, R.string.addTable);
		menu.add(0, menuSearch, 0, "��ѯ����");
		menu.add(0, menuLogin, 0, "��¼");
		menu.add(0, menuRegister, 0, "ע��");
		menu.add(0, menuUpload, 0, "�ϴ�");
		MenuItem x = menu.findItem(menuUpload);
		x.setVisible(false);
		menu.add(0, menuDownload, 0, "����");
		x = menu.findItem(menuDownload);
		x.setVisible(false);
		menu.add(0, menuLogout, 0, "�˳���¼");
		x = menu.findItem(menuLogout);
		x.setVisible(false);
		menu.add(0, menuSearchPeople, 0, "��ѯ�û�");
		x = menu.findItem(menuSearchPeople);
		x.setVisible(false);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (userName != null) {
			MenuItem loginItem = menu.findItem(menuLogin);
			loginItem.setVisible(false);
			MenuItem registerItem = menu.findItem(menuRegister);
			registerItem.setVisible(false);
			MenuItem x = menu.findItem(menuUpload);
			x.setVisible(true);
			x = menu.findItem(menuDownload);
			x.setVisible(true);
			x = menu.findItem(menuLogout);
			x.setVisible(true);
			x = menu.findItem(menuSearchPeople);
			x.setVisible(true);
		} else {
			MenuItem loginItem = menu.findItem(menuLogin);
			loginItem.setVisible(true);
			MenuItem registerItem = menu.findItem(menuRegister);
			registerItem.setVisible(true);
			MenuItem x = menu.findItem(menuUpload);
			x.setVisible(false);
			x = menu.findItem(menuDownload);
			x.setVisible(false);
			x = menu.findItem(menuLogout);
			x.setVisible(false);
			x = menu.findItem(menuSearchPeople);
			x.setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * ��HOME�������Ĳ˵���ѡ���¼�
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case menuInsert:
			Intent intent = new Intent(this, TableEdit.class);
			startActivityForResult(intent, ACTIVITY_EDIT);
			break;
		case menuSearch:
			final EditText inputServer = new EditText(this);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("��ѯ����").setIcon(android.R.drawable.ic_dialog_info)
					.setView(inputServer).setNegativeButton("Cancel", null);
			builder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							String input = inputServer.getText().toString();
							fillData(input);
						}
					});
			builder.show();
			break;
		case menuLogin:
			Intent intent2 = new Intent(this, Login.class);
			startActivityForResult(intent2, STEPLOGIN);

			break;
		case menuRegister:
			Intent intent3 = new Intent(this, register.class);
			startActivityForResult(intent3, ACTIVITY_EDIT);
			break;
		case menuUpload:
			Upload();
			break;
		case menuDownload:
			Download();
			break;
		case menuLogout:
			userName = null;
			Intent intent5 = new Intent(this, PushService.class);
			stopService(intent5);
			break;
		case menuSearchPeople:
			final EditText inputServer2 = new EditText(this);
			AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
			builder2.setTitle("��ѯ�û�")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setView(inputServer2).setNegativeButton("Cancel", null);
			builder2.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							input = inputServer2.getText().toString();
							new Thread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									SearchUser();
								}
							}).start();
						}
					});
			builder2.show();
			break;
		case menuDelete:
			dbHelper.deleteTable(getListView().getSelectedItemId());
			fillData();
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * ��ѡ��ListView �е�һ�� View ʱ�Ķ��� ��������Ϊ �༭���������
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(this, ShowNote.class);
		intent.putExtra(NotesDbAdapter.KEY_ROWID, id);
		startActivityForResult(intent, ACTIVITY_EDIT);
	}

	/**
	 * startActivityForResult() �� onActivityResult() ������ ǰ�� ��������������һ��activity
	 * ���� ���������һ�� activity �������¼�
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == STEPLOGIN && resultCode == RESULT_OK) {
			userName = intent.getStringExtra("LOGINRESULT");
			Intent intent4 = new Intent(this, PushService.class);
			intent4.putExtra("name", userName);
			startService(intent4);
		}
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
	}

	/**
	 * ���������˵� ���һ���˵�1���Ӳ��ɿ� ���ɴ���һ���Ҽ��˵�
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(0, menuDelete, 0, "ɾ������");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	/**
	 * �����˵���ѡ���¼�
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo info;
		info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case menuDelete:
			Log.d("MENU", "item" + info.id);
			dbHelper.deleteTable(info.id);
			fillData();
			break;
		}
		return super.onContextItemSelected(item);
	}

	public void Upload() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				clear();
				myUpload();
			}
		}).start();
	}

	public void Download() {
		clearLocal();
		fillData();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				myDownload();
			}
		}).start();
	}

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RESULT_OK:
				Toast.makeText(MainActivity.this, "�ϴ��ɹ�", Toast.LENGTH_SHORT)
						.show();
				break;
			case RESULT_CANCELED:
				Toast.makeText(MainActivity.this, "�ϴ�ʧ�ܣ�������������",
						Toast.LENGTH_LONG).show();
				break;
			case Download_OK:
				fillData();
				break;
			case Download_FINISH:
				fillData();
				Toast.makeText(MainActivity.this, "���سɹ�", Toast.LENGTH_SHORT)
						.show();
				break;
			case Download_CANCELED:
				Toast.makeText(MainActivity.this, "����ʧ�ܣ�������������",
						Toast.LENGTH_LONG).show();
				break;
			case SEARCH_OK:
				Toast.makeText(MainActivity.this, "���ҳɹ�", Toast.LENGTH_SHORT)
						.show();
				Intent intent = new Intent(MainActivity.this, ShowUser.class);
				intent.putExtra("name", input);
				startActivityForResult(intent, ACTIVITY_EDIT);
				break;
			case SEARCH_FAILED:
				Toast.makeText(MainActivity.this, "����ʧ�ܣ��û���������",
						Toast.LENGTH_LONG).show();
				break;
			}
			super.handleMessage(msg);
		}
	};

	private void SearchUser() {
		Toast.makeText(MainActivity.this, "��������...", Toast.LENGTH_LONG)
		.show();
		/* ���http����õ��Ľ�� */
		String result = "";
		String ss = null;
		/* ��Ҫ���͵����ݷ�� */
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("user", input));
		nameValuePairs.add(new BasicNameValuePair("option", "1"));
		InputStream is = null;
		// http post
		try {
			/* ����һ��HttpClient��һ������ */
			HttpClient httpclient = new DefaultHttpClient();
			/* ����һ��HttpPost�Ķ��� */
			HttpPost httppost = new HttpPost(NotesDbAdapter.url);
			/* ������������� */
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
			/* ����HttpResponse���� */
			HttpResponse response = httpclient.execute(httppost);
			/* ��ȡ��λ�Ӧ����Ϣʵ�� */
			HttpEntity entity = response.getEntity();
			/* ����һ��ָ�����ʵ��������� */
			is = entity.getContent();
		} catch (Exception e) {
			System.out.println("Connectiong Error");
			e.printStackTrace();
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
		}
		// parse json data
		try {
			/* ���ַ���result����һ��JSONArray���� */
			JSONArray jArray = new JSONArray(result);
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject json_data = jArray.getJSONObject(i);
				System.out.println("Success");
				System.out.println("result " + json_data.toString());
				if (i == 0) {
					ss = json_data.getString("name");
				} else {
					ss += json_data.toString();
				}
			}
		} catch (JSONException e) {
			System.out.println("Error parsing json");
		}
		Message msg = new Message();
		if (ss != null) {
			msg.what = SEARCH_OK;
		} else {
			msg.what = SEARCH_FAILED;
		}
		// ������Ϣ
		myHandler.sendMessage(msg);
	}

	private void clear() {
		System.out.println("in clear");
		/* ���http����õ��Ľ�� */
		boolean flag = true;
		String result = "";
		String ss = null;
		/* ��Ҫ���͵����ݷ�� */
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("name", userName + "_db"));
		nameValuePairs.add(new BasicNameValuePair("option", "3"));
		if (!Connect(nameValuePairs))
			flag = false;
	}

	private void myUpload() {
		Toast.makeText(MainActivity.this, "��������...", Toast.LENGTH_LONG)
		.show();
		System.out.println("in up");
		/* ���http����õ��Ľ�� */
		cursor = dbHelper.getallTable();
		Boolean flag = true;
		while (cursor.moveToNext()) {
			String sql = cursor.getString(cursor
					.getColumnIndex(NotesDbAdapter.KEY_NOTE));
			String id = cursor.getString(cursor
					.getColumnIndex(NotesDbAdapter.KEY_ROWID));
			if (sql.contains("_conflict"))
				continue;
			/* ��Ҫ���͵����ݷ�� */
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("id", id));
			nameValuePairs
					.add(new BasicNameValuePair("name", userName + "_db"));
			nameValuePairs.add(new BasicNameValuePair("table", userName + "_"
					+ sql));
			nameValuePairs.add(new BasicNameValuePair("option", "4"));
			if (!Connect(nameValuePairs)) {
				System.out.println("table "+userName);
				flag = false;
				break;
			}
			Cursor mCursor = dbHelper.getall(sql);

			while (mCursor.moveToNext()) {
				id = mCursor.getString(mCursor
						.getColumnIndex(NotesDbAdapter.KEY_ROWID));
				String note = mCursor.getString(mCursor
						.getColumnIndex(NotesDbAdapter.KEY_NOTE));
				String contents = mCursor.getString(mCursor
						.getColumnIndex(NotesDbAdapter.KEY_CONTENTS));
				String summary = mCursor.getString(mCursor
						.getColumnIndex(NotesDbAdapter.KEY_SUMMARY));
				String executor = mCursor.getString(mCursor
						.getColumnIndex(NotesDbAdapter.KEY_EXECUTOR));
				ArrayList<NameValuePair> nameValuePairs2 = new ArrayList<NameValuePair>();
				System.out.println("subtable: " + sql);
				nameValuePairs2.add(new BasicNameValuePair("table", userName
						+ "_" + sql));
				nameValuePairs2.add(new BasicNameValuePair("id", id));
				nameValuePairs2.add(new BasicNameValuePair("note", note));
				nameValuePairs2
						.add(new BasicNameValuePair("contents", contents));
				nameValuePairs2.add(new BasicNameValuePair("summary", summary));
				nameValuePairs2
						.add(new BasicNameValuePair("executor", executor));
				nameValuePairs2.add(new BasicNameValuePair("option", "5"));
				if (!Connect(nameValuePairs2)) {
					System.out.println("table "+sql);
					flag = false;
					break;
				}
			}
			if (!flag)
				break;
		}
		Message msg = new Message();
		if (flag) {
			msg.what = RESULT_OK;
		} else {
			msg.what = RESULT_CANCELED;
		}
		// ������Ϣ
		myHandler.sendMessage(msg);
	}

	boolean Connect(ArrayList<NameValuePair> nameValuePairs) {
		String result = "";
		String ss = null;
		InputStream is = null;
		try {
			/* ����һ��HttpClient��һ������ */
			HttpClient httpclient = new DefaultHttpClient();
			/* ����һ��HttpPost�Ķ��� */
			HttpPost httppost = new HttpPost(NotesDbAdapter.url);
			/* ������������� */
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			/* ����HttpResponse���� */
			HttpResponse response = httpclient.execute(httppost);
			/* ��ȡ��λ�Ӧ����Ϣʵ�� */
			HttpEntity entity = response.getEntity();
			/* ����һ��ָ�����ʵ��������� */
			is = entity.getContent();
		} catch (Exception e) {
			System.out.println("Connectiong Error");
			e.printStackTrace();
			return false;
		}
		// convert response to string
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "utf-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			is.close();
			result = sb.toString();
			System.out
					.println("get = " + result + " size = " + result.length());
		} catch (Exception e) {
			System.out.println("Error converting to String");
			return false;
		}
		if (result.equals("1"))
			return true;
		return false;
	}

	void clearLocal() {
		cursor = dbHelper.getallTable();
		while (cursor.moveToNext()) {
			long tableID = cursor.getLong(cursor
					.getColumnIndex(NotesDbAdapter.KEY_ROWID));
			dbHelper.deleteTable(tableID);
		}
		dbHelper.clear();
	}

	void myDownload() {
		Toast.makeText(MainActivity.this, "��������...", Toast.LENGTH_LONG)
		.show();
		/* ���http����õ��Ľ�� */
		String result = "";
		String ss = null;
		/* ��Ҫ���͵����ݷ�� */
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("table", userName + "_db"));
		nameValuePairs.add(new BasicNameValuePair("option", "6"));
		InputStream is = null;
		Message msg = new Message();
		// http post
		try {
			/* ����һ��HttpClient��һ������ */
			HttpClient httpclient = new DefaultHttpClient();
			/* ����һ��HttpPost�Ķ��� */
			HttpPost httppost = new HttpPost(NotesDbAdapter.url);
			/* ������������� */
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
			/* ����HttpResponse���� */
			HttpResponse response = httpclient.execute(httppost);
			/* ��ȡ��λ�Ӧ����Ϣʵ�� */
			HttpEntity entity = response.getEntity();
			/* ����һ��ָ�����ʵ��������� */
			is = entity.getContent();
		} catch (Exception e) {
			System.out.println("Connectiong Error");
			e.printStackTrace();
			msg.what = Download_CANCELED;
			myHandler.sendMessage(msg);
			return;
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
			msg.what = Download_OK;
			myHandler.sendMessage(msg);
			return;
		}
		// parse json data
		try {
			/* ���ַ���result����һ��JSONArray���� */
			JSONArray jArray = new JSONArray(result);
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject json_data = jArray.getJSONObject(i);
				System.out.println("Success");
				System.out.println("result " + json_data.toString());
				ss = json_data.getString("note");
				// System.out.println("ss before:" + ss);
				String tt = ss.substring(userName.length() + 1, ss.length());
				System.out.println("ss after:" + tt);
				getTable(tt);
			}
		} catch (JSONException e) {
			System.out.println("Error parsing json");
			msg.what = Download_FINISH;
			myHandler.sendMessage(msg);
			return;
		}
		msg.what = Download_FINISH;
		// ������Ϣ
		myHandler.sendMessage(msg);
	}

	void getTable(String table) {
		/* ���http����õ��Ľ�� */
		dbHelper.createTable(table);
		String result = "";
		String ss = null;
		/* ��Ҫ���͵����ݷ�� */
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("table", userName + "_"
				+ table));
		nameValuePairs.add(new BasicNameValuePair("option", "6"));
		InputStream is = null;
		Message msg = new Message();
		// http post
		try {
			/* ����һ��HttpClient��һ������ */
			HttpClient httpclient = new DefaultHttpClient();
			/* ����һ��HttpPost�Ķ��� */
			HttpPost httppost = new HttpPost(NotesDbAdapter.url);
			/* ������������� */
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
			/* ����HttpResponse���� */
			HttpResponse response = httpclient.execute(httppost);
			/* ��ȡ��λ�Ӧ����Ϣʵ�� */
			HttpEntity entity = response.getEntity();
			/* ����һ��ָ�����ʵ��������� */
			is = entity.getContent();
		} catch (Exception e) {
			System.out.println("Connectiong Error");
			e.printStackTrace();
			return;
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
			return;
		}
		// parse json data
		try {
			/* ���ַ���result����һ��JSONArray���� */
			JSONArray jArray = new JSONArray(result);
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject json_data = jArray.getJSONObject(i);
				System.out.println("Success");
				System.out.println("result " + json_data.toString());
				dbHelper.create(table, json_data.getString("note"),
						json_data.getString("contents"),
						json_data.getString("summary"),
						json_data.getString("executor"));
			}
		} catch (JSONException e) {
			System.out.println("Error parsing json");
			return;
		}
		msg.what = Download_OK;
		// ������Ϣ
		myHandler.sendMessage(msg);
	}
}
