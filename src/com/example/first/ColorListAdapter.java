package com.example.first;
import java.util.ArrayList;
import java.util.List;
 
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
 
public class ColorListAdapter extends BaseExpandableListAdapter {
	private ArrayList<Boolean> use;
	private long id;
	private Activity activity;  
	private List<String> groupArray;  
    private List<List<String>> childArray; 
    public ColorListAdapter(Activity a,List<String> groupArray,List<List<String>> childArray, 
    		ArrayList<Boolean> use, long id) {
    	activity = a;  
        this.groupArray = groupArray;  
        this.childArray = childArray;
		this.use=use;
		this.id=id-1;
	}
    @Override  
    public Object getChild(int groupPosition, int childPosition) {  
        // TODO Auto-generated method stub  
        return childArray.get(groupPosition).get(childPosition);  
    }  
    @Override  
    public long getChildId(int groupPosition, int childPosition) {  
        // TODO Auto-generated method stub  
        return childPosition;  
    }  
    @Override  
    public View getChildView(int groupPosition, int childPosition,  
            boolean isLastChild, View convertView, ViewGroup parent) {  
        // TODO Auto-generated method stub  
        String string = childArray.get(groupPosition).get(childPosition);  
        return getTextView(string);  
    }  
    @Override  
    public int getChildrenCount(int groupPosition) {  
        // TODO Auto-generated method stub  
        return childArray.get(groupPosition).size();  
    }  
    @Override  
    public Object getGroup(int groupPosition) {  
        // TODO Auto-generated method stub  
        return groupArray.get(groupPosition);  
    }  
    @Override  
    public int getGroupCount() {  
        // TODO Auto-generated method stub  
        return groupArray.size();  
    }  
    @Override  
    public long getGroupId(int groupPosition) {  
        // TODO Auto-generated method stub  
        return groupPosition;  
    }  
    @Override
    public View getGroupView(int position, boolean args, View convertView,ViewGroup parent){
    	String string = groupArray.get(position);  
        TextView view = getTextView(string); 
    	if (position==id)
        	view.setBackgroundColor(Color.parseColor("#F0FFFF"));//颜色设置
        else if (id!=-1&&use!=null&&use.get(position)) {
            view.setBackgroundColor(Color.parseColor("#FFDAB9"));//颜色设置
            System.out.println(position+" "+id);
        }
        return view; 
    } 
    @Override  
    public boolean hasStableIds() {  
        // TODO Auto-generated method stub  
        return false;  
    }  
    @Override  
    public boolean isChildSelectable(int groupPosition, int childPosition) {  
        // TODO Auto-generated method stub  
        return true;  
    }  
    private TextView getTextView(String string){  
    	AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 64);  
        TextView text = new TextView(activity);  
        text.setLayoutParams(layoutParams);  
        // Center the text vertically  
        text.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);  
        // Set the text starting position  
        text.setPadding(36, 0, 0, 0);  
        text.setText(string);  
        return text;  
    }  
    
}