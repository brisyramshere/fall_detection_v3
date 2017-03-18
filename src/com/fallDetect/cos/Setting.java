package com.fallDetect.cos;

import com.fallDetect.cos.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class Setting extends Activity{
	
	private ImageButton contact_settingBT,sms_settingBT,aboutBT,otherSettingBT;
	
	@Override
	 protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		contact_settingBT=(ImageButton)findViewById(R.id.contactSetting);
		sms_settingBT=(ImageButton)findViewById(R.id.smsSetting);
		otherSettingBT=(ImageButton)findViewById(R.id.otherSetting);
		aboutBT=(ImageButton)findViewById(R.id.about);
		
		//紧急联系人设置界面
		contact_settingBT.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent contacts_settingIntent = new Intent(Setting.this,contactsSetting.class);
				startActivity(contacts_settingIntent);
			}
		});
		//短信设置界面
		sms_settingBT.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent sms_settingIntent = new Intent(Setting.this,SmsSetting.class);
				startActivity(sms_settingIntent);
			}
		});
		//其他设置：
		otherSettingBT.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent otherSettingIntent = new Intent(Setting.this,Other_setting.class);
				startActivity(otherSettingIntent);
			}
		});
		//关于界面
		aboutBT.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent aboutIntent = new Intent(Setting.this,About.class);
				startActivity(aboutIntent);
			}
		});
		
	}

}
