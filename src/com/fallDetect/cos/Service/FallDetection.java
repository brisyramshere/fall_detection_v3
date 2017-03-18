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
	
	//多线程消息传递的消息标志
	public static final int FALL_DETECTED=3;
	//public static final int DRAW_DATA=4;
	
	OverallValue ov;
	
	//*********声明变量******************//
	//传感器相关变量
	private  SensorManager sm;
	//数据处理相关变量
	//private ProcessThread mProcessThread;
	private double acceleration[],acceleration_process[];
	float X_lateral;
	float Y_longitudinal;
	float Z_vertical;
	double XYZ;
	int index;
	private int Treshold;
	//点亮屏幕相关设置
	PowerManager pm;
	WakeLock mWakelock;
	WakeLock mWakelock1;
	//解锁屏幕
    KeyguardManager km;
    KeyguardLock kl;
	// 状态栏提示要用的  
    private NotificationManager mNotificationManager;  
    private PendingIntent m_PendingIntent;  
    private Notification notification; 

    //GPS
    LocationManager locationManager;
    
    double[] a_now=new double[150];
    int numoverplus;
    
    

	

	//采样定时器变量
	private Timer timer=null;
	//铃声、状态栏相关变量
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
		
		Log.i("Service", "启动服务");
		sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        int sensor1 = Sensor.TYPE_ACCELEROMETER; //三轴加速度传感器
        //int sensor2 = Sensor.TYPE_GYROSCOPE;   //陀螺仪
        acceleration=new double[100];
        acceleration_process=new double[100];
        index=0;
       
        ov=(OverallValue)getApplicationContext();
        ov.setRunningFlag(1);
        //数据处理相关变量初始化
        numoverplus=5;
        a_now[0]=9.8;
        a_now[1]=9.8;
        a_now[2]=9.8;
        a_now[3]=9.8;
        a_now[4]=9.8;
        Treshold=ov.getThreshold();
        Log.i("Service", "!!!!!阈值="+Treshold);
    	flag_ready=false;
        
        //*******初始化Service的时候********
        //1.注册三轴加速度传感器的监听器
        sm.registerListener(myAccelerometerListener,sm.getDefaultSensor(sensor1),SensorManager.SENSOR_DELAY_GAME);
        //sm.registerListener(myAccelerometerListener,sm.getDefaultSensor(sensor2),SensorManager.SENSOR_DELAY_NORMAL);
        //2.打开采样定时器
        if(timer==null)
		{
		index=0;
		timer=new Timer();
		timer.schedule(new timepass(), 20, 20);
		}
        /*3.打开数据处理线程
        if (mProcessThread == null) {
        	mProcessThread = new ProcessThread();
        	mProcessThread.start();
        	Log.d("Service", "打开数据处理线程！！！！");
        }*/
        //***********************************
      //消息通知栏
        
        
        //定义NotificationManager
        String ns = Context.NOTIFICATION_SERVICE;
        mNotificationManager = (NotificationManager) getSystemService(ns);
        //定义通知栏展现的内容信息
        int icon = R.drawable.icon;
        CharSequence tickerText = "跌倒检测系统";
        long when = System.currentTimeMillis();
        notification = new Notification(icon, tickerText, when);
         
        //定义下拉通知栏时要展现的内容信息
        mContext = getApplicationContext();
        CharSequence contentTitle = "跌倒监测";
        CharSequence contentText = "正在智能监控中......";
        Intent notificationIntent = new Intent(this,StartActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        notification.setLatestEventInfo(mContext, contentTitle, contentText,
                contentIntent);
         
        //用mNotificationManager的notify方法通知用户生成标题栏消息通知
        mNotificationManager.notify(1, notification);
        
      //点亮手机屏幕
        pm = (PowerManager)getSystemService(POWER_SERVICE);
       //mWakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.SCREEN_DIM_WAKE_LOCK,"SimpleTimer");
        mWakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, FallDetection.class.getName());
        mWakelock.acquire();//点亮
        //键盘锁管理器对象  
        km= (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);  
         //这里参数”unLock”作为调试时LogCat中的Tag
        kl = km.newKeyguardLock("unLock");  
        
        /*
        //GPS服务
		if(ov.getGPSFlag())
		{
		//gps=new Gps(SmsSend.this);
		//cellIds=UtilTool.init(SmsSend.this);
		// 获取系统LocationManager服务
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// 从GPS获取最近的定位信息
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		ov.saveGPS(location.getLatitude(),location.getLongitude());
		// 设置每2秒获取一次GPS的定位信息
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				2000, 8, new LocationListener() {

					public void onLocationChanged(Location location) {
						// 当GPS定位信息发生改变时，更新位置
						ov.saveGPS(location.getLatitude(),location.getLongitude());
					}

					public void onProviderDisabled(String provider) {
					}

					public void onProviderEnabled(String provider) {
						// 当GPS LocationProvider可用时，更新位置
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
        m_Notification.tickerText = "随时情感助手在呼唤你……";  
        m_Notification.setLatestEventInfo(mContext, "随时情感助手", intent.getExtras().getString("remind_kind", ""), m_PendingIntent);  
        m_Manager.notify(1023, m_Notification);  
        
 */       
      //铃声播放器相关初始化
       // mContext = getApplicationContext();  
    //	AudioManager audioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);  
      //  audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);  
	}
	
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("Service", "停止服务");
		//Service销毁的时候：
		//1.停止定时器
		stopTimer();
		//2.取消三轴加速度的监听注册
		sm.unregisterListener(myAccelerometerListener);
		//3.释放媒体播放器资源
		if(mMediaPlayer != null){  
            mMediaPlayer.stop();  
            // 要释放资源，不然会打开很多个MediaPlayer  
            mMediaPlayer.release();  
        }  
		ov.setRunningFlag(0);
		mNotificationManager.cancel(1);
		//释放禁止锁屏
		if (mWakelock != null) { mWakelock.release(); mWakelock = null; }
		//if (mWakelock1 != null) { mWakelock1.release(); mWakelock1 = null; }
		kl = km.newKeyguardLock("Lock");  
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//传感器监听函数，传感器值发生变化就进入这个程序
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
	    
	   
	   /*消息处理进程-有问题。。。
	   private class ProcessThread extends Thread {

		   
	        public void run() {
	        	
	        	while(true)
	        	{
	        		Log.i("Service", "数据处理"+flag_ready);
	            while(flag_ready)
	            {
	            	Log.i("Service", "数据处理");
	            	if(DetectionAlgorithm(acceleration_process)){
            			Log.d("Service", "检测到跌倒");
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
	            		//数据处理
	            		Log.i("Service", "数据处理");
	            		if(DetectionAlgorithm(acceleration_process)){
	            			Log.i("Service", "检测到跌倒");
	            			//PlaySound(mContext);
	            			mWakelock.acquire();//点亮
	            			kl.disableKeyguard();  //解锁
	            			Intent smsIntent = new Intent(FallDetection.this,SmsSend.class);
	            			smsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
	        				getApplication().startActivity(smsIntent);
	            		}
	            		
	            		
	            		break;
	            }
	        }
	    };

	    //定时器停止程序
	    private void stopTimer()
	    {       
	        if (timer != null)
	        {   
	            timer.cancel();   
	            timer = null;   
	        }   
	        index=0;
	    }   
	    
	    //定时器内部的运行程序
	    private class timepass extends TimerTask
	    {
	    	@Override
			public void run() {
	    		acceleration[index]=XYZ;
    			//发送消息给画图activity     
    			//String obj=String.valueOf(acceleration[index]);
                //mHandler.obtainMessage(Advance.DRAW_DATA, 1, -1, acceleration[index]).sendToTarget();
    			//向Advance类发送数据，画图
	    		Log.d("Value", "加速度值:"+XYZ+" "+X_lateral+" "+Z_vertical);
    			Intent intent = new Intent("drawwave");  
                intent.putExtra("data", XYZ);  
                sendBroadcast(intent);  
                
	    		index++;
	    		if(index>=100){
	    			index=0;
	    			for(int i=0;i<100;i++)
	    				acceleration_process[i]=acceleration[i];    //将数据转移到另一个数组中进行算法分析
	    			
	    			//flag_ready=true;
	    			mHandler.sendEmptyMessage(FALL_DETECTED);
	    			Log.i("Service", "服务正在运行"+flag_ready);
	    		}
			}
	    }
	    
	    //检测算法
	    private boolean DetectionAlgorithm(double a[])
	    {
	    	Log.i("Service", "数据处理进行中");
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
		//找极小值点
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
	     //在每个极小值点后找极大值点和回到9的点   
	     for(j=0;j<num_min-1;j++)
	        {
	         int Findmax=0;
	         int Find8=0;
	         //找极大值点   
	         double maxa=0;
	         int maxp=0;
	         for(int k=minp[j];k<minp[j+1];k++)
	         {
	              if(maxa<a_now[k]&&a_now[k]-mina[j]>Treshold)  //阈值
	              {
	                    maxa=a_now[k];
	                    maxp=k;
	                    Findmax=1;
	                }
	           }
	           //找极大值后回到9的点 
	            if(Findmax==1)
	                {
	            	int position8=0;
	             for(int k=maxp+1;k<minp[j+1];k++)
	             {
	                if (a_now[k-1]>9&&a_now[k]<9&&Find8==0)
	                {
	                    Find8=1;
	                    position8=k;
	                    //判断是否跌倒
	                    if(position8-minp[j]<60)
	                    {
	                        int noFall=0;
	                        for(int i=position8;i<num_end+5;i++)
	                        {
	                          if(a_now[k]>Treshold)  //阈值
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
	        //判断是否有位检测完的点
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
	        	  Log.i("Service", "检测成功");
	              return true;
	              }
	          else
	              {
	        	  Log.i("Service", "检测不成功");
	              return false;    
	              }

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
