package com.sil;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TimePicker;
import android.widget.Toast;

public class sil_page extends Activity {

	private Button b1,b2,b3;
	static final int TIME_DIALOG_ID = 0;
	OnTimeSetListener mTimeSetListener;
	int hr;
	AudioManager audMangr;
	int min,flag=0;;
	 Calendar cal = new GregorianCalendar();
	String profile,time1,time2;
	String val1,gpname,gtime;
	int gonce;
	Cursor num,cur;
	SQLiteDatabase db;
	int[] ids;
	    public void onCreate(Bundle savedInstanceState) 
	    {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.silent_page);
	      //  startService(new Intent(sil_service.class.getName()));
	        startService(new Intent(this, sil_service.class));
	        
	        db=openOrCreateDatabase("sil.db",SQLiteDatabase.CREATE_IF_NECESSARY, null);
	        db.setVersion(1);
	        db.setLocale(Locale.getDefault());
	        db.setLockingEnabled(true);
	      
	        b1 = (Button) findViewById(R.id.button1);
	        b1.setClickable(true);
	        b1.setOnClickListener(new View.OnClickListener() 
	        {
	        public void onClick(View v) 
	        {
	        showDialog(TIME_DIALOG_ID);
	        flag=1;
	        }
	        });
	        
	        b2 = (Button) findViewById(R.id.button2);
	        b2.setClickable(true);
	        b2.setOnClickListener(new View.OnClickListener() 
	        {
	        public void onClick(View v) 
	        {
	        showDialog(TIME_DIALOG_ID);
	        flag=2;
	        }
	        });
	   
	        mTimeSetListener =new OnTimeSetListener() 
	        {
	        public void onTimeSet(TimePicker view, int hourOfDay, int minute) 
	        {
	        	hr=hourOfDay;
	        	min=minute;
	        	String mins;
	        	if(min<10)     
	        	time2=hr+":"+"0"+min;
	        	else
	        	time2=hr+":"+min;
	        	
	        	if(hr>11)
	        	time2=time2+" PM";
	        	else
	        	time2=time2+" AM";
	        	
	        	if(hr<10)
	        		time2="0"+time2;
	        		
	        	if(flag==1)
	        	{
	        		time1=time2;
	        		b1.setText(time2);
	        		flag=0;
	        	}
	        	else if(flag==2)
	        	{
	        		b2.setText(time2);
	        		flag=0;
	        	}        	
	        }
	        }; 
	        
	        
	        b3 = (Button) findViewById(R.id.button3);
	        b3.setOnClickListener(new View.OnClickListener() 
	        {
	        public void onClick(View v) 
	        {
	        	int count;
	        	
	        	
	        	//num=db.query("gps", null, null, null, null, null, null);
	        	//count=count+num.getCount()+1;
	        	boolean b1;
	        	int once;
	        	CheckBox cb1=(CheckBox)findViewById(R.id.checkBox1);
	        	 b1=cb1.isChecked();
	             if(b1==true)
	             {   once=1;    
	            
	             } 
	             else
	             {   once=0;    }
	             
	             cur=db.rawQuery("select id from silent where stime like '"+time1+"'AND etime like '"+time2+"'",null);
     	     	cur.moveToFirst();
     	     	if(time1.equals(time2)==true)
     	     	{
     	     		Toast.makeText(getApplicationContext(),"Start time and End time can't be same", Toast.LENGTH_SHORT).show();
     	     	}
     	     	else
     	     	{
     	     	
     	     		if(cur.getCount() >0)
     	     		{
     	     			Toast.makeText(getApplicationContext()," Rule already exist !!! ", Toast.LENGTH_LONG).show();
     	     		}
     	     		else
     	     		{
     	     			//Adjust the id column ,,,,, messed up when deleting in between
     	     			
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

     	     	        	ids=new int[listcnt];
     	     	        	cur.moveToFirst();
     	     	        	int move=0;
     	     	        	while (cur.isAfterLast() == false) 
     	     	        	{
     	     	        		ids[move]=cur.getInt(0);
     	     	        		cur.moveToNext();
     	     	        		move++;
     	     	        	}
     	     	        	cur.close();
     	     	        	int tmp;
     	     	        	for(int i=0;i<listcnt;i++)
     	     	        	{
     	     	        		for(int j=i;j<listcnt;j++)
     	     	        		{
     	     	        			if(ids[i]>ids[j])
     	     	        			{
     	     	        				tmp=ids[i];
     	     	        				ids[i]=ids[j];
     	     	        				ids[j]=tmp;
     	     	        			}
     	     	        		}
     	     	        	}
     	     	        	for(int i=0;i<listcnt;i++)
     	     	        	{
     	     	        		ContentValues values1=new ContentValues();
     	     	        		values1.put("id",i+1);
     	     	        	db.update("silent",values1,"id = '"+ids[i]+"'", null);
     	     	        	}
     	     	        
     	     	        }
     	     	     ///////////////////////////////////////
     	     			
     	     	      num=db.query("silent",null, null, null, null, null, null);
     		        	if(num.getCount()!=0)
     		        	{
     		        		count=num.getCount()+1;
     		        	}
     		        	else
     		        	{
     		        		count =1;
     		        	}
     		        	
     	     			ContentValues values=new ContentValues();
     	     			values.put("id",count);
     	     			values.put("stime", time1);
     	     			values.put("etime",time2);
     	     			values.put("once",once);
     	     			db.insert("silent", null, values);
     	     			Toast.makeText(getApplicationContext(),"Settings saved", Toast.LENGTH_SHORT).show();
     	     			Intent in=new Intent(sil_page.this,myact.class);
     	     			startActivity(in);
     	     			finish();
     	     		}
     	     	}
	        }
	       });
	   }
	    protected Dialog onCreateDialog(int id)
	     {
	     	switch (id) 
	     	{
	     	case TIME_DIALOG_ID:
	     	return new TimePickerDialog(this,mTimeSetListener, 0, 0, false);
	     	}
	     	return null;
	     	}
		 
		  public boolean onCreateOptionsMenu(Menu menu) 
		    {
		        menu.add(1,1,menu.FIRST,"Back");
		        
		        return super.onCreateOptionsMenu(menu);
		     }
		    public boolean onOptionsItemSelected(MenuItem item)
		    {    
		    	switch (item.getItemId())
		    	{   
		    	case 1:
		    		Intent in=new Intent(sil_page.this,myact.class);
	    			startActivity(in);
					finish();
		    	//	Toast.makeText(getApplicationContext(), "helooo", Toast.LENGTH_SHORT);
		    		break;    
		    	} 
		    	return true;
		    	}
}

