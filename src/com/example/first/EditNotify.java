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

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditNotify extends Activity {
    private TextView othersNote, othersContents, othersSummary, othersExecutor;
    private EditText note, contents, summary, executor;
    private Button button_confirm;
    private long id, did;
    private String send, receive, table, Note, Contents, Summary, Executor;
    private String myNote, myContents, mySummary, myExecutor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check);//�༭��ʱ��ʹ����һ��UI
        findViews();
        showAndUpdateNote(savedInstanceState);
    }
    
    private void findViews() {
    	othersNote = (TextView) findViewById(R.id.othersNote);
    	othersContents = (TextView) findViewById(R.id.othersContents);
    	othersSummary = (TextView) findViewById(R.id.othersSummary);
    	othersExecutor = (TextView) findViewById(R.id.othersExecutor);
        note = (EditText) findViewById(R.id.note);
        contents = (EditText) findViewById(R.id.contents);
        summary = (EditText) findViewById(R.id.summary);
        executor = (EditText) findViewById(R.id.executor);
        button_confirm = (Button) findViewById(R.id.confirm);
    }

    /**
     * �༭�޸ı������ұ���
     * @param savedInstanceState
     */
    private void showAndUpdateNote(Bundle savedInstanceState) {
            Bundle extras = getIntent().getExtras();
            id = extras != null ? extras.getLong("id") : null;
            did = extras != null ? extras.getLong("did") : null;
            table = extras != null ? extras.getString("table") : null;
            send = extras != null ? extras.getString("send") : null;
            receive = extras != null ? extras.getString("receive") : null;
            Note = extras != null ? extras.getString("note") : null;
            Contents = extras != null ? extras.getString("contents") : null;
            Summary = extras != null ? extras.getString("summary") : null;
            Executor = extras != null ? extras.getString("executor") : null;
           System.out.println(table+" "+send+" "+receive);
           new Thread(new Runnable() {
   			@Override
   			public void run() {
   				// TODO Auto-generated method stub
   				get();
   				showNote();
                button_confirm.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                    	new Thread(new Runnable() {
                			@Override
                			public void run() {
                				// TODO Auto-generated method stub
                				update(table, did, note.getText().toString(), contents.getText().toString(),
                                		summary.getText().toString(), executor.getText().toString());
                			}
                		}).start();
                        Intent intent = new Intent(EditNotify.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
   			}
   		}).start();
        
    }
    
    /**
     * ���Ҫ�༭�ı���
     */
    private void showNote() {
        if (table != null) {
            othersNote.setText(send+" �޸ı���Ϊ:" +Note);
            othersContents.setText(send+" �޸�����Ϊ:" +Contents);
            othersSummary.setText(send+" �޸�ժҪΪ:" +Summary);
            othersExecutor.setText(send+" �޸�ִ����Ϊ:" +Executor);
            note.setText(myNote);
            contents.setText(myContents);
            summary.setText(mySummary);
            executor.setText(myExecutor);
        }
    }
    Handler myHandler = new Handler() {  
        public void handleMessage(Message msg) {   
             switch (msg.what) {   
                  case RESULT_OK:
                	  Toast.makeText(EditNotify.this,"�޸ĳɹ�", Toast.LENGTH_SHORT).show(); 
                	  
                      break;   
                  case RESULT_CANCELED:
                	  Toast.makeText(EditNotify.this,"�޸�ʧ�ܣ�������������", Toast.LENGTH_LONG).show();
                	  break;
             }   
             super.handleMessage(msg);   
        }   
   };  
   
	public void get() {
		Toast.makeText(EditNotify.this, "��������...", Toast.LENGTH_LONG)
		.show();
		/* ���http����õ��Ľ�� */
		String result = "";
		/* ��Ҫ���͵����ݷ�� */
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("table", table));
		nameValuePairs.add(new BasicNameValuePair("id", Long.valueOf(did).toString()));
		nameValuePairs.add(new BasicNameValuePair("option", "10"));
		InputStream is = null;
		// http post
		try {
			/* ����һ��HttpClient��һ������ */
			HttpClient httpclient = new DefaultHttpClient();
			/* ����һ��HttpPost�Ķ��� */
			HttpPost httppost = new HttpPost(NotesDbAdapter.url);
			/* ������������� */
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"utf-8"));
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
		if (result==null) {
			return ;
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
					myNote = json_data.getString("note");
					myContents = json_data.getString("contents");
					myExecutor = json_data.getString("executor");
					mySummary = json_data.getString("summary");
				}
			}
		} catch (JSONException e) {
			System.out.println("Error parsing json");
		}
	}
	
	public void update(String table, long id, String note, String contents,
    		String summary, String executor) {
		Toast.makeText(EditNotify.this, "��������...", Toast.LENGTH_LONG)
		.show();
		/* ���http����õ��Ľ�� */
		String result = "";
		/* ��Ҫ���͵����ݷ�� */
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("table", table));
		nameValuePairs.add(new BasicNameValuePair("note", note));
		nameValuePairs.add(new BasicNameValuePair("contents", contents));
		nameValuePairs.add(new BasicNameValuePair("summary", summary));
		nameValuePairs.add(new BasicNameValuePair("executor", executor));
		nameValuePairs.add(new BasicNameValuePair("id", Long.valueOf(id).toString()));
		nameValuePairs.add(new BasicNameValuePair("option", "11"));
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
		msg.what = RESULT_OK;
		myHandler.sendMessage(msg);
	}
}
