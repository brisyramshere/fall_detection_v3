package com.fallDetect.cos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class SmsSetting extends Activity{
	
	private Button smsSaveBT;
	private EditText smsContentET;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms);
		smsSaveBT=(Button)findViewById(R.id.saveSms);
		smsContentET=(EditText)findViewById(R.id.smsContent);
		
		showsms();
		smsSaveBT.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String smsContent=smsContentET.getText().toString();
				smsSave(smsContent);
				Toast.makeText(getApplicationContext(), "短信内容更新成功！",
						Toast.LENGTH_SHORT).show();
			}
		});
		
		
		
	}
	
	private void showsms() {
		// TODO Auto-generated method stub
		try {
            //String encoding="GBK";
			String path=Environment.getExternalStorageDirectory().getPath();
			String smsDir=path+"/Fall_Detection/sms.txt";
			File file_sms=new File(smsDir);
			
			if(file_sms.isFile() && file_sms.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                new FileInputStream(file_sms));//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                	smsContentET.setText(lineTxt);
                }                
                read.close();
		    }			
		} catch (Exception e) 
		{
			e.printStackTrace();
        }
	}
	
	public void smsSave(String sms)
	{
		String path=Environment.getExternalStorageDirectory().getPath();
	    String sDir=path+"/Fall_Detection/";
	    String smsDir=sDir+"sms.txt";
	  	try{
		File destDir = new File(sDir);
		if (!destDir.exists()) {
		   destDir.mkdirs();
		}else {
			File file=new File(smsDir);
			file.delete();
		}
		
		File file=new File(smsDir);
		FileOutputStream fos=new FileOutputStream(file,true);
		fos.write(sms.getBytes());
		fos.close();
	    }catch (Exception e) {
	      	// TODO: handle exception
	      		  e.printStackTrace();
	    }  
	}
}
