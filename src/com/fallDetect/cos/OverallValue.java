package com.fallDetect.cos;



import android.app.Application;


//OverallValue��һ��ȫ���࣬�̳�application��Ӧ�ó�������ʱ�ʹ��ڣ����ڴ��ȫ�ֱ���
//�̳���application���������application��name�������������֣�com.fallDetect.cos.OverallVlaue����Ҫ��manifest�ļ���������
public class OverallValue extends Application{
	
	
	//��ֵ�趨
	private int Threshold=20;
	public void setThreshold(int flag)
	{
		Threshold=flag;
	}
	public int getThreshold()
	{
		return Threshold;
	}
	//���ŵȴ�ʱ���趨
	private int WaitTime=30;
	public void setWaitTime(int flag)
	{
		WaitTime=flag;
	}
	public int getWaitTime()
	{
		return WaitTime;
	}
	//����״̬��־
	private int RunningFlag=0;
	public void setRunningFlag(int flag)
	{
		RunningFlag=flag;
	}
	public int getRunningFlag()
	{
		return RunningFlag;
	}
	//�������Ʊ�־
	private boolean VoiceFlag=true;
	public void setVoiceFlag(boolean flag)
	{
		VoiceFlag=flag;
	}
	public boolean getVoiceFlag()
	{
		return VoiceFlag;
	}
	//λ����Ϣ���Ʊ�־
	private boolean GPSFlag=true;
	public void setGPSFlag(boolean flag)
	{
		GPSFlag=flag;
	}
	public boolean getGPSFlag()
	{
		return GPSFlag;
	}
	
	//GPS��Ϣ����
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
