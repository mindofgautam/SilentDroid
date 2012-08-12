package com.sil;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class myact extends Activity {
    /** Called when the activity is first created. */
	SQLiteDatabase db;
	Cursor cur;
	String time1,time2;
	static String[] time;
	String[] str1;
	Activity activity;
    public void onCreate(Bundle savedInstanceState) 
    {
    	activity=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        startService(new Intent(sil_service.class.getName())); 
        //Intent serviceIntent = new Intent(sil_service.class.getName());         
    	//getApplicationContext().startService(serviceIntent);  
        Log.i("Hello", "Service destroying");      
        db=openOrCreateDatabase("sil.db",SQLiteDatabase.CREATE_IF_NECESSARY, null);
        db.setVersion(1);
        db.setLocale(Locale.getDefault());
        db.setLockingEnabled(true);
        try
        {
        db.execSQL("create table silent(id INTEGER PRIMARY KEY,stime TEXT,etime TEXT,once INTEGER)");
        }
        catch(SQLException e)
        {}
        
        Button newprof=(Button) findViewById(R.id.button1);
        newprof.setOnClickListener(new OnClickListener()
       {
       	 public void onClick(View v)
            {
       		Intent in=new Intent(myact.this,sil_page.class);
    			startActivity(in);
    			//finish();
            }
       });
        
        cur = db.query("silent", null, null,null, null, null, null);
        int listcnt=0;
        if(cur.getCount()!=0)
        {
        	cur.moveToFirst();
        	while (cur.isAfterLast() == false) 
        	{
        		cur.moveToNext();
        		listcnt++;
        	}

        time=new String[listcnt];
        cur.moveToFirst();
        int move=0;
        while (cur.isAfterLast() == false) 
        {
        	time[move]=cur.getString(1)+"   -   "+cur.getString(2);
            cur.moveToNext();
            move++;
        }
        cur.close();
        
        ListView l=(ListView)findViewById(R.id.listView1); 
        l.setAdapter(new ArrayAdapter(this,android.R.layout.simple_list_item_1 ,time)); 

        l.setOnItemClickListener( new OnItemClickListener() 
        { 

        	public void onItemClick(AdapterView<?> adapter, View view, int pos, long arg3) 
        	{ 
        		String str=adapter.getItemAtPosition(pos).toString();
        		str1=str.split("   -   ");
        		cur=db.rawQuery("select id from silent where stime like '"+str1[0]+"'AND etime like '"+str1[1]+"'",null);
        	     	cur.moveToFirst();
        	     	
        	     	AlertDialog.Builder alertbox = new AlertDialog.Builder(activity); 
    	     		alertbox.setMessage("Do you want to delete this setting?"); 
    	     		alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
    	     		{ 
    	     			public void onClick(DialogInterface arg0, int arg1) 
    	     			{  
    	     				 db.execSQL("delete from silent where id = '"+cur.getInt(0)+"'");
    	     				Toast.makeText(getApplicationContext(), "Setting Deleted successfully", Toast.LENGTH_SHORT).show();
    	     				Intent in=new Intent(myact.this,myact.class);
    	     				startActivity(in);
    	     				finish();	
    	     			}             
    	     		});                         
    	     		alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() 
    	     		{ 
    	     			public void onClick(DialogInterface arg0, int arg1)
    	     			{                    
    	     				//	Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();                 
    	     			}            
    	     		});   
        	     	
        	     	if(cur.getCount() >0)
        	     	{
        	     		alertbox.show();     
        	     	}
        	     		   
        		}                      

        }); 
        }
    }
}