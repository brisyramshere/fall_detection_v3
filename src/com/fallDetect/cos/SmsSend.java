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
    
  //��ȡȫ�ֱ���
  	OverallValue ov;
    //GPS�ͻ�վ��Ϣ���
    ArrayList<CellInfo> cellIds = null;
    private Gps gps=null;
    private LocationManager locationManager;
    private final static String TAG="Service";
	//������״̬����ر���
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
		
		//��ȡ��ϵ�˺��ֻ���
		smsContents=smsRead();
		phoneNum=NumRead();
		phoneName=nameRead();
		
		//���ı�������ʾ�������ݺ�GPS��Ϣ����γ�ȣ�
		String smsString="";
		if(ov.getGPSFlag())
		{
		//gps=new Gps(SmsSend.this);
		//cellIds=UtilTool.init(SmsSend.this);
		String latString="",lonsString="";
		// ��ȡϵͳLocationManager����
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// ��GPS��ȡ����Ķ�λ��Ϣ
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		// ����ÿ2���ȡһ��GPS�Ķ�λ��Ϣ

	
		/*
		if(gps!=null){ //����������ʱgpsΪ��
	         //��ȡ��γ��
	    	 Location location;
	         location=gps.getLocation();
	         //���gps�޷���ȡ��γ�ȣ����û�վ��λ��ȡ
	         if(location==null){
	          Log.v(TAG, "gps location null"); 
	          smsString="���ֻ�δ��GPS���޷���ȡλ����Ϣ����";
	       }
	         else {
				latString=String.valueOf(location.getLatitude());
				lonsString=String.valueOf(location.getLongitude());
				smsString="�������ڣ�γ�ȣ�"+latString+"�����ȣ�"+lonsString;
			}
	     }
	     Log.d("Service", "!!!!!!!!!!!!GPSλ�ã�"+latString+"  "+lonsString);
		}
*/
	        if(location==null){
		          Log.i(TAG, "gps location null"); 
		          smsString="��GPSλ�û�ȡδ�ɹ�����";
		       }
		         else {
					latString=String.valueOf(location.getLatitude());
					lonsString=String.valueOf(location.getLongitude());
					smsString="�������ڣ�γ�ȣ�"+latString+"�����ȣ�"+lonsString;
			}
		}
		//��ʾ��������
		smsContentsAndGPS=smsContents+smsString;
		smsET.setText(smsContentsAndGPS);
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				2000, 8, new LocationListener() {

					public void onLocationChanged(Location location) {
						// ��GPS��λ��Ϣ�����ı�ʱ������λ��
						smsContentsAndGPS=smsContents+"�������ڣ�γ�ȣ�"+String.valueOf(location.getLatitude())+"�����ȣ�"+String.valueOf(location.getLongitude());
						smsET.setText(smsContentsAndGPS);
					}

					public void onProviderDisabled(String provider) {
					}

					public void onProviderEnabled(String provider) {
						// ��GPS LocationProvider����ʱ������λ��
						smsContentsAndGPS=smsContents+"�������ڣ�γ�ȣ�"+String.valueOf(locationManager.getLastKnownLocation(provider).getLatitude())+"�����ȣ�"+String.valueOf(locationManager.getLastKnownLocation(provider).getLongitude());
						smsET.setText(smsContents+"�������ڣ�γ�ȣ�"+String.valueOf(locationManager.getLastKnownLocation(provider).getLatitude())+"�����ȣ�"+String.valueOf(locationManager.getLastKnownLocation(provider).getLongitude()));
					}

					public void onStatusChanged(String provider, int status,
							Bundle extras) {
					}
				});
		//���ı����кͽ�����ϵ����Ϣ
		contactsET.setText(phoneName+":"+phoneNum);
		//�ж���ϵ�˺Ͷ��Ŷ����ú��ˣ��Ž�����ŷ��͵���ʱ
		if (" ".equals(phoneNum)) {  
            Toast.makeText(SmsSend.this,  
                    "�������ý�����ϵ��", Toast.LENGTH_SHORT)  
                    .show();  
        }else {
        	//�򿪷��͵���ʱ��ʱ��
    		if(timer==null)
    		{
    		timer=new Timer();
    		timer.schedule(new timerTask(), 0, 1000);
    		}
		}

		//ȡ���Զ����Ͱ�ť
		cancelBT.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (timer != null)
		        {   
		            timer.cancel();   
		            timer = null;   
		        }  
				cancelBT.setText("����δ����");
			}
		});
		
		//������������س�ʼ���Ͳ�������
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
		//3.�ͷ�ý�岥������Դ
		if(mMediaPlayer != null){  
            mMediaPlayer.stop();  
            // Ҫ�ͷ���Դ����Ȼ��򿪺ܶ��MediaPlayer  
            mMediaPlayer.release();  
        }  
	}



	//��SD���ж�ȡ��������
	private String smsRead() {
		// TODO Auto-generated method stub
    	String smsString=" ";
    	try {
            //String encoding="GBK";
			String path=Environment.getExternalStorageDirectory().getPath();
			String smsDir=path+"/Fall_Detection/sms.txt";
			File file_sms=new File(smsDir);
			
			if(file_sms.isFile() && file_sms.exists()){ //�ж��ļ��Ƿ����
                InputStreamReader read = new InputStreamReader(
                new FileInputStream(file_sms));//���ǵ������ʽ
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
	
	//��SD���ж�ȡ�绰���룬
    private String NumRead() {
		// TODO Auto-generated method stub
    	String numString=" ";
    	try {
            //String encoding="GBK";
			String path=Environment.getExternalStorageDirectory().getPath();
			String smsDir=path+"/Fall_Detection/contacts_phone.txt";
			File file_sms=new File(smsDir);
			
			if(file_sms.isFile() && file_sms.exists()){ //�ж��ļ��Ƿ����
                InputStreamReader read = new InputStreamReader(
                new FileInputStream(file_sms));//���ǵ������ʽ
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
    
    //��SD���ж�ȡ������ϵ������
	private String nameRead() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
    	String nameString=" ";
    	try {
            //String encoding="GBK";
			String path=Environment.getExternalStorageDirectory().getPath();
			String smsDir=path+"/Fall_Detection/contacts_name.txt";
			File file_sms=new File(smsDir);
			
			if(file_sms.isFile() && file_sms.exists()){ //�ж��ļ��Ƿ����
                InputStreamReader read = new InputStreamReader(
                new FileInputStream(file_sms));//���ǵ������ʽ
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
    //�����̵߳���Ϣ����
    private final Handler mHandler = new Handler() {
        @Override
        	public void handleMessage(Message msg) {
            	switch (msg.what) {
            	case 1:
            		//�رն�ʱ��
            		if (timer != null)
    		        {   
    		            timer.cancel();   
    		            timer = null;   
    		        } 
            		//���Ͷ���
            		sendSMS(phoneNum, smsContentsAndGPS); 
            		cancelBT.setText("�����ѷ���");
            		break;
            	case 2:
            		String string="ȡ����"+String.valueOf(time_remain)+"s��";
        			cancelBT.setText(string);
        			break;
            }
        }
    };
    
    private void sendSMS(String phoneNum, String message) {  
        //��ʼ��������SmsManager��  
        SmsManager smsManager = SmsManager.getDefault();  
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(SEND_ACTION), 0);  
        
        //����״̬���ش���
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
        
        //����������ݳ��ȳ���70���Ϊ��������  
        if (message.length() > 70) {  
            ArrayList<String> msgs = smsManager.divideMessage(message);  
            for (String msg : msgs) {  
                smsManager.sendTextMessage(phoneNum, null, msg, pi, null);  
            }  
        } else {  
            smsManager.sendTextMessage(phoneNum, null, message, pi, null);  
        }  
        
        
        Toast.makeText(this, "���ŷ��ͳɹ�", Toast.LENGTH_SHORT)  
                .show();  
    }  
    
  //�������
    public  void PlaySound(final Context context) {  
          
        Log.e("ee", "��������");  
        // ʹ����������������·��  
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);   
        // ���Ϊ�գ��Ź��죬��Ϊ�գ�˵��֮ǰ�й����  
        if(mMediaPlayer == null)  
            mMediaPlayer = new MediaPlayer();
        	//mMediaPlayer = MediaPlayer.create(this, uri);
        try {  
            mMediaPlayer.setDataSource(context, uri);  
            mMediaPlayer.setLooping(true); //ѭ������  
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
    


