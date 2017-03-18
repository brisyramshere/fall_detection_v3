package com.fallDetect.cos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.fallDetect.cos.Service.CellInfo;
import com.fallDetect.cos.Service.Gps;
import com.fallDetect.cos.Service.UtilTool;




import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SmsSend extends Activity{
	
	
	private EditText smsET,contactsET;
	private Button cancelBT;
	private Timer timer;
	private int time_remain=30;
	private String smsContents,smsContentsAndGPS;
	private String phoneNum,phoneName;
	PendingIntent paIntent;
    SmsManager smsManager;
    
  //获取全局变量
  	OverallValue ov;
    //GPS和基站信息相关
    ArrayList<CellInfo> cellIds = null;
    private Gps gps=null;
    private LocationManager locationManager;
    private final static String TAG="Service";
	//铃声、状态栏相关变量
	private MediaPlayer mMediaPlayer = null;  
	Context mContext;
	
    private final static String SEND_ACTION      = "send";
    private final static String DELIVERED_ACTION = "delivered";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms_send);
		
		ov=(OverallValue)getApplicationContext();
		time_remain=ov.getWaitTime();
		smsET=(EditText)findViewById(R.id.sms_to_send);
		contactsET=(EditText)findViewById(R.id.contacts_to_recieve);
		cancelBT=(Button)findViewById(R.id.cancelSend);
		
		//获取联系人和手机号
		smsContents=smsRead();
		phoneNum=NumRead();
		phoneName=nameRead();
		
		//在文本框中显示短信内容和GPS信息（经纬度）
		String smsString="";
		if(ov.getGPSFlag())
		{
		//gps=new Gps(SmsSend.this);
		//cellIds=UtilTool.init(SmsSend.this);
		String latString="",lonsString="";
		// 获取系统LocationManager服务
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// 从GPS获取最近的定位信息
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		// 设置每2秒获取一次GPS的定位信息

	
		/*
		if(gps!=null){ //当结束服务时gps为空
	         //获取经纬度
	    	 Location location;
	         location=gps.getLocation();
	         //如果gps无法获取经纬度，改用基站定位获取
	         if(location==null){
	          Log.v(TAG, "gps location null"); 
	          smsString="（手机未打开GPS，无法获取位置信息。）";
	       }
	         else {
				latString=String.valueOf(location.getLatitude());
				lonsString=String.valueOf(location.getLongitude());
				smsString="我现在在：纬度："+latString+"，经度："+lonsString;
			}
	     }
	     Log.d("Service", "!!!!!!!!!!!!GPS位置："+latString+"  "+lonsString);
		}
*/
	        if(location==null){
		          Log.i(TAG, "gps location null"); 
		          smsString="（GPS位置获取未成功。）";
		       }
		         else {
					latString=String.valueOf(location.getLatitude());
					lonsString=String.valueOf(location.getLongitude());
					smsString="我现在在：纬度："+latString+"，经度："+lonsString;
			}
		}
		//显示短信内容
		smsContentsAndGPS=smsContents+smsString;
		smsET.setText(smsContentsAndGPS);
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				2000, 8, new LocationListener() {

					public void onLocationChanged(Location location) {
						// 当GPS定位信息发生改变时，更新位置
						smsContentsAndGPS=smsContents+"我现在在：纬度："+String.valueOf(location.getLatitude())+"，经度："+String.valueOf(location.getLongitude());
						smsET.setText(smsContentsAndGPS);
					}

					public void onProviderDisabled(String provider) {
					}

					public void onProviderEnabled(String provider) {
						// 当GPS LocationProvider可用时，更新位置
						smsContentsAndGPS=smsContents+"我现在在：纬度："+String.valueOf(locationManager.getLastKnownLocation(provider).getLatitude())+"，经度："+String.valueOf(locationManager.getLastKnownLocation(provider).getLongitude());
						smsET.setText(smsContents+"我现在在：纬度："+String.valueOf(locationManager.getLastKnownLocation(provider).getLatitude())+"，经度："+String.valueOf(locationManager.getLastKnownLocation(provider).getLongitude()));
					}

					public void onStatusChanged(String provider, int status,
							Bundle extras) {
					}
				});
		//在文本框中和紧急联系人信息
		contactsET.setText(phoneName+":"+phoneNum);
		//判断联系人和短信都设置好了，才进入短信发送倒计时
		if (" ".equals(phoneNum)) {  
            Toast.makeText(SmsSend.this,  
                    "请先设置紧急联系人", Toast.LENGTH_SHORT)  
                    .show();  
        }else {
        	//打开发送倒计时定时器
    		if(timer==null)
    		{
    		timer=new Timer();
    		timer.schedule(new timerTask(), 0, 1000);
    		}
		}

		//取消自动发送按钮
		cancelBT.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (timer != null)
		        {   
		            timer.cancel();   
		            timer = null;   
		        }  
				cancelBT.setText("短信未发送");
			}
		});
		
		//铃声播放器相关初始化和播放铃声
        mContext = getApplicationContext();  
    	AudioManager audioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);  
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL); 
        if(ov.getVoiceFlag())
        {
        PlaySound(mContext);
        }
        
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (timer != null)
        {   
            timer.cancel();   
            timer = null;   
        }  
		//3.释放媒体播放器资源
		if(mMediaPlayer != null){  
            mMediaPlayer.stop();  
            // 要释放资源，不然会打开很多个MediaPlayer  
            mMediaPlayer.release();  
        }  
	}



	//从SD卡中读取短信内容
	private String smsRead() {
		// TODO Auto-generated method stub
    	String smsString=" ";
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
                	smsString=lineTxt;
                }                
                read.close();
		    }			
		} catch (Exception e) 
		{
			e.printStackTrace();
        }
		return smsString;
	}
	
	//从SD卡中读取电话号码，
    private String NumRead() {
		// TODO Auto-generated method stub
    	String numString=" ";
    	try {
            //String encoding="GBK";
			String path=Environment.getExternalStorageDirectory().getPath();
			String smsDir=path+"/Fall_Detection/contacts_phone.txt";
			File file_sms=new File(smsDir);
			
			if(file_sms.isFile() && file_sms.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                new FileInputStream(file_sms));//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                	numString=lineTxt;
                	break;
                }                
                read.close();
		    }			
		} catch (Exception e) 
		{
			e.printStackTrace();
        }
		return numString;
	}
    
    //从SD卡中读取紧急联系人姓名
	private String nameRead() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
    	String nameString=" ";
    	try {
            //String encoding="GBK";
			String path=Environment.getExternalStorageDirectory().getPath();
			String smsDir=path+"/Fall_Detection/contacts_name.txt";
			File file_sms=new File(smsDir);
			
			if(file_sms.isFile() && file_sms.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                new FileInputStream(file_sms));//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                	nameString=lineTxt;
                	break;
                }                
                read.close();
		    }			
		} catch (Exception e) 
		{
			e.printStackTrace();
        }
		return nameString;

	}
    
	private class timerTask extends TimerTask
    {
    	@Override
		public void run() {
    		
    		if(time_remain<=0)
    		{
                mHandler.sendEmptyMessage(1);
    		}
    		else {
    			time_remain--;
    			mHandler.sendEmptyMessage(2);
			}
    		
		}
    	
    }
    //各个线程的消息处理
    private final Handler mHandler = new Handler() {
        @Override
        	public void handleMessage(Message msg) {
            	switch (msg.what) {
            	case 1:
            		//关闭定时器
            		if (timer != null)
    		        {   
    		            timer.cancel();   
    		            timer = null;   
    		        } 
            		//发送短信
            		sendSMS(phoneNum, smsContentsAndGPS); 
            		cancelBT.setText("短信已发送");
            		break;
            	case 2:
            		String string="取消（"+String.valueOf(time_remain)+"s）";
        			cancelBT.setText(string);
        			break;
            }
        }
    };
    
    private void sendSMS(String phoneNum, String message) {  
        //初始化发短信SmsManager类  
        SmsManager smsManager = SmsManager.getDefault();  
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(SEND_ACTION), 0);  
        
        //发送状态返回处理
        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "Send Success!", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Send Failed because generic failure cause.",
                                       Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "Send Failed because service is currently unavailable.",
                                       Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Send Failed because no pdu provided.", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Send Failed because radio was explicitly turned off.",
                                       Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getBaseContext(), "Send Failed.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SEND_ACTION));
        
        //如果短信内容长度超过70则分为若干条发  
        if (message.length() > 70) {  
            ArrayList<String> msgs = smsManager.divideMessage(message);  
            for (String msg : msgs) {  
                smsManager.sendTextMessage(phoneNum, null, msg, pi, null);  
            }  
        } else {  
            smsManager.sendTextMessage(phoneNum, null, message, pi, null);  
        }  
        
        
        Toast.makeText(this, "短信发送成功", Toast.LENGTH_SHORT)  
                .show();  
    }  
    
  //响铃程序
    public  void PlaySound(final Context context) {  
          
        Log.e("ee", "正在响铃");  
        // 使用来电铃声的铃声路径  
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);   
        // 如果为空，才构造，不为空，说明之前有构造过  
        if(mMediaPlayer == null)  
            mMediaPlayer = new MediaPlayer();
        	//mMediaPlayer = MediaPlayer.create(this, uri);
        try {  
            mMediaPlayer.setDataSource(context, uri);  
            mMediaPlayer.setLooping(true); //循环播放  
            mMediaPlayer.prepare();  
            mMediaPlayer.start();  
        } catch (IllegalArgumentException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (SecurityException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (IllegalStateException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
          
    }  
}
    


