package com.example.first;

import java.util.ArrayList;

import com.example.first.MyListAdapter.ViewHolder;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ListEdit extends Activity {  
    private ListView lv;  
    private MyListAdapter mAdapter;  
    private Cursor list;  
    private Button bt_finish; 
    private int checkNum; // 记录选中的条目数量  
    private TextView tv_show;// 用于显示选中的条目数量  
    private NotesDbAdapter dbHelper;
    private String Table = null, from;
    private ArrayList<Boolean> use;
    private Long id;
    /** Called when the activity is first created. */  
  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.listedit);  
        /* 实例化各个控件 */  
        lv = (ListView) findViewById(R.id.lv);  
        bt_finish = (Button) findViewById(R.id.bt_finish); 
        tv_show = (TextView) findViewById(R.id.tv);  
        dbHelper = new NotesDbAdapter(this);
        dbHelper.open();
        Bundle extras = getIntent().getExtras();
        id = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID) : null;
        Table = extras != null ? extras.getString("TABLE") : null;
        from = extras != null ? extras.getString("from") : null;
        list=dbHelper.getall(Table);
        getUse();
        // 实例化自定义的MyAdapter  
        mAdapter = new MyListAdapter(list, this, use, id);  
        // 绑定Adapter  
        lv.setAdapter(mAdapter);  
        // 全选按钮的回调接口  
        bt_finish.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
            	if (from.equals("confict"))
            		finish();
            	else {
            		Intent intent = new Intent(ListEdit.this, NoteEdit.class);
                    intent.putExtra(NotesDbAdapter.KEY_ROWID, id);
                    intent.putExtra("TABLE", Table);
                    startActivity(intent);
            	}
            }  
        });  
  
        // 绑定listView的监听器  
        lv.setOnItemClickListener(new OnItemClickListener() {  
            @Override  
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
                    long arg3) {  
                // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤  
                ViewHolder holder = (ViewHolder) arg1.getTag();  
                // 改变CheckBox的状态  
                holder.cb.toggle();  
                // 将CheckBox的选中状况记录下来  
                MyListAdapter.getIsSelected().put(arg2, holder.cb.isChecked());  
                // 调整选定条目  
                if (holder.cb.isChecked() == true) {  
                    checkNum++;  
                    if (from.equals("confict")) {
                    	dbHelper.createConfict(Table, id, arg3+1);
                    	dbHelper.createConfict(Table, arg3+1, id);
                    } else {
                    	dbHelper.createAscend(Table, id, arg3+1);
                    	dbHelper.createAscend(Table, arg3+1, id);
                    }
                } else {  
                    checkNum--; 
                    if (from.equals("confict")) {
                    	dbHelper.deleteConfict(Table, id, arg3+1);
                    	dbHelper.deleteConfict(Table, arg3+1, id);
                    } else {
                    	dbHelper.deleteAscend(Table, id, arg3+1);
                    	dbHelper.deleteAscend(Table, arg3+1, id);
                    	
                    }
                }  
                // 用TextView显示  
                tv_show.setText("已选中" + checkNum + "项");  
            }  
        });  
    }  
    private void getUse()
    {
    	use = new ArrayList<Boolean>();
    	for (int i=0; i<list.getCount(); i++)
    		use.add(false);
    	Cursor mCursor = dbHelper.getallTable();
    	if (from.equals("confict"))
    		mCursor = dbHelper.getConfict(Table, id);
    	else 
    		mCursor = dbHelper.getAscend(Table, id);
    	System.out.println(id+" "+Table);
    	System.out.println("size:"+mCursor.getCount());
    	while (mCursor.moveToNext()) {  
	        int Nameindex = mCursor.getColumnIndex(NotesDbAdapter.KEY_B); 
	        use.set(mCursor.getInt(Nameindex)-1, true);
	        System.out.println("!!");
	        checkNum++;
	    }  
    }
    // 刷新listview和TextView的显示  
    private void dataChanged() {  
        // 通知listView刷新  
        mAdapter.notifyDataSetChanged();  
        // TextView显示最新的选中数目  
        tv_show.setText("已选中" + checkNum + "项");  
    };  
}  