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

import android.R.anim;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ListView;

public class ShowUserNote extends ExpandableListActivity {
	private static final int ACTIVITY_EDIT = 0x1001;
	protected final int menuEdit=Menu.FIRST;
	private List<String> groupArray;  
    private List<List<String>> childArray; 
    private String Table = null;
    ArrayList<Boolean> use = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expandable_layout);
        registerForContextMenu(getExpandableListView());
        setAdapter();
        
        
    }
    private void setAdapter() {
        if (Table==null) {
        	Bundle extras = getIntent().getExtras();
        	Table = extras != null ? extras.getString("table") : null;
        }
        setTitle(Table);
        new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				getAll();
			}
		}).start();
    }
    private void fillData() {
    	System.out.println("size: "+groupArray.size()+" "+childArray.size());
        ColorListAdapter adapter = new ColorListAdapter(this, 
                                    groupArray, 
                                    childArray, 
                                    use, -1);
        getExpandableListView().setAdapter(adapter); 
    }

    /**
     * startActivityForResult() �� onActivityResult()  ������ 
     * ǰ�� ��������������һ��activity ���� ���������һ�� activity �������¼�
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        fillData();
    }
    /**
     * ���������˵�  ���һ���˵�1���Ӳ��ɿ� ���ɴ���һ���Ҽ��˵�
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                    ContextMenuInfo menuInfo) {
            menu.add(0, menuEdit, 0,  "�༭��־");
            super.onCreateContextMenu(menu, v, menuInfo);
    }
    /**
     * �����˵���ѡ���¼�
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	
    		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo(); 
    		long id = ExpandableListView.getPackedPositionGroup(info.packedPosition)+1; 
    		int pos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
    		switch (item.getItemId()) { 
                    case menuEdit:
                        Intent intent2 = new Intent(this, UserNoteEdit.class);
                        intent2.putExtra(NotesDbAdapter.KEY_ROWID, id);
                        intent2.putExtra("TABLE", Table);
                        intent2.putExtra("contents", childArray.get(pos).get(0));
                        intent2.putExtra("summary", childArray.get(pos).get(1));
                        intent2.putExtra("executor", childArray.get(pos).get(2));
                        intent2.putExtra("note", groupArray.get(pos));
                        startActivityForResult(intent2, ACTIVITY_EDIT);
                        break;
            }
            return super.onContextItemSelected(item);
    }
    
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
    	 new AlertDialog.Builder(this)
         .setTitle("��ϸ����")
         .setMessage(childArray.get(groupPosition).get(childPosition))
         .show();
    	return super.onChildClick(parent, v, groupPosition, childPosition, id);
    };
    
    Handler myHandler = new Handler() {  
        public void handleMessage(Message msg) {   
             switch (msg.what) {   
                  case RESULT_OK:
                	  Toast.makeText(ShowUserNote.this,"���سɹ�", Toast.LENGTH_SHORT).show(); 
                	  fillData();
                      break;   
                  case RESULT_CANCELED:
                	  Toast.makeText(ShowUserNote.this,"����ʧ�ܣ�������������", Toast.LENGTH_LONG).show();
                	  break;
             }   
             super.handleMessage(msg);   
        }   
   };  
   
	public void getAll() {
		Toast.makeText(ShowUserNote.this, "��������...", Toast.LENGTH_LONG)
		.show();
		/* ���http����õ��Ľ�� */
		String result = "";
		/* ��Ҫ���͵����ݷ�� */
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("table", Table));
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
		if (result==null) {
			msg.what=RESULT_OK;
			myHandler.sendMessage(msg);
			return ;
		}
		// parse json data
		try {
			/* ���ַ���result����һ��JSONArray���� */
			JSONArray jArray = new JSONArray(result);
			groupArray = new ArrayList<String>();
	        childArray = new ArrayList<List<String>>();
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject json_data = jArray.getJSONObject(i);
				System.out.println("Success");
				System.out.println("result " + json_data.toString());
				List<String> tempArray01 = new ArrayList<String>();
	        	groupArray.add(json_data.getString("note"));
	        	tempArray01.add(json_data.getString("contents")); 
	        	tempArray01.add(json_data.getString("summary")); 
	        	tempArray01.add(json_data.getString("executor")); 
	        	childArray.add(tempArray01);
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
