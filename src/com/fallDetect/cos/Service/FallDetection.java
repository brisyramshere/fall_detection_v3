package com.fallDetect.cos.Service;


import java.io.IOException;
import java.lang.annotation.Target;
import java.util.Timer;
import java.util.TimerTask;


import com.fallDetect.cos.Advance;
import com.fallDetect.cos.OverallValue;
import com.fallDetect.cos.SmsSend;
import com.fallDetect.cos.R;
import com.fallDetect.cos.StartActivity;


import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class FallDetection extends Service{
	
	//���߳���Ϣ���ݵ���Ϣ��־
	public static final int FALL_DETECTED=3;
	//public static final int DRAW_DATA=4;
	
	OverallValue ov;
	
	//*********��������******************//
	//��������ر���
	private  SensorManager sm;
	//���ݴ�����ر���
	//private ProcessThread mProcessThread;
	private double acceleration[],acceleration_process[];
	float X_lateral;
	float Y_longitudinal;
	float Z_vertical;
	double XYZ;
	int index;
	private int Treshold;
	//������Ļ�������
	PowerManager pm;
	WakeLock mWakelock;
	WakeLock mWakelock1;
	//������Ļ
    KeyguardManager km;
    KeyguardLock kl;
	// ״̬����ʾҪ�õ�  
    private NotificationManager mNotificationManager;  
    private PendingIntent m_PendingIntent;  
    private Notification notification; 

    //GPS
    LocationManager locationManager;
    
    double[] a_now=new double[150];
    int numoverplus;
    
    

	

	//������ʱ������
	private Timer timer=null;
	//������״̬����ر���
	private MediaPlayer mMediaPlayer = null;  
	Context mContext;
	
	private boolean flag_ready;

    /*
    private final Handler mHandler;
    public FallDetection(Context context, Handler handler) {
        mHandler = handler;
    }
	*/
	/*
    public class FallDetectionBinder extends Binder {
    	FallDetection getService() {
            return FallDetection.this;
        }
    }
	*/
	
	@SuppressLint("Wakelock")
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		 
        

	}
	
	@SuppressLint("NewApi")  
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		Log.i("Service", "��������");
		sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        int sensor1 = Sensor.TYPE_ACCELEROMETER; //������ٶȴ�����
        //int sensor2 = Sensor.TYPE_GYROSCOPE;   //������
        acceleration=new double[100];
        acceleration_process=new double[100];
        index=0;
       
        ov=(OverallValue)getApplicationContext();
        ov.setRunningFlag(1);
        //���ݴ�����ر�����ʼ��
        numoverplus=5;
        a_now[0]=9.8;
        a_now[1]=9.8;
        a_now[2]=9.8;
        a_now[3]=9.8;
        a_now[4]=9.8;
        Treshold=ov.getThreshold();
        Log.i("Service", "!!!!!��ֵ="+Treshold);
    	flag_ready=false;
        
        //*******��ʼ��Service��ʱ��********
        //1.ע��������ٶȴ������ļ�����
        sm.registerListener(myAccelerometerListener,sm.getDefaultSensor(sensor1),SensorManager.SENSOR_DELAY_GAME);
        //sm.registerListener(myAccelerometerListener,sm.getDefaultSensor(sensor2),SensorManager.SENSOR_DELAY_NORMAL);
        //2.�򿪲�����ʱ��
        if(timer==null)
		{
		index=0;
		timer=new Timer();
		timer.schedule(new timepass(), 20, 20);
		}
        /*3.�����ݴ����߳�
        if (mProcessThread == null) {
        	mProcessThread = new ProcessThread();
        	mProcessThread.start();
        	Log.d("Service", "�����ݴ����̣߳�������");
        }*/
        //***********************************
      //��Ϣ֪ͨ��
        
        
        //����NotificationManager
        String ns = Context.NOTIFICATION_SERVICE;
        mNotificationManager = (NotificationManager) getSystemService(ns);
        //����֪ͨ��չ�ֵ�������Ϣ
        int icon = R.drawable.icon;
        CharSequence tickerText = "�������ϵͳ";
        long when = System.currentTimeMillis();
        notification = new Notification(icon, tickerText, when);
         
        //��������֪ͨ��ʱҪչ�ֵ�������Ϣ
        mContext = getApplicationContext();
        CharSequence contentTitle = "�������";
        CharSequence contentText = "�������ܼ����......";
        Intent notificationIntent = new Intent(this,StartActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        notification.setLatestEventInfo(mContext, contentTitle, contentText,
                contentIntent);
         
        //��mNotificationManager��notify����֪ͨ�û����ɱ�������Ϣ֪ͨ
        mNotificationManager.notify(1, notification);
        
      //�����ֻ���Ļ
        pm = (PowerManager)getSystemService(POWER_SERVICE);
       //mWakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.SCREEN_DIM_WAKE_LOCK,"SimpleTimer");
        mWakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, FallDetection.class.getName());
        mWakelock.acquire();//����
        //����������������  
        km= (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);  
         //���������unLock����Ϊ����ʱLogCat�е�Tag
        kl = km.newKeyguardLock("unLock");  
        
        /*
        //GPS����
		if(ov.getGPSFlag())
		{
		//gps=new Gps(SmsSend.this);
		//cellIds=UtilTool.init(SmsSend.this);
		// ��ȡϵͳLocationManager����
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// ��GPS��ȡ����Ķ�λ��Ϣ
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		ov.saveGPS(location.getLatitude(),location.getLongitude());
		// ����ÿ2���ȡһ��GPS�Ķ�λ��Ϣ
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				2000, 8, new LocationListener() {

					public void onLocationChanged(Location location) {
						// ��GPS��λ��Ϣ�����ı�ʱ������λ��
						ov.saveGPS(location.getLatitude(),location.getLongitude());
					}

					public void onProviderDisabled(String provider) {
					}

					public void onProviderEnabled(String provider) {
						// ��GPS LocationProvider����ʱ������λ��
						ov.saveGPS(locationManager.getLastKnownLocation(provider).getLatitude(),locationManager.getLastKnownLocation(provider).getLongitude());
					}

					public void onStatusChanged(String provider, int status,
							Bundle extras) {
					}
				});
		}
		*/
		return START_STICKY;
		
     //   if(target == null)  
      //      target = new Target(mContext);   
