package com.rongf.ticket.entity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;

public class Passenger {
	// 编号
	private String code;
	// 姓名
	private String passengerName;
	// 性别编号
	private String sexCode;
	// 性别
	private String sexName;
	// 出生日期
	private String bornDate;
	// 国家
	private String countryCode;
	// 乘客证件类型编号
	private String passengerIdTypeCode;
	// 乘客证件类型
	private String passengerIdTypeName;
	// 乘客证件号
	private String passengerIdNo;
	// 乘客类型编号
	private String passengerType;
	// TODO
	private String passengerFlag;
	// 乘客类型
	private String passengerTypeName;
	// 手机号
	private String mobileNo;
	// 电话号
	private String phoneNo;
	// 邮箱
	private String email;
	// 地址
	private String address;
	// 邮编
	private String postalcode;
	// 首字母编号
	private String firstLetter;
	// 记录总数
	private String recordCount;
	// TODO
	private String totalTimes;
	// 序列号
	private String indexId;
	// TODO
	private String gatBornDate;
	// TODO
	private String gatValidDateStart;
	// TODO
	private String gatValidDateEnd;
	// TODO
	private String gatVersion;
	
	private Passenger() {}
	
	public static List<Passenger> buildPassengerFromJsonArray(JSONArray passengerArray) {
		List<Passenger> passengerList = new ArrayList<>();
		
		if (null == passengerArray)
			return passengerList;
		
		for (int i = 0; i < passengerArray.size(); i++) {
			Passenger passenger = new Passenger();
			
			Field[] fieldArray = passenger.getClass().getDeclaredFields();
			for (int j = 0; j < fieldArray.length; j++) {
				try {
					String setMethodName = "set" 
							+ fieldArray[j].getName().substring(0, 1).toUpperCase() 
							+ fieldArray[j].getName().substring(1); 
					Method setMethod = passenger.getClass().getMethod(setMethodName, String.class);
					
					String jsonValue = new String();
					for (String key : passengerArray.getJSONObject(i).keySet()) {
						if (key.equals(fieldArray[j].getName())) {
							jsonValue = passengerArray.getJSONObject(i).getString(key);
							break;
						}
						
						StringBuilder transKey = new StringBuilder(key);
						int underLineIndex = transKey.indexOf("_");
						while (underLineIndex != -1) {
							transKey.deleteCharAt(underLineIndex);
							transKey.setCharAt(underLineIndex, Character.toUpperCase(transKey.charAt(underLineIndex)));
							underLineIndex = transKey.indexOf("_");
						}
						if (transKey.toString().equals(fieldArray[j].getName())) {
							jsonValue = passengerArray.getJSONObject(i).getString(key);
							break;
						}
					}
					
					setMethod.invoke(passenger, jsonValue);
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					return new ArrayList<>();
				}
			}
			passengerList.add(passenger);
		}
		
		return passengerList;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPassengerName() {
		return passengerName;
	}

	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}

	public String getSexCode() {
		return sexCode;
	}

	public void setSexCode(String sexCode) {
		this.sexCode = sexCode;
	}

	public String getSexName() {
		return sexName;
	}

	public void setSexName(String sexName) {
		this.sexName = sexName;
	}

	public String getBornDate() {
		return bornDate;
	}

	public void setBornDate(String bornDate) {
		this.bornDate = bornDate;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getPassengerIdTypeCode() {
		return passengerIdTypeCode;
	}

	public void setPassengerIdTypeCode(String passengerIdTypeCode) {
		this.passengerIdTypeCode = passengerIdTypeCode;
	}

	public String getPassengerIdTypeName() {
		return passengerIdTypeName;
	}

	public void setPassengerIdTypeName(String passengerIdTypeName) {
		this.passengerIdTypeName = passengerIdTypeName;
	}

	public String getPassengerIdNo() {
		return passengerIdNo;
	}

	public void setPassengerIdNo(String passengerIdNo) {
		this.passengerIdNo = passengerIdNo;
	}

	public String getPassengerType() {
		return passengerType;
	}

	public void setPassengerType(String passengerType) {
		this.passengerType = passengerType;
	}

	public String getPassengerFlag() {
		return passengerFlag;
	}

	public void setPassengerFlag(String passengerFlag) {
		this.passengerFlag = passengerFlag;
	}

	public String getPassengerTypeName() {
		return passengerTypeName;
	}

	public void setPassengerTypeName(String passengerTypeName) {
		this.passengerTypeName = passengerTypeName;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostalcode() {
		return postalcode;
	}

	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}

	public String getFirstLetter() {
		return firstLetter;
	}

	public void setFirstLetter(String firstLetter) {
		this.firstLetter = firstLetter;
	}

	public String getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(String recordCount) {
		this.recordCount = recordCount;
	}

	public String getTotalTimes() {
		return totalTimes;
	}

	public void setTotalTimes(String totalTimes) {
		this.totalTimes = totalTimes;
	}

	public String getIndexId() {
		return indexId;
	}

	public void setIndexId(String indexId) {
		this.indexId = indexId;
	}

	public String getGatBornDate() {
		return gatBornDate;
	}

	public void setGatBornDate(String gatBornDate) {
		this.gatBornDate = gatBornDate;
	}

	public String getGatValidDateStart() {
		return gatValidDateStart;
	}

	public void setGatValidDateStart(String gatValidDateStart) {
		this.gatValidDateStart = gatValidDateStart;
	}

	public String getGatValidDateEnd() {
		return gatValidDateEnd;
	}

	public void setGatValidDateEnd(String gatValidDateEnd) {
		this.gatValidDateEnd = gatValidDateEnd;
	}

	public String getGatVersion() {
		return gatVersion;
	}

	public void setGatVersion(String gatVersion) {
		this.gatVersion = gatVersion;
	}
	
}
