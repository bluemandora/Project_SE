package com.example.first;
import java.util.Date;

import android.R.string;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NotesDbAdapter {

        private static final String DATABASE_NAME = "notes.db";
        private static final int DATABASE_VERSION = 1;

        private static final String DATABASE_TABLE = "notes";
        
        public static final String url = "http://1.discussnote.sinaapp.com/try.php";
        
        public static final String KEY_ROWID = "_id";
        public static final String KEY_CONTENTS = "contents";
        public static final String KEY_NOTE = "note";
        public static final String KEY_SUMMARY = "summary";
        public static final String KEY_EXECUTOR = "executor";
        public static final String KEY_CREATED = "created";
        public static final String KEY_A = "role1";
        public static final String KEY_B = "role2";

        private Context context = null;
        private DatabaseHelper dbHelper;
        private SQLiteDatabase db;

        String[] strCols = new String[] { KEY_ROWID, KEY_NOTE, KEY_CREATED };
        
        private static final String DATABASE_CREATE = 
                        "create table "+DATABASE_TABLE
                        +"("
                        + "_id INTEGER PRIMARY KEY," 
                        + "note TEXT," 
                        + "created INTEGER,"
                        + "modified INTEGER" 
                        + ");";

        public NotesDbAdapter(Context ctx) {
                this.context = ctx;
        }

        public NotesDbAdapter open() throws SQLException {
                dbHelper = new DatabaseHelper(context);
                db = dbHelper.getWritableDatabase();
                return this;
        }

        public void close() {
                dbHelper.close();
        }
        
        // add an table
        public long createTable(String Note) {
                Date now = new Date();
                ContentValues args = new ContentValues();
                args.put(KEY_NOTE, Note);
                args.put(KEY_CREATED, now.getTime());
                String sql = "create table "+Note
                        +"("
                        + "_id INTEGER PRIMARY KEY," 
                        + "note TEXT," 
                        + "contents TEXT," 
                        + "summary TEXT," 
                        + "executor TEXT," 
                        + "created INTEGER,"
                        + "modified INTEGER" 
                        + ");";
                db.execSQL(sql);
                db.execSQL("create table "+Note+"_conflict"
                        +"("
                        + "_id INTEGER PRIMARY KEY," 
                        + "role1 INTEGER," 
                        + "role2 INTEGER,"
                        + "created INTEGER" 
                        + ");");
                return db.insert(DATABASE_TABLE, null, args);
        }
        
        // remove an entry
        public boolean deleteTable(long rowId) {
        		Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {
                    KEY_ROWID, KEY_NOTE, KEY_CREATED }, KEY_ROWID + "=" + rowId,
                    null, null, null, null, null);
        		while (mCursor.moveToNext()) {  
        	        int Nameindex = mCursor.getColumnIndex(KEY_NOTE);  
        	        System.out.println(mCursor.getString(Nameindex));
        	        db.execSQL("DROP TABLE IF EXISTS " + mCursor.getString(Nameindex));
        	        db.execSQL("DROP TABLE IF EXISTS " + mCursor.getString(Nameindex)+"_conflict");
        	    }  
                return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
        }
        public void clear() {
        	db.execSQL("delete from "+DATABASE_TABLE);
        	db.execSQL("VACUUM");
        }
        // get all tables
        public Cursor getallTable() {
                 return db.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_NOTE,
                                 KEY_CREATED }, null, null, null, null, null);
         }
         
        // query single table
        public Cursor getTable(long rowId) throws SQLException {
                Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {
                                KEY_ROWID, KEY_NOTE, KEY_CREATED }, KEY_ROWID + "=" + rowId,
                                null, null, null, null, null);
                if (mCursor != null) {
                        mCursor.moveToFirst();
                }
                return mCursor;
        }
        // query several table
        public Cursor searchTable(String name) throws SQLException {
        	String current_sql_sel = "SELECT  * FROM "+DATABASE_TABLE +" where "+KEY_NOTE+" like '%"+name+"%'";
        	Cursor mCursor = db.rawQuery(current_sql_sel, null);
                if (mCursor != null) {
                        mCursor.moveToFirst();
                }
                return mCursor;
        }
     // query several note
        public Cursor searchNote(String table, String name) throws SQLException {
        	String current_sql_sel = "SELECT  * FROM "+table +" where "+KEY_NOTE+" like '%"+name+"%'"
        							+ " or "+KEY_CONTENTS+" like '%"+name+"%'"
        							+ " or "+KEY_SUMMARY+" like '%"+name+"%'"
        							+ " or "+KEY_EXECUTOR+" like '%"+name+"%'";
        	Cursor mCursor = db.rawQuery(current_sql_sel, null);
                return mCursor;
        }
        
        // add an entry
        public long create(String table, String note, String contents, String summary, String executor) {
                Date now = new Date();
                ContentValues args = new ContentValues();
                args.put(KEY_NOTE, note);
                args.put(KEY_CONTENTS, contents);
                args.put(KEY_SUMMARY, summary);
                args.put(KEY_EXECUTOR, executor);
                args.put(KEY_CREATED, now.getTime());
                return db.insert(table, null, args);
        }
        
        // add a conflict
        public long createConfict(String table, long a, long b) {
                Date now = new Date();
                ContentValues args = new ContentValues();
                args.put(KEY_A, a); 
                args.put(KEY_B, b); 
                args.put(KEY_CREATED, now.getTime());
                long id = db.insert(table+"_conflict", null, args);
                System.out.println("db add " + a + " "+ b);
                return id;
        }
        
        // delete conflict
        public void deleteConfict(String table, long a, long b) {
        	db.execSQL("delete from "+table+"_conflict"+" where role1="+a+" and "+"role2="+b);
        }
        
     // delete conflict
        public void deleteConfict(String table) {
        	db.execSQL("delete from "+table+"_conflict");
        }
        
        // get conflict
        public Cursor getConfict(String table, long id) {
        	Cursor mCursor = db.rawQuery("select * from "+table+"_conflict"+" where role1="+id, null);
        	return mCursor;
        }

        // remove an entry
        public boolean delete(String table, long rowId) {
        		db.delete(table+"_conflict", KEY_A + "=" + rowId, null);
        		db.delete(table+"_conflict", KEY_B + "=" + rowId, null);
                return db.delete(table, KEY_ROWID + "=" + rowId, null) > 0;
        }

        // query single entry
        public Cursor get(String table, long rowId) throws SQLException {
                Cursor mCursor = db.query(true, table, new String[] {
                                KEY_ROWID, KEY_NOTE, KEY_CONTENTS, KEY_SUMMARY, KEY_EXECUTOR, KEY_CREATED }, KEY_ROWID + "=" + rowId,
                                null, null, null, null, null);
                if (mCursor != null) {
                        mCursor.moveToFirst();
                }
                return mCursor;
        }
        // get all entries
       public Cursor getall(String table) {
                return db.query(table, new String[] { KEY_ROWID, KEY_NOTE, KEY_CONTENTS, 
                                KEY_SUMMARY, KEY_EXECUTOR, KEY_CREATED }, null, null, null, null, null);
        }
        
        // update
        public boolean update(String table, long rowId, String note, String contents, String summary, String executor) {
                ContentValues args = new ContentValues();
                args.put(KEY_NOTE, note);
                args.put(KEY_CONTENTS, contents);
                args.put(KEY_SUMMARY, summary);
                args.put(KEY_EXECUTOR, executor);
                return db.update(table, args, KEY_ROWID + "=" + rowId, null) > 0;
        }
        
        //初始化数据库连接时  做一些动作
        private static class DatabaseHelper extends SQLiteOpenHelper {
                public DatabaseHelper(Context context) {
                        super(context, DATABASE_NAME, null, DATABASE_VERSION);
                }
                
                //Called when the database is created for the first time.
                @Override
                public void onCreate(SQLiteDatabase db) {
                        db.execSQL(DATABASE_CREATE);
                }
                
                //Called when the database needs to be upgraded.
                @Override
                public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
                        onCreate(db);
                }
                
                //Close any open database object.
                @Override
                public synchronized void close() {
                        super.close();
                }
                
                //Create and/or open a database.
                @Override
                public synchronized SQLiteDatabase getReadableDatabase() {
                        return super.getReadableDatabase();
                }
                
                //Create and/or open a database that will be used for reading and writing.
                @Override
                public synchronized SQLiteDatabase getWritableDatabase() {
                        return super.getWritableDatabase();
                }
                
                //Called when the database has been opened.
                @Override
                public void onOpen(SQLiteDatabase db) {
                        super.onOpen(db);
                }
        }
}
