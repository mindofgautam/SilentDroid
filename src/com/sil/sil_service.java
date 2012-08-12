  package com.sil;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.IBinder;
import android.provider.MediaStore.Audio;
import android.util.Log;
import android.widget.TextView;

import com.sil.R;

//import com.lati.gpstest.MyLocationListener;


public class sil_service extends Service
{   
	private static final String TAG = sil_service.class.getSimpleName();
	  Timer timer;
	SQLiteDatabase db,db1;
	double lat,lon;
	String profile,latlon,loc,Text1, h,m,time1;
	int listcnt=0;
	Cursor cur1,cur2;
	static int cnt=0;
	TextView tv;
	int sil,vib,keys,mvol,rvol,avol,nvol;
	AudioManager audMangr;
	Context context;
	Timer myTimer;
	String mins;
	private TimerTask task = new TimerTask() 
	  {      
		  public void run() 
	  	  {       
			  Log.i(TAG, "Timer task doing work"); 
			  timechk();
	  	  }   
	  }; 
	
	  public void timechk()
	  {
		    db=openOrCreateDatabase("sil.db",SQLiteDatabase.CREATE_IF_NECESSARY, null);
		    db.setVersion(1);
		    db.setLocale(Locale.getDefault());
		    db.setLockingEnabled(true);
		    int hr,min;   
		    Calendar c = Calendar.getInstance();  
		    hr = c.get(Calendar.HOUR_OF_DAY);
		    min=c.get(Calendar.MINUTE);
		    
        	
        	if(min<10)     
        	time1=hr+":"+"0"+min;
        	else
        	time1=hr+":"+min;
        	
        	if(hr>11)
        	time1=time1+" PM";
        	else
        	time1=time1+" AM";
        	
        	if(hr<10)
        		time1="0"+time1;	    
		   
		    cur2 = db.query("silent", null,null,null, null, null, null);
		    cur2.moveToFirst();
		   while (cur2.isAfterLast() == false) 
	    	{
	    	  cnt++;
	    	  Log.i(TAG, "cursor :"+cur2.getString(1)+"   sys time:"+time1); 
	    	  if(cur2.getString(1).equals(time1))
	    	  {
	    		  Log.i(TAG, "once value :"+cur2.getString(3)); 
	    		 
	    		  String ns = Context.NOTIFICATION_SERVICE;
	    	        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
	    	       // audMangr.setRingerMode(AudioManager.RINGER_MODE_SILENT);
	    	       
	    	        int icon = R.drawable.icon;
	    	        CharSequence tickerText = "Silence activated : "+cur2.getString(1);
	    	        long when = System.currentTimeMillis();
	    	        Notification notification = new Notification(icon, tickerText, when);
	    	        Context context = getApplicationContext();
	    	        CharSequence contentTitle = "Silence activated : "+cur2.getString(1);
	    	        CharSequence contentText = "Android Silencer";
	    	        Intent notificationIntent = new Intent(sil_service.this, myact.class);
	    	        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
	    	        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
	    	        final int HELLO_ID = 1;
	    	        mNotificationManager.notify(HELLO_ID, notification);	  	    		  
	    	       
	    	        ///////////////////////////////////////////////////////////////////////////////////////////////    		  
		    		  audMangr= (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
		    		  AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
	
		    	        	 audMangr.setRingerMode(AudioManager.RINGER_MODE_SILENT);
	    	  }
	    	  else if(cur2.getString(2).equals(time1))
	    	  {
	    		  Log.i(TAG, "value :"+cur2.getString(3)); 
	    		  if(cur2.getInt(3) == 1)
	    		  {
	    			  db.execSQL("delete from silent where id='"+cur2.getInt(0)+"'");  
	    		  }
	    		  String ns = Context.NOTIFICATION_SERVICE;
	    	        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
	    	       // audMangr.setRingerMode(AudioManager.RINGER_MODE_SILENT);
	    	       
	    	        int icon = R.drawable.icon;
	    	        CharSequence tickerText = "Silence De-Activated : "+cur2.getString(2);
	    	        long when = System.currentTimeMillis();
	    	        Notification notification = new Notification(icon, tickerText, when);
	    	        Context context = getApplicationContext();
	    	        CharSequence contentTitle = "Silence De-Activated : "+cur2.getString(2);
	    	        CharSequence contentText = "Android Silencer";
	    	        Intent notificationIntent = new Intent(sil_service.this, myact.class);
	    	        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
	    	        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
	    	        final int HELLO_ID = 1;
	    	        mNotificationManager.notify(HELLO_ID, notification);	  	    		  
	    	       
	    	        ///////////////////////////////////////////////////////////////////////////////////////////////    		  
		    		int vibflag=0;  
	    	        audMangr= (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
		    		  AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		    		  if(audMangr.getRingerMode() ==2)
		    		  {
		    			  vibflag=1;
		    		  }	
		    		  
		    		  audMangr.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		    		  if(vibflag==1)
		    		  {
		    			  audMangr.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		    			  vibflag=0;
		    		  }	
		    		 	  
	    	  }
	    	  cur2.moveToNext();
	    	}
	  }
	  public IBinder onBind(Intent intent)
	{     
		// TODO Auto-generated method stub     
		return null;   
	}

	 public void onCreate()
	 {    
		 super.onCreate();     
		 Log.i(TAG, "Service creating");

    	timer = new Timer("TweetCollectorTimer");     
		timer.schedule(task, 5000, 20000);   
      }  

	 public void onDestroy() 
	 {     
		 super.onDestroy();     
		 Log.i(TAG, "Service destroying");      
	     timer.cancel();     
	     timer = null;   
	 } 
	 
	
};

