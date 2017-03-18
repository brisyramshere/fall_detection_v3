package com.fallDetect.cos;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class Other_setting extends Activity{
	//获取全局变量
	OverallValue ov;
	SeekBar senseBar;
	SeekBar timeBar;
	CheckBox gpsBox,soundBox;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.other_setting);
		
		ov=(OverallValue)getApplicationContext();
		
		senseBar=(SeekBar)findViewById(R.id.seekBar1);
		timeBar=(SeekBar)findViewById(R.id.seekBar2);
		gpsBox=(CheckBox)findViewById(R.id.checkBox1);
		soundBox=(CheckBox)findViewById(R.id.checkBox2);
		//阈值调整
		senseBar.setProgress((30-ov.getThreshold())/2);//进度条：0-10，阈值调整范围：10-30，反比。
		senseBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			private int progress = 0;
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				ov.setThreshold(30-2*progress);
			}
			
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				progress=arg1;
				
			}
		});
		//等待时间调整
		timeBar.setProgress(ov.getWaitTime()/6);//进度条0-10，时间：0-60
		timeBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			private int progress = 0;
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				ov.setWaitTime(progress*6);
				Toast.makeText(getApplicationContext(),"时间设定为 "+progress*6, 0).show();
			}
			
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				progress=arg1;
			}
		});
		
		//声音选项
		soundBox.setChecked(ov.getVoiceFlag());
		soundBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
            	if(arg1)
            		ov.setVoiceFlag(true);
            	else {
					ov.setVoiceFlag(false);
				}
                Toast.makeText(Other_setting.this, 
                        arg1?"已打开声音报警":"已关闭声音报警"    , Toast.LENGTH_LONG).show();
            }
        });
		
		//GPS选项
		
		gpsBox.setChecked(ov.getGPSFlag());
		gpsBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			Context mContext=getBaseContext();
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
            	if(arg1){
            		ov.setGPSFlag(true);
            		
            		if(!isOPen(mContext))  //判断GPS是否打开
            		{
            			openGPS(mContext);  //强制打开GPS
            		}
            			
            	}
            	else {
					ov.setGPSFlag(false);
				}
                Toast.makeText(Other_setting.this, 
                        arg1?"将通过短信发送位置信息":"将取消发送位置信息"    , Toast.LENGTH_LONG).show();
            }
        });
	}
	
    /** 
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的 
     * @param context 
     * @return true 表示开启 
     */  
    public static final boolean isOPen(final Context context) {  
        LocationManager locationManager   
                                 = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);  
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）  
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);  
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）  
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);  
        if (gps || network) {  
            return true;  
        }  
  
        return false;  
    }  
    
    /** 
     * 强制帮用户打开GPS 
     * @param context 
     */  
    public static final void openGPS(Context context) {  
        Intent GPSIntent = new Intent();  
        GPSIntent.setClassName("com.android.settings",  
                "com.android.settings.widget.SettingsAppWidgetProvider");  
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");  
        GPSIntent.setData(Uri.parse("custom:3"));  
        try {  
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();  
        } catch (CanceledException e) {  
            e.printStackTrace();  
        }  
    }  
}
