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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.WorkSource;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class register extends Activity {
	private EditText field_user;
	private EditText field_password, field_confirmPassword;
    private Button button_confirm;
    private String user, password, confirmPassword;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.register);//编辑的时候使用另一个UI
        findViews();
        Work(savedInstanceState);
    }
	private void findViews() {
        field_user = (EditText) findViewById(R.id.user);
        field_password = (EditText) findViewById(R.id.password);
        field_confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        button_confirm = (Button) findViewById(R.id.confirm);
    }
	private void Work(Bundle savedInstanceState) {
        button_confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	user = field_user.getText().toString();
            	password = field_password.getText().toString();
            	confirmPassword = field_confirmPassword.getText().toString();
            	if (user == null || password == null || confirmPassword == null)
            		setResult(RESULT_CANCELED);
            	else if (!password.equals(confirmPassword)) {
            		Toast.makeText(register.this,"两次输入的密码不一致", Toast.LENGTH_LONG).show();
            	}
            	else {
            		new Thread(new Runnable(){  
            			@Override  
            		     public void run() {  
            		    // TODO Auto-generated method stub  
            		    	myRegister();  
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
                	  	Toast.makeText(register.this,"注册成功", Toast.LENGTH_SHORT).show(); 
          				setResult(RESULT_OK);
          				finish();
                       break;   
                  case RESULT_CANCELED:
                	  Toast.makeText(register.this,"用户名已存在", Toast.LENGTH_LONG).show();
                	  break;
             }   
             super.handleMessage(msg);   
        }   
   };  
	private void myRegister() {
		/* 存放http请求得到的结果 */
		String result = "";
		String ss = null;
		/* 将要发送的数据封包 */
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("user", user));
		nameValuePairs.add(new BasicNameValuePair("table", user+"_db"));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		nameValuePairs.add(new BasicNameValuePair("option", "2"));
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
			System.out.println("get = " + result + " size = "+result.length());
		} catch (Exception e) {
			System.out.println("Error converting to String");
		}
		Message msg = new Message(); 
        if(result.equals("1"))
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
