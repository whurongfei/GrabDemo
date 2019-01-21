package com.rongf.ticket.entity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.rongf.ticket.utils.CityInfo;

public class TicketInfo {
	// 加密字符串
    private String secretStr;
    // 按钮信息
    private String buttonTextInfo;
    // 列车编号
    private String trainNo;
    // 站点车次
    private String stationTrainCode;
    // 始发站编号
    private String startStationTelecode;
    // 终点站编号
    private String endStationTelecode;
    // 出发站编号
    private String fromStationTelecode;
    // 目的站编号
    private String toStationTelecode;
    // 出发时间
    private String startTime;
    // 到站时间
    private String arriveTime;
    // 历时
    private String lishi;
    // 网络购票允许标志
    private String canWebBuy;
    // TODO
    private String ypInfo;
    // 出发日期
    private String startTrainDate;
    // 火车坐席特征
    private String trainSeatFeature;
    // 位置编号
    private String locationCode;
    // 出发站台
    private String fromStationNo;
    // 目的站台
    private String toStationNo;
    // TODO
    private String isSupportCard;
    // TODO
    private String controlledTrainFlag;
    // 
    private String ggNum;
    // 高级软卧
    private String grNum;
    // 其他
    private String qtNum;
    // 软卧
    private String rwNum;
    // 软座
    private String rzNum;
    // 
    private String tzNum;
    //　无座
    private String wzNum;
    //
    private String ybNum;
    //　硬卧
    private String ywNum;
    // 硬座
    private String yzNum;
    // 二等座
    private String zeNum;
    // 一等座
    private String zyNum;
    // 商务座
    private String swzNum;
    //
    private String srrbNum;
    //
    private String ypEx;
    // 坐席类型
    private String seatTypes;
    //
    private String exchangeTrainFlag;
    //
    private String houbuTrainFlag;
    // 出发站名称
    private String fromStationName;
    // 目的站名称
    private String toStationName;

    public TicketInfo (String secretStr,            
    		           String buttonTextInfo,       
    		           String trainNo,              
    		           String stationTrainCode,     
    		           String startStationTelecode, 
    		           String endStationTelecode,   
    		           String fromStationTelecode,  
    		           String toStationTelecode,    
    		           String startTime,            
    		           String arriveTime,            
    		           String lishi,                 
    		           String canWebBuy,             
    		           String ypInfo,                
    		           String startTrainDate,        
    		           String trainSeatFeature,      
    		           String locationCode,          
    		           String fromStationNo,         
    		           String toStationNo,           
    		           String isSupportCard,         
    		           String controlledTrainFlag,   
    		           String ggNum,                 
    		           String grNum,                 
    		           String qtNum,                 
    		           String rwNum,                 
    		           String rzNum,                 
    		           String tzNum,                 
    		           String wzNum,                 
    		           String ybNum,                 
    		           String ywNum,                 
    		           String yzNum,                 
    		           String zeNum,                 
    		           String zyNum,                 
    		           String swzNum,                
    		           String srrbNum,               
    		           String ypEx,                  
    		           String seatTypes,             
    		           String exchangeTrainFlag,     
    		           String houbuTrainFlag)         {
    	this.secretStr = secretStr;            
    	this.buttonTextInfo = buttonTextInfo;       
    	this.trainNo = trainNo;              
    	this.stationTrainCode = stationTrainCode;     
    	this.startStationTelecode = startStationTelecode; 
    	this.endStationTelecode = endStationTelecode;   
    	this.fromStationTelecode = fromStationTelecode;  
    	this.toStationTelecode = toStationTelecode;    
    	this.startTime = startTime;            
    	this.arriveTime = arriveTime;           
    	this.lishi = lishi;                
    	this.canWebBuy = canWebBuy;            
    	this.ypInfo = ypInfo;               
    	this.startTrainDate = startTrainDate;       
    	this.trainSeatFeature = trainSeatFeature;     
    	this.locationCode = locationCode;         
    	this.fromStationNo = fromStationNo;        
    	this.toStationNo = toStationNo;          
    	this.isSupportCard = isSupportCard;        
    	this.controlledTrainFlag = controlledTrainFlag;  
    	this.ggNum = ggNum.isEmpty() ? "--" : ggNum;                
    	this.grNum = grNum.isEmpty() ? "--" : grNum;                
    	this.qtNum = qtNum.isEmpty() ? "--" : qtNum;                
    	this.rwNum = rwNum.isEmpty() ? "--" : rwNum;                
    	this.rzNum = rzNum.isEmpty() ? "--" : rzNum;                
    	this.tzNum = tzNum.isEmpty() ? "--" : tzNum;                
    	this.wzNum = wzNum.isEmpty() ? "--" : wzNum;                
    	this.ybNum = ybNum.isEmpty() ? "--" : ybNum;                
    	this.ywNum = ywNum.isEmpty() ? "--" : ywNum;                
    	this.yzNum = yzNum.isEmpty() ? "--" : yzNum;                
    	this.zeNum = zeNum.isEmpty() ? "--" : zeNum;                
    	this.zyNum = zyNum.isEmpty() ? "--" : zyNum;                
    	this.swzNum = swzNum.isEmpty() ? "--" : swzNum;               
    	this.srrbNum = srrbNum.isEmpty() ? "--" : srrbNum;              
    	this.ypEx = ypEx;                 
    	this.seatTypes = seatTypes;            
    	this.exchangeTrainFlag = exchangeTrainFlag;    
    	this.houbuTrainFlag = houbuTrainFlag;        
    	this.fromStationName = CityInfo.getCodeCity(fromStationTelecode);
    	this.toStationName = CityInfo.getCodeCity(toStationTelecode);
    }
    
