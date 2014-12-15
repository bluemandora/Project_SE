package com.example.first;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.RowId;
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

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserNoteEdit extends Activity {
    private EditText note, contents, summary, executor;
    private Button button_confirm;
    private long rowId;
    private String table, Note, Contents, Summary, Executor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_edit);//编辑的时候使用另一个UI
        findViews();
        showAndUpdateNote(savedInstanceState);
    }

    private void findViews() {
        note = (EditText) findViewById(R.id.note);
        contents = (EditText) findViewById(R.id.contents);
        summary = (EditText) findViewById(R.id.summary);
        executor = (EditText) findViewById(R.id.executor);
        button_confirm = (Button) findViewById(R.id.confirm);
    }

    /**
     * 编辑修改备忘并且保存
     * @param savedInstanceState
     */
    private void showAndUpdateNote(Bundle savedInstanceState) {
        if (table == null) {
            Bundle extras = getIntent().getExtras();
            rowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID) : null;
            table = extras != null ? extras.getString("TABLE") : null;
            Note = extras != null ? extras.getString("note") : null;
            Summary = extras != null ? extras.getString("summary") : null;
            Contents = extras != null ? extras.getString("contents") : null;
            Executor = extras != null ? extras.getString("executor") : null;
        }
        showNote();
        button_confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	new Thread(new Runnable() {
        			@Override
        			public void run() {
        				// TODO Auto-generated method stub
        				update();
        			}
        		}).start();
            }
        });
    }
    
    /**
     * 填充要编辑的备忘
     */
    private void showNote() {
        if (table != null) {
            note.setText(Note);
            contents.setText(Contents);
            summary.setText(Summary);
            executor.setText(Executor);
        }
    }
    Handler myHandler = new Handler() {  
        public void handleMessage(Message msg) {   
             switch (msg.what) {   
                  case RESULT_OK:
                	  Toast.makeText(UserNoteEdit.this,"修改成功", Toast.LENGTH_SHORT).show(); 
                	  setResult(RESULT_OK);
                	  finish();
                      break;   
                  case RESULT_CANCELED:
                	  Toast.makeText(UserNoteEdit.this,"修改失败，请检查网络连接", Toast.LENGTH_LONG).show();
                	  break;
             }   
             super.handleMessage(msg);   
        }   
   };  
   
	public void update() {
		Toast.makeText(UserNoteEdit.this, "正在连接...", Toast.LENGTH_LONG)
		.show();
		/* 存放http请求得到的结果 */
		String result = "";
		/* 将要发送的数据封包 */
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("table", table));
		nameValuePairs.add(new BasicNameValuePair("did", Long.valueOf(rowId).toString()));
		nameValuePairs.add(new BasicNameValuePair("note", note.getText().toString()));
		nameValuePairs.add(new BasicNameValuePair("contents", contents.getText().toString()));
		nameValuePairs.add(new BasicNameValuePair("summary", summary.getText().toString()));
		nameValuePairs.add(new BasicNameValuePair("executor", executor.getText().toString()));
		nameValuePairs.add(new BasicNameValuePair("option", "9"));
		nameValuePairs.add(new BasicNameValuePair("send", MainActivity.userName));
		nameValuePairs.add(new BasicNameValuePair("receive", ShowUser.name));
		InputStream is = null;
		Message msg = new Message(); 
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
			msg.what = RESULT_CANCELED; 
			myHandler.sendMessage(msg); 
			return ;
		}
		msg.what = RESULT_OK; 
        // 发送消息
        myHandler.sendMessage(msg); 
	}
}
