package com.example.first;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ShowUser extends ListActivity {
	private static final int STEPLOGIN = 1;  
	private static final int ACTIVITY_EDIT = 0x1001;
	public static String name;
	List<Map<String, Object>> show;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		registerForContextMenu(getListView());
		Bundle extras = getIntent().getExtras();
        name = extras != null ? extras.getString("name") : null;
        setAdapter();
		setTitle("�û� "+name+" �Ļ���");
	}

	private void setAdapter() {
		show=new ArrayList<Map<String,Object>>();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				getAllTable();
			}
		}).start();
	}

	private void fillData() {
		String[] from = new String[] { "note" };
		int[] to = new int[] { android.R.id.text1 };
		SimpleAdapter adapter = new SimpleAdapter(this, show, android.R.layout.simple_list_item_1, from, to);
		setListAdapter(adapter);
	}
	/**
	 * ��ѡ��ListView �е�һ�� View ʱ�Ķ��� ��������Ϊ �༭���������
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(this, ShowUserNote.class);
		intent.putExtra("table", show.get(position).get("note").toString());
		startActivityForResult(intent, ACTIVITY_EDIT);
	}

	/**
	 * startActivityForResult() �� onActivityResult() ������ ǰ�� ��������������һ��activity
	 * ���� ���������һ�� activity �������¼�
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
	}
	
	Handler myHandler = new Handler() {  
        public void handleMessage(Message msg) {   
             switch (msg.what) {   
                  case RESULT_OK:
                	  Toast.makeText(ShowUser.this,"���سɹ�", Toast.LENGTH_SHORT).show(); 
                	  fillData();
                      break;   
                  case RESULT_CANCELED:
                	  Toast.makeText(ShowUser.this,"����ʧ�ܣ�������������", Toast.LENGTH_LONG).show();
                	  break;
             }   
             super.handleMessage(msg);   
        }   
   };  
	public void getAllTable() {
		Toast.makeText(ShowUser.this, "��������...", Toast.LENGTH_LONG)
		.show();
		/* ���http����õ��Ľ�� */
		String result = "";
		String ss = null;
		/* ��Ҫ���͵����ݷ�� */
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("table", name+"_db"));
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
			msg.what = RESULT_CANCELED; 
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
			msg.what = RESULT_CANCELED; 
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
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("note", ss);
				show.add(map);
			}
		} catch (JSONException e) {
			System.out.println("Error parsing json");
			msg.what = RESULT_CANCELED; 
		}
		msg.what = RESULT_OK; 
        // ������Ϣ
        myHandler.sendMessage(msg); 
	}
}
