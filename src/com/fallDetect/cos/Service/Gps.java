package com.fallDetect.cos.Service;

/**
 * GPS��γ��
 * 
 * @author zhangxu
 * 
 */

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
 
public class Gps{
 private Location location = null;
 private LocationManager locationManager = null;
 private Context context = null;
 
 /**
  * ��ʼ�� 
  * 
  * @param ctx
  */
 public Gps(Context ctx) {
  context=ctx;
  locationManager=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
  location = locationManager.getLastKnownLocation(getProvider());
  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
 }
 
 
 // ��ȡLocation Provider
 private String getProvider() {
  // ����λ�ò�ѯ����
  Criteria criteria = new Criteria();
  // ��ѯ���ȣ���
  criteria.setAccuracy(Criteria.ACCURACY_FINE);
  // �Ƿ��ѯ��������
  criteria.setAltitudeRequired(false);
  // �Ƿ��ѯ��λ�� : ��
  criteria.setBearingRequired(false);
  // �Ƿ������ѣ���
  criteria.setCostAllowed(true);
  // ����Ҫ�󣺵�
  criteria.setPowerRequirement(Criteria.POWER_LOW);
  // ��������ʵķ���������provider����2������Ϊtrue˵�� , ���ֻ��һ��provider����Ч��,�򷵻ص�ǰprovider
  return locationManager.getBestProvider(criteria, true);
 }
 
 private LocationListener locationListener = new LocationListener() {
  // λ�÷����ı�����
  public void onLocationChanged(Location l) {
   if(l!=null){
    location=l;
   }
  }
 
  // provider ���û��رպ����
  public void onProviderDisabled(String provider) {
   location=null;
  }
 
  // provider ���û����������
  public void onProviderEnabled(String provider) {
   Location l = locationManager.getLastKnownLocation(provider);
   if(l!=null){
    location=l;
   }
     
  }
 
  // provider ״̬�仯ʱ����
  public void onStatusChanged(String provider, int status, Bundle extras) {
  }
 
 };
  
 public Location getLocation(){
  return location;
 }
  
 public void closeLocation(){
  if(locationManager!=null){
   if(locationListener!=null){
    locationManager.removeUpdates(locationListener);
    locationListener=null;
   }
   locationManager=null;
  }
 }
 
 
}