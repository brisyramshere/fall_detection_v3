package com.fallDetect.cos;



import android.app.Application;


//OverallValue是一个全局类，继承application，应用程序运行时就存在，用于存放全局变量
//继承了application，所以这个application的name就是这个类的名字：com.fallDetect.cos.OverallVlaue，需要在manifest文件里面声明
public class OverallValue extends Application{
	
	
	//阈值设定
	private int Threshold=20;
	public void setThreshold(int flag)
	{
		Threshold=flag;
	}
	public int getThreshold()
	{
		return Threshold;
	}
	//短信等待时长设定
	private int WaitTime=30;
	public void setWaitTime(int flag)
	{
		WaitTime=flag;
	}
	public int getWaitTime()
	{
		return WaitTime;
	}
	//运行状态标志
	private int RunningFlag=0;
	public void setRunningFlag(int flag)
	{
		RunningFlag=flag;
	}
	public int getRunningFlag()
	{
		return RunningFlag;
	}
	//音量控制标志
	private boolean VoiceFlag=true;
	public void setVoiceFlag(boolean flag)
	{
		VoiceFlag=flag;
	}
	public boolean getVoiceFlag()
	{
		return VoiceFlag;
	}
	//位置信息控制标志
	private boolean GPSFlag=true;
	public void setGPSFlag(boolean flag)
	{
		GPSFlag=flag;
	}
	public boolean getGPSFlag()
	{
		return GPSFlag;
	}
	
	//GPS信息管理
	private double latitude=0;
	private double longtitude=0;
	public void saveGPS(double lat, double lon)
	{
		latitude=lat;
		longtitude=lon;
	}
	public double getLatitude()
	{
		return latitude;
	}
	public double getLongitude()
	{
		return longtitude;
	}
}
