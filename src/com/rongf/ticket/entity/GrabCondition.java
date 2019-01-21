package com.rongf.ticket.entity;

import java.util.HashSet;
import java.util.Set;

public class GrabCondition {
	// 出发站
	String fromStation;
	// 目的站
	String toStation;
	// 开始日期(yyyy-MM-dd)
	String dateString;
	// 抢票天数
	int days;
	// 最早发车时间(hh:mm)
	String earliestTime = "";
	// 最晚发车时间(hh:mm)
	String latestTime = "";
	// 火车类型
	Set<String> trainTypeSet = new HashSet<>();
	// 座位类型
	Set<String> seatTypeSet = new HashSet<>();
	// 是否接受上车补票
	boolean adjustFlag = false;
	// 乘车人集合
	Set<String> passengerSet = new HashSet<>();
	
	public GrabCondition() {}
	
	public GrabCondition(String fromStation, String toStation, String dateString) {
		this(fromStation, toStation, dateString, 1);
	}
	
	public GrabCondition(String fromStation, String toStation, String dateString, int days) {
		this.fromStation = fromStation;
		this.toStation = toStation;
		this.dateString = dateString;
		this.days = days;
	}
	
	public String getFromStation() {
		return fromStation;
	}
	public void setFromStation(String fromStation) {
		this.fromStation = fromStation;
	}
	public String getToStation() {
		return toStation;
	}
	public void setToStation(String toStation) {
		this.toStation = toStation;
	}
	public String getDateString() {
		return dateString;
	}
	public void setDateString(String dateString) {
		this.dateString = dateString;
	}
	public int getDays() {
		return days;
	}
	public void setDays(int days) {
		this.days = days;
	}
	public String getEarliestTime() {
		return earliestTime;
	}
	public void setEarliestTime(String earliestTime) {
		this.earliestTime = earliestTime;
	}
	public String getLatestTime() {
		return latestTime;
	}
	public void setLatestTime(String latestTime) {
		this.latestTime = latestTime;
	}
	public Set<String> getTrainTypeSet() {
		return trainTypeSet;
	}
	public void setTrainTypeSet(Set<String> trainTypeSet) {
		this.trainTypeSet = trainTypeSet;
	}
	public Set<String> getSeatTypeSet() {
		return seatTypeSet;
	}
	public void setSeatTypeSet(Set<String> seatTypeSet) {
		this.seatTypeSet = seatTypeSet;
	}
	public boolean isAdjustFlag() {
		return adjustFlag;
	}
	public void setAdjustFlag(boolean adjustFlag) {
		this.adjustFlag = adjustFlag;
	}
	public Set<String> getPassengerSet() {
		return passengerSet;
	}
	public void setPassengerSet(Set<String> passengerSet) {
		this.passengerSet = passengerSet;
	}
	
	
	
}
