package com.example.first;

import android.R.bool;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TableEdit extends Activity {
    
    private NotesDbAdapter dbHelper;
    private EditText field_note;
    private Button button_confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	System.out.println("in the edit");
        super.onCreate(savedInstanceState);
        dbHelper = new NotesDbAdapter(this);
        dbHelper.open(); 
        setContentView(R.layout.table_edit);//编辑的时候使用另一个UI
        findViews();
        showAndUpdateNote(savedInstanceState);
    }

    private void findViews() {
        field_note = (EditText) findViewById(R.id.note);
        button_confirm = (Button) findViewById(R.id.confirm);
    }
    boolean hasBlank(String str) {
    	int i = str.indexOf(' ');
    	if (i==-1) return false;
    	return true;
    }
    /**
     * 编辑修改备忘并且保存
     * @param savedInstanceState
     */
    private void showAndUpdateNote(Bundle savedInstanceState) {
        button_confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	String Table = field_note.getText().toString();
            	if (Table == null || (Table.charAt(0)<='9'&&Table.charAt(0)>='0') || hasBlank(Table)) {
            		Toast.makeText(TableEdit.this, "名字不能以数字开头，不能包含空白字符", Toast.LENGTH_LONG)
					.show();
            	}
            	else {
            		dbHelper.createTable(Table);
                	setResult(RESULT_OK);
                	finish();
            	}
                
            }
        });
    }
}
