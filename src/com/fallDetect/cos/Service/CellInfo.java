package com.fallDetect.cos.Service;


/**
 * ��վ��Ϣ
 * 
 * @author zhangxu
 * 
 */
public class CellInfo {
 /** ��վid�������ҵ���վ��λ�� */
 private int cellId;
 /** �ƶ������룬��3λ���й�Ϊ460����imsiǰ3λ */
 private String mobileCountryCode="460";
 /** �ƶ������룬��2λ�����й����ƶ��Ĵ���Ϊ00��02����ͨ�Ĵ���Ϊ01�����ŵĴ���Ϊ03����imsi��4~5λ */
 private String mobileNetworkCode="0";
 /** ���������� */
 private int locationAreaCode;
 /** �ź�����[ѡ gsm|cdma|wcdma] */
 private String radioType="";
 
 public CellInfo() {
 }
 
 public int getCellId() {
  return cellId;
 }
 
 public void setCellId(int cellId) {
  this.cellId = cellId;
 }
 
 public String getMobileCountryCode() {
  return mobileCountryCode;
 }
 
 public void setMobileCountryCode(String mobileCountryCode) {
  this.mobileCountryCode = mobileCountryCode;
 }
 
 public String getMobileNetworkCode() {
  return mobileNetworkCode;
 }
 
 public void setMobileNetworkCode(String mobileNetworkCode) {
  this.mobileNetworkCode = mobileNetworkCode;
 }
 
 public int getLocationAreaCode() {
  return locationAreaCode;
 }
 
 public void setLocationAreaCode(int locationAreaCode) {
  this.locationAreaCode = locationAreaCode;
 }
 
 public String getRadioType() {
  return radioType;
 }
 
 public void setRadioType(String radioType) {
  this.radioType = radioType;
 }
 
}