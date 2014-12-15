package com.example.first;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.WorkSource;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
	private static final int login_noname = 0x1786;
	private EditText field_user;
	private EditText field_password;
    private Button button_confirm;
    private String user, password;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.login);//编辑的时候使用另一个UI
        findViews();
        Work(savedInstanceState);
    }
	private void findViews() {
        field_user = (EditText) findViewById(R.id.user);
        field_password = (EditText) findViewById(R.id.password);
        button_confirm = (Button) findViewById(R.id.confirm);
    }
	private void Work(Bundle savedInstanceState) {
        button_confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	user = field_user.getText().toString();
            	password = field_password.getText().toString();
            	if (user == null || password == null)
            		setResult(RESULT_CANCELED);
            	else {
            		Toast.makeText(Login.this, "正在连接...", Toast.LENGTH_LONG)
					.show();
            		new Thread(new Runnable(){  
            			@Override  
            		     public void run() {  
            		    // TODO Auto-generated method stub  
            		    	myLogin();  
            		    }  
            		}).start(); 
                	
            	}
                
            }
        });
    }
	Handler myHandler = new Handler() {  
        public void handleMessage(Message msg) {   
             switch (msg.what) {   
                  case RESULT_OK:
                	  	Toast.makeText(Login.this,"登录成功", Toast.LENGTH_SHORT).show(); 
                	  	Intent it = new Intent();  
                        it.putExtra("LOGINRESULT", ((EditText)findViewById(R.id.user)).getText()  
                                .toString());  
          				setResult(RESULT_OK, it);
          				finish();
                       break;   
                  case RESULT_CANCELED:
                	  Toast.makeText(Login.this,"用户名或密码错误，请重新登录", Toast.LENGTH_LONG).show();
                	  break;
                  case RESULT_FIRST_USER:
                	  Toast.makeText(Login.this,"请检查网络连接", Toast.LENGTH_LONG).show();
                	  finish();
                	  break;
             }   
             super.handleMessage(msg);   
        }   
   };  
	private void myLogin() {
		/* 存放http请求得到的结果 */
		String result = "";
		String ss = null;
		/* 将要发送的数据封包 */
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("user", user));
		nameValuePairs.add(new BasicNameValuePair("option", "1"));
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
			msg.what = RESULT_FIRST_USER; 
			myHandler.sendMessage(msg); 
			return ;
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
			msg.what = RESULT_FIRST_USER; 
			myHandler.sendMessage(msg); 
			return ;
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
					ss = json_data.getString("password");
				} else {
					ss += json_data.toString();
				}
			}
		} catch (JSONException e) {
			System.out.println("Error parsing json");
			msg.what = RESULT_CANCELED; 
			myHandler.sendMessage(msg); 
			return ;
		}
		
        if(ss.equals(password))
        {
            msg.what = RESULT_OK; 
        }
        else 
        {
            msg.what = RESULT_CANCELED; 
        }
        // 发送消息
        myHandler.sendMessage(msg); 
	}
}
