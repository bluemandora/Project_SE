package com.example.first;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class NoteEdit extends Activity {
    
    private NotesDbAdapter dbHelper;
    private EditText note, contents, summary, executor;
    private ScrollView scroll;
    private Button button_confirm;
    private long rowId;
    private String table;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new NotesDbAdapter(this);
        dbHelper.open(); 
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
        scroll = (ScrollView) findViewById(R.id.scrollView1);
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
        }
        showAscend();
        showNote();
        button_confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dbHelper.update(table, rowId, note.getText().toString(), contents.getText().toString(),
                		summary.getText().toString(), executor.getText().toString());
                Intent intent = new Intent(NoteEdit.this, ShowNote.class);
                intent.putExtra("table", table);
                startActivity(intent);
            }
        });
    }
    
    /**
     * 填充要编辑的备忘
     */
    private void showNote() {
        if (table != null) {
            Cursor Note = dbHelper.get(table, rowId);
            note.setText(Note.getString(
                    Note.getColumnIndexOrThrow(NotesDbAdapter.KEY_NOTE)
                ));
            contents.setText(Note.getString(
                    Note.getColumnIndexOrThrow(NotesDbAdapter.KEY_CONTENTS)
                ));
            summary.setText(Note.getString(
                    Note.getColumnIndexOrThrow(NotesDbAdapter.KEY_SUMMARY)
                ));
            executor.setText(Note.getString(
                    Note.getColumnIndexOrThrow(NotesDbAdapter.KEY_EXECUTOR)
                ));
        }
    }
    
    private void showAscend() {
        if (table != null) {
        	Cursor mCursor = dbHelper.getAscend(table, rowId);
        	LinearLayout layout = new LinearLayout(this);
        	layout.setOrientation(LinearLayout.VERTICAL);
        	while (mCursor.moveToNext()) {  
        		System.out.println("success3");
    	        int Nameindex = mCursor.getInt(mCursor.getColumnIndex(NotesDbAdapter.KEY_B)); 
    	        TextView Note = new TextView(NoteEdit.this);
    	        TextView Contents = new TextView(NoteEdit.this);
    	        TextView Summary = new TextView(NoteEdit.this);
    	        TextView Executor = new TextView(NoteEdit.this);
    	        Cursor tmp = dbHelper.get(table, Nameindex);
    	        System.out.println(Nameindex);
    	        System.out.println(tmp.getString(tmp.getColumnIndex(NotesDbAdapter.KEY_NOTE)));
    	        System.out.println(tmp.getString(tmp.getColumnIndex(NotesDbAdapter.KEY_CONTENTS)));
    	        System.out.println(tmp.getString(tmp.getColumnIndex(NotesDbAdapter.KEY_SUMMARY)));
    	        System.out.println(tmp.getString(tmp.getColumnIndex(NotesDbAdapter.KEY_EXECUTOR)));
    	        Note.setText(tmp.getString(tmp.getColumnIndex(NotesDbAdapter.KEY_NOTE)));
    	        Contents.setText(tmp.getString(tmp.getColumnIndex(NotesDbAdapter.KEY_CONTENTS)));
    	        Summary.setText(tmp.getString(tmp.getColumnIndex(NotesDbAdapter.KEY_SUMMARY)));
    	        Executor.setText(tmp.getString(tmp.getColumnIndex(NotesDbAdapter.KEY_EXECUTOR)));
    	        layout.addView(Note);
    	        layout.addView(Contents);
    	        layout.addView(Summary);
    	        layout.addView(Executor);
    	    }  
        	scroll.addView(layout);
        }
    }
}
