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
	//��ȡȫ�ֱ���
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
		//��ֵ����
		senseBar.setProgress((30-ov.getThreshold())/2);//��������0-10����ֵ������Χ��10-30�����ȡ�
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
		//�ȴ�ʱ�����
		timeBar.setProgress(ov.getWaitTime()/6);//������0-10��ʱ�䣺0-60
		timeBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			private int progress = 0;
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				ov.setWaitTime(progress*6);
				Toast.makeText(getApplicationContext(),"ʱ���趨Ϊ "+progress*6, 0).show();
			}
			
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				progress=arg1;
			}
		});
		
		//����ѡ��
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
                        arg1?"�Ѵ���������":"�ѹر���������"    , Toast.LENGTH_LONG).show();
            }
        });
		
		//GPSѡ��
		
		gpsBox.setChecked(ov.getGPSFlag());
		gpsBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			Context mContext=getBaseContext();
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
            	if(arg1){
            		ov.setGPSFlag(true);
            		
            		if(!isOPen(mContext))  //�ж�GPS�Ƿ��
            		{
            			openGPS(mContext);  //ǿ�ƴ�GPS
            		}
            			
            	}
            	else {
					ov.setGPSFlag(false);
				}
                Toast.makeText(Other_setting.this, 
                        arg1?"��ͨ�����ŷ���λ����Ϣ":"��ȡ������λ����Ϣ"    , Toast.LENGTH_LONG).show();
            }
        });
	}
	
    /** 
     * �ж�GPS�Ƿ�����GPS����AGPS����һ������Ϊ�ǿ����� 
     * @param context 
     * @return true ��ʾ���� 
     */  
    public static final boolean isOPen(final Context context) {  
        LocationManager locationManager   
                                 = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);  
        // ͨ��GPS���Ƕ�λ����λ������Ծ�ȷ���֣�ͨ��24�����Ƕ�λ��������Ϳտ��ĵط���λ׼ȷ���ٶȿ죩  
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);  
        // ͨ��WLAN���ƶ�����(3G/2G)ȷ����λ�ã�Ҳ����AGPS������GPS��λ����Ҫ���������ڻ��ڸ������Ⱥ��ï�ܵ����ֵȣ��ܼ��ĵط���λ��  
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);  
        if (gps || network) {  
            return true;  
        }  
  
        return false;  
    }  
    
    /** 
     * ǿ�ư��û���GPS 
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