    public static List<TicketInfo> buildFrom(JSONArray ticketArray) {
    	List<TicketInfo> resultList = new ArrayList<>();
    	for (int index = 0; index < ticketArray.size(); index++) {
    		String[] stringArray = ticketArray.getString(index).split("\\|");
    		resultList.add(new TicketInfo(stringArray[0],
    									  stringArray[1],
    									  stringArray[2],
    									  stringArray[3],
    									  stringArray[4],
    									  stringArray[5],
    									  stringArray[6],
    									  stringArray[7],
    									  stringArray[8],
    									  stringArray[9],
    									  stringArray[10],
    									  stringArray[11],
    									  stringArray[12],
    									  stringArray[13],
    									  stringArray[14],
    									  stringArray[15],
    									  stringArray[16],
    									  stringArray[17],
    									  stringArray[18],
    									  stringArray[19],
    									  stringArray[20],
    									  stringArray[21],
    									  stringArray[22],
    									  stringArray[23],
    									  stringArray[24],
    									  stringArray[25],
    									  stringArray[26],
    									  stringArray[27],
    									  stringArray[28],
    									  stringArray[29],
    									  stringArray[30],
    									  stringArray[31],
    									  stringArray[32],
    									  stringArray[33],
    									  stringArray[34],
    									  stringArray[35],
    									  stringArray[36],
    									  stringArray[37]));
    	}
    	
    	return resultList;
    }
    
