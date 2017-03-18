package com.fallDetect.cos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.fallDetect.cos.R;
import com.fallDetect.cos.Service.FallDetection;


public class StartActivity extends Activity{
	
	//定义变量
	private ImageButton startBT,SettingBT,voiceBT;
	//获取全局变量
	OverallValue ov;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		startBT = (ImageButton) findViewById(R.id.OnButton);
		SettingBT=(ImageButton)findViewById(R.id.settingButton);
		voiceBT=(ImageButton)findViewById(R.id.voiceButton);
		
		ov=(OverallValue)getApplicationContext();
		Context mContext=getBaseContext();
		if(isServiceWork(mContext,"com.fallDetect.cos.Service.FallDetection"))
		{
			startBT.setBackgroundResource(R.drawable.stop);	
			ov.setRunningFlag(1);
		}
		//开始按钮
		startBT.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) 
			{
				Intent intent = new Intent(StartActivity.this,FallDetection.class);  
				// TODO Auto-generated method stub
				if(ov.getRunningFlag()==0)
				{
					
					startBT.setBackgroundResource(R.drawable.stop);	
					ov.setRunningFlag(1);
					//开启监控服务
					startService(intent); 
					Log.e("Service", "按键启动");
					
				}
				else 
				{
					
					startBT.setBackgroundResource(R.drawable.start);	
					ov.setRunningFlag(0);
					//关闭监控服务
					stopService(intent); 
					Log.e("Service", "按键关闭");
					
					
					
				}
			}
		});
		
		
		//设置按钮
		SettingBT.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent settingIntent = new Intent(StartActivity.this,Setting.class);
				startActivity(settingIntent);
			}
		});
		
		
		//声音开关
		voiceBT.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				/*
				if(ov.getVoiceFlag()==1)
				{
				voiceBT.setBackgroundResource(R.drawable.off);
				ov.setVoiceFlag(0);
				//关闭声音
				
				}
				else {
					voiceBT.setBackgroundResource(R.drawable.on);
					ov.setVoiceFlag(1);
					//打开声音
					
				}
				*/
				Intent waveIntent = new Intent(StartActivity.this,Advance.class);
				startActivity(waveIntent);
			}
		});
		
		
		
		
	}
	
	public boolean isServiceWork(Context mContext, String serviceName) {  
	    boolean isWork = false;  
	    ActivityManager myAM = (ActivityManager) mContext  
	            .getSystemService(Context.ACTIVITY_SERVICE);  
	    List<RunningServiceInfo> myList = myAM.getRunningServices(40);  
	    if (myList.size() <= 0) {  
	        return false;  
	    }  
	    for (int i = 0; i < myList.size(); i++) {  
	        String mName = myList.get(i).service.getClassName().toString();  
	        if (mName.equals(serviceName)) {  
	            isWork = true;  
	            break;  
	        }  
	    }  
	    return isWork;  
	}  

}