/*		int i;
		Context mContext=getApplicationContext();  
        //Intent intent=new Intent(this,Service.class);
        m_Manager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        m_Manager.cancel(1023);  
        m_PendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,SmsSend.class), 0);  
        m_Notification = new Notification();  
        m_Notification.icon = R.drawable.icon;  
        m_Notification.tickerText = "��ʱ��������ں����㡭��";  
        m_Notification.setLatestEventInfo(mContext, "��ʱ�������", intent.getExtras().getString("remind_kind", ""), m_PendingIntent);  
        m_Manager.notify(1023, m_Notification);  
        
 */       
      //������������س�ʼ��
       // mContext = getApplicationContext();  
    //	AudioManager audioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);  
      //  audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);  
	}
	
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("Service", "ֹͣ����");
		//Service���ٵ�ʱ��
		//1.ֹͣ��ʱ��
		stopTimer();
		//2.ȡ��������ٶȵļ���ע��
		sm.unregisterListener(myAccelerometerListener);
		//3.�ͷ�ý�岥������Դ
		if(mMediaPlayer != null){  
            mMediaPlayer.stop();  
            // Ҫ�ͷ���Դ����Ȼ��򿪺ܶ��MediaPlayer  
            mMediaPlayer.release();  
        }  
		ov.setRunningFlag(0);
		mNotificationManager.cancel(1);
		//�ͷŽ�ֹ����
		if (mWakelock != null) { mWakelock.release(); mWakelock = null; }
		//if (mWakelock1 != null) { mWakelock1.release(); mWakelock1 = null; }
		kl = km.newKeyguardLock("Lock");  
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//����������������������ֵ�����仯�ͽ����������
	final SensorEventListener myAccelerometerListener = new SensorEventListener(){
	    	
	    	public void onSensorChanged(SensorEvent sensorEvent){
	    		if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
	    		{
	    			X_lateral = sensorEvent.values[0];
	    			Y_longitudinal = sensorEvent.values[1];
	    			Z_vertical = sensorEvent.values[2];
	    			XYZ=Math.sqrt(Math.pow(X_lateral,2)+Math.pow(Y_longitudinal, 2)+Math.pow(Z_vertical, 2));
	    			
	    		}
	    	}
	    	
	    	public void onAccuracyChanged(Sensor sensor , int accuracy){
	    		//ACT.setText("onAccuracyChanged");
	    	}
	   };
	    
	   
	   /*��Ϣ�������-�����⡣����
	   private class ProcessThread extends Thread {

		   
	        public void run() {
	        	
	        	while(true)
	        	{
	        		Log.i("Service", "���ݴ���"+flag_ready);
	            while(flag_ready)
	            {
	            	Log.i("Service", "���ݴ���");
	            	if(DetectionAlgorithm(acceleration_process)){
            			Log.d("Service", "��⵽����");
            			PlaySound(mContext);
            			Intent smsIntent = new Intent(FallDetection.this,SmsSend.class);
            			smsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        				getApplication().startActivity(smsIntent);
        				flag_ready=false;
            		}
	            }
	        	}
			}
	            
	    }
	    */
	    private final Handler mHandler = new Handler() {
	        @Override
	        	public void handleMessage(Message msg) {
	            	switch (msg.what) {
	            	case FALL_DETECTED:
	            		//���ݴ���
	            		Log.i("Service", "���ݴ���");
	            		if(DetectionAlgorithm(acceleration_process)){
	            			Log.i("Service", "��⵽����");
	            			//PlaySound(mContext);
	            			mWakelock.acquire();//����
	            			kl.disableKeyguard();  //����
	            			Intent smsIntent = new Intent(FallDetection.this,SmsSend.class);
	            			smsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
	        				getApplication().startActivity(smsIntent);
	            		}
	            		
	            		
	            		break;
	            }
	        }
	    };

	    //��ʱ��ֹͣ����
	    private void stopTimer()
	    {       
	        if (timer != null)
	        {   
	            timer.cancel();   
	            timer = null;   
	        }   
	        index=0;
	    }   
	    
	    //��ʱ���ڲ������г���
	    private class timepass extends TimerTask
	    {
	    	@Override
			public void run() {
	    		acceleration[index]=XYZ;
    			//������Ϣ����ͼactivity     
    			//String obj=String.valueOf(acceleration[index]);
                //mHandler.obtainMessage(Advance.DRAW_DATA, 1, -1, acceleration[index]).sendToTarget();
    			//��Advance�෢�����ݣ���ͼ
	    		Log.d("Value", "���ٶ�ֵ:"+XYZ+" "+X_lateral+" "+Z_vertical);
    			Intent intent = new Intent("drawwave");  
                intent.putExtra("data", XYZ);  
                sendBroadcast(intent);  
                
	    		index++;
	    		if(index>=100){
	    			index=0;
	    			for(int i=0;i<100;i++)
	    				acceleration_process[i]=acceleration[i];    //������ת�Ƶ���һ�������н����㷨����
	    			
	    			//flag_ready=true;
	    			mHandler.sendEmptyMessage(FALL_DETECTED);
	    			Log.i("Service", "������������"+flag_ready);
	    		}
			}
	    }
	    
	    //����㷨
	    private boolean DetectionAlgorithm(double a[])
	    {
	    	Log.i("Service", "���ݴ��������");
	    	for(int i=numoverplus;i<numoverplus+100;i++)
	        {
	           a_now[i]=a[i-numoverplus];
	         }
	     
	    int num_end=numoverplus+95;
	    int num_min=0;
	    int j=5;
	    double[] mina=new double[10];
	    int[] minp=new int[10];
	    int Fall=0;
		//�Ҽ�Сֵ��
	    while(j<num_end)
	       {
	        if(a_now[j]>1&&a_now[j]<8&&a_now[j]<=a_now[j-1]&&a_now[j]<a_now[j-2]&&a_now[j]<a_now[j-3]&&a_now[j]<a_now[j-4]&&a_now[j]<a_now[j-5]&&a_now[j]<=a_now[j+1]&&a_now[j]<a_now[j+2]&&a_now[j]<a_now[j+3]&&a_now[j]<a_now[j+4]&&a_now[j]<a_now[j+5])
	           {
	                 mina[num_min]=a_now[j];
	                 minp[num_min]=j;
	                 j=j+5;
	                 num_min++;
	            }
	            j=j+1;
	       }
	     //��ÿ����Сֵ����Ҽ���ֵ��ͻص�9�ĵ�   
	     for(j=0;j<num_min-1;j++)
	        {
	         int Findmax=0;
	         int Find8=0;
	         //�Ҽ���ֵ��   
	         double maxa=0;
	         int maxp=0;
	         for(int k=minp[j];k<minp[j+1];k++)
	         {
	              if(maxa<a_now[k]&&a_now[k]-mina[j]>Treshold)  //��ֵ
	              {
	                    maxa=a_now[k];
	                    maxp=k;
	                    Findmax=1;
	                }
	           }
	           //�Ҽ���ֵ��ص�9�ĵ� 
	            if(Findmax==1)
	                {
	            	int position8=0;
	             for(int k=maxp+1;k<minp[j+1];k++)
	             {
	                if (a_now[k-1]>9&&a_now[k]<9&&Find8==0)
	                {
	                    Find8=1;
	                    position8=k;
	                    //�ж��Ƿ����
	                    if(position8-minp[j]<60)
	                    {
	                        int noFall=0;
	                        for(int i=position8;i<num_end+5;i++)
	                        {
	                          if(a_now[k]>Treshold)  //��ֵ
	                          {
	                            noFall=1;
	                           }
	                        }
	                        if(noFall==0)
	                            {
	                             num_min=0;
	                             Fall=1;
	                            
	                            }
	                     }
	                }
	             }
	            }
	        }
	        //�ж��Ƿ���λ�����ĵ�
	        numoverplus=5;
	        for(int i=0;i<5;i++)
	            {
	            a_now[i]=a[95+i];
	            }
	        
	        if(num_min>0&&num_end+5-minp[num_min]<50)
	            {
	            numoverplus=num_end+5-minp[num_min]+5;
	            for(int i=0;i<numoverplus;i++)
	            {
	            a_now[i]=a_now[99-numoverplus];
	            }
	          }
	          
	          
	          if(Fall==1)
	              {
	        	  Log.i("Service", "���ɹ�");
	              return true;
	              }
	          else
	              {
	        	  Log.i("Service", "��ⲻ�ɹ�");
	              return false;    
	              }

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