    @Override
    public String toString() {
    	StringBuilder result = new StringBuilder();
    	
    	Field[] fields = this.getClass().getDeclaredFields();
    	for (int i = 0; i < fields.length; i++) {
			try {
				String attr = fields[i].getName();
				String getMethodName = "get" + attr.substring(0, 1).toUpperCase() + attr.substring(1);
				Method getMethod = this.getClass().getDeclaredMethod(getMethodName);
				String value = (String) getMethod.invoke(this);
				result.append(String.format("%s=%s", attr, value)).append("\r");
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	return result.toString();
    }

	public String getSecretStr() {
		return secretStr;
	}

	public void setSecretStr(String secretStr) {
		this.secretStr = secretStr;
	}

	public String getButtonTextInfo() {
		return buttonTextInfo;
	}

	public void setButtonTextInfo(String buttonTextInfo) {
		this.buttonTextInfo = buttonTextInfo;
	}

	public String getTrainNo() {
		return trainNo;
	}

	public void setTrainNo(String trainNo) {
		this.trainNo = trainNo;
	}

	public String getStationTrainCode() {
		return stationTrainCode;
	}

	public void setStationTrainCode(String stationTrainCode) {
		this.stationTrainCode = stationTrainCode;
	}

	public String getStartStationTelecode() {
		return startStationTelecode;
	}

	public void setStartStationTelecode(String startStationTelecode) {
		this.startStationTelecode = startStationTelecode;
	}

	public String getEndStationTelecode() {
		return endStationTelecode;
	}

	public void setEndStationTelecode(String endStationTelecode) {
		this.endStationTelecode = endStationTelecode;
	}

	public String getFromStationTelecode() {
		return fromStationTelecode;
	}

	public void setFromStationTelecode(String fromStationTelecode) {
		this.fromStationTelecode = fromStationTelecode;
	}

	public String getToStationTelecode() {
		return toStationTelecode;
	}

	public void setToStationTelecode(String toStationTelecode) {
		this.toStationTelecode = toStationTelecode;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getArriveTime() {
		return arriveTime;
	}

	public void setArriveTime(String arriveTime) {
		this.arriveTime = arriveTime;
	}

	public String getLishi() {
		return lishi;
	}

	public void setLishi(String lishi) {
		this.lishi = lishi;
	}

	public String getCanWebBuy() {
		return canWebBuy;
	}

	public void setCanWebBuy(String canWebBuy) {
		this.canWebBuy = canWebBuy;
	}

	public String getYpInfo() {
		return ypInfo;
	}

	public void setYpInfo(String ypInfo) {
		this.ypInfo = ypInfo;
	}

	public String getStartTrainDate() {
		return startTrainDate;
	}

	public void setStartTrainDate(String startTrainDate) {
		this.startTrainDate = startTrainDate;
	}

	public String getTrainSeatFeature() {
		return trainSeatFeature;
	}

	public void setTrainSeatFeature(String trainSeatFeature) {
		this.trainSeatFeature = trainSeatFeature;
	}

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public String getFromStationNo() {
		return fromStationNo;
	}

	public void setFromStationNo(String fromStationNo) {
		this.fromStationNo = fromStationNo;
	}

	public String getToStationNo() {
		return toStationNo;
	}

	public void setToStationNo(String toStationNo) {
		this.toStationNo = toStationNo;
	}

	public String getIsSupportCard() {
		return isSupportCard;
	}

	public void setIsSupportCard(String isSupportCard) {
		this.isSupportCard = isSupportCard;
	}

	public String getControlledTrainFlag() {
		return controlledTrainFlag;
	}

	public void setControlledTrainFlag(String controlledTrainFlag) {
		this.controlledTrainFlag = controlledTrainFlag;
	}

	public String getGgNum() {
		return ggNum;
	}

	public void setGgNum(String ggNum) {
		this.ggNum = ggNum;
	}

	public String getGrNum() {
		return grNum;
	}

	public void setGrNum(String grNum) {
		this.grNum = grNum;
	}

	public String getQtNum() {
		return qtNum;
	}

	public void setQtNum(String qtNum) {
		this.qtNum = qtNum;
	}

	public String getRwNum() {
		return rwNum;
	}

	public void setRwNum(String rwNum) {
		this.rwNum = rwNum;
	}

	public String getRzNum() {
		return rzNum;
	}

	public void setRzNum(String rzNum) {
		this.rzNum = rzNum;
	}

	public String getTzNum() {
		return tzNum;
	}

	public void setTzNum(String tzNum) {
		this.tzNum = tzNum;
	}

	public String getWzNum() {
		return wzNum;
	}

	public void setWzNum(String wzNum) {
		this.wzNum = wzNum;
	}

	public String getYbNum() {
		return ybNum;
	}

	public void setYbNum(String ybNum) {
		this.ybNum = ybNum;
	}

	public String getYwNum() {
		return ywNum;
	}

	public void setYwNum(String ywNum) {
		this.ywNum = ywNum;
	}

	public String getYzNum() {
		return yzNum;
	}

	public void setYzNum(String yzNum) {
		this.yzNum = yzNum;
	}

	public String getZeNum() {
		return zeNum;
	}

	public void setZeNum(String zeNum) {
		this.zeNum = zeNum;
	}

	public String getZyNum() {
		return zyNum;
	}

	public void setZyNum(String zyNum) {
		this.zyNum = zyNum;
	}

	public String getSwzNum() {
		return swzNum;
	}

	public void setSwzNum(String swzNum) {
		this.swzNum = swzNum;
	}

	public String getSrrbNum() {
		return srrbNum;
	}

	public void setSrrbNum(String srrbNum) {
		this.srrbNum = srrbNum;
	}

	public String getYpEx() {
		return ypEx;
	}

	public void setYpEx(String ypEx) {
		this.ypEx = ypEx;
	}

	public String getSeatTypes() {
		return seatTypes;
	}

	public void setSeatTypes(String seatTypes) {
		this.seatTypes = seatTypes;
	}

	public String getExchangeTrainFlag() {
		return exchangeTrainFlag;
	}

	public void setExchangeTrainFlag(String exchangeTrainFlag) {
		this.exchangeTrainFlag = exchangeTrainFlag;
	}

	public String getHoubuTrainFlag() {
		return houbuTrainFlag;
	}

	public void setHoubuTrainFlag(String houbuTrainFlag) {
		this.houbuTrainFlag = houbuTrainFlag;
	}

	public String getFromStationName() {
		return fromStationName;
	}

	public void setFromStationName(String fromStationName) {
		this.fromStationName = fromStationName;
	}

	public String getToStationName() {
		return toStationName;
	}

	public void setToStationName(String toStationName) {
		this.toStationName = toStationName;
	}
}
