package com.rongf.ticket.client;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.rongf.ticket.entity.GrabCondition;
import com.rongf.ticket.entity.Passenger;
import com.rongf.ticket.entity.TicketInfo;
import com.rongf.ticket.utils.LoginUtils;
import com.rongf.ticket.utils.OrderUtils;
import com.rongf.ticket.utils.PassengerUtils;
import com.rongf.ticket.utils.PublicInfo.SeatType;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import com.rongf.ticket.utils.TicketUtils;
import com.rongf.ticket.utils.TicketUtilsTest;

public class LoginClient {
	public static BlockingQueue<TicketInfo> ticketQueue = new ArrayBlockingQueue<>(20);
	private static volatile boolean found = false;
	/**
	 * 开始抢票
	 * 
	 * @param args：json串用于传参
	 * {
	 * 		"username": "12306用户（必填）",
	 * 		"password": "12306密码（必填）",
	 * 		"fromStation": "出发站（必填）",
	 * 		"toStation": "目的站（必填）",
	 * 		"dateString": "乘车日期(yyyy-MM-dd)（必填）",
	 * 		"days": "连续抢票天数",
	 * 		"earliestTime": "最早发车时间",
	 * 		"latestTime": "最晚发车时间",
	 * 		"trainType": "火车类型（用“，”分隔）[G,C,D,Z,T,K,L]",
	 * 		"seatType": "座位类型（用“，”分隔 [RW,RZ,YW,YZ,ZE,ZY]）",
	 * 		"adjustFlag": "是否上车补票[true, false]",
	 * 		"passengers": "乘车人（用“，”分隔）（必填）"
	 * }
	 */
	public static void main(String[] args) {
		String paramString = "{\n" + 
				"	\"username\": \"***\",\n" + 
				"	\"password\": \"***\",\n" + 
				"	\"fromStation\": \"深圳\",\n" + 
				"	\"toStation\": \"武汉\",\n" + 
				"	\"dateString\": \"2019-01-26\",\n" + 
				"	\"days\": \"7\",\n" + 
				"	\"earliestTime\": \"\",\n" + 
				"	\"latestTime\": \"\",\n" + 
				"	\"trainType\": \"G\",\n" + 
				"	\"seatType\": \"ZE,ZY\",\n" + 
				"	\"adjustFlag\": \"true\",\n" + 
				"	\"passengers\": \"***\"\n" + 
				"}";
		final JSONObject paramJson;
		try {
//			paramJson = JSONObject.parseObject(args[0]);
			paramJson = JSONObject.parseObject(paramString);
		} catch (JSONException e) {
			System.out.println("上送参数非合法json串");
			return;
		}
		
		if (null == paramJson) {
			System.out.println("请输入请求参数");
			return;
		}
		
		if (null == paramJson.getString("username") || paramJson.getString("username").isEmpty()) {
			System.out.println("必须输入12306用户名");
			return;
		}
		
		if (null == paramJson.getString("password") || paramJson.getString("password").isEmpty()) {
			System.out.println("必须输入12306密码");
			return;
		}
		
		if (null == paramJson.getString("fromStation") || paramJson.getString("fromStation").isEmpty()) {
			System.out.println("必须输入出发站");
			return;
		}
		
		if (null == paramJson.getString("toStation") || paramJson.getString("toStation").isEmpty()) {
			System.out.println("必须输入目的站");
			return;
		}
		
		if (null == paramJson.getString("dateString") || paramJson.getString("dateString").isEmpty()) {
			System.out.println("必须输入乘车日期(yyyy-MM-dd)");
			return;
		}
		
		if (null == paramJson.getString("passengers") || paramJson.getString("passengers").isEmpty()) {
			System.out.println("必须输入乘车人（用“，”分隔）");
			return;
		}
			
		if (!LoginUtils.login(paramJson.getString("username"), paramJson.getString("password"))) {
			System.out.println("登录失败");
			return;
		}
		
		// 初始化抢票条件
		String fromStation = paramJson.getString("fromStation");
		String toStation = paramJson.getString("toStation");
		String dateString = paramJson.getString("dateString");
		Integer days = paramJson.getInteger("days");
		String earliestTime = paramJson.getString("earliestTime");
		String latestTime = paramJson.getString("latestTime");
		boolean adjustFlag = paramJson.getBoolean("adjustFlag") == null || !paramJson.getBoolean("adjustFlag") ? false : true;
		
		Set<String> trainTypeSet = new HashSet<>();
		String trainTypes = paramJson.getString("trainType");
		String[] trainTypeArray = trainTypes.split(",");
		for (int i = 0; i < trainTypeArray.length; i++)
			trainTypeSet.add(trainTypeArray[i]);
		
		Set<String> seatTypeSet = new HashSet<>();
		String seatTypes = paramJson.getString("seatType");
		String[] seatTypeArray = seatTypes.split(",");
		for (int i = 0; i < seatTypeArray.length; i++)
			seatTypeSet.add(seatTypeArray[i]);
		
		Set<String> passengerSet = new HashSet<>();
		String passengers = paramJson.getString("passengers");
		String[] passengerArray = passengers.split(",");
		for (int i = 0; i < passengerArray.length; i++)
			passengerSet.add(passengerArray[i]);
		
		GrabCondition condition = new GrabCondition(fromStation, toStation, dateString);
		if (null != days && days > 0)
			condition.setDays(days);
		if (null != earliestTime && !earliestTime.isEmpty())
			condition.setEarliestTime(earliestTime);
		if (null != latestTime && !latestTime.isEmpty())
			condition.setLatestTime(latestTime);
		if (adjustFlag)
			condition.setAdjustFlag(adjustFlag);
		if (!trainTypeSet.isEmpty())
			condition.setTrainTypeSet(trainTypeSet);
		if (!seatTypeSet.isEmpty())
			condition.setSeatTypeSet(seatTypeSet);
		condition.setPassengerSet(passengerSet);
		
		// 启动下单线程
		Thread orderThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				startOrderThread(condition);
			}
		});
		orderThread.setDaemon(true);
		orderThread.start();
		
		// 启动刷票线程
		Thread grabThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				startGrabThread(condition);
			}
		});
		grabThread.setDaemon(true);
		grabThread.start();
		
		// 保持登录线程
		Thread keepThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				keepThread(paramJson.getString("username"), paramJson.getString("password"));
			}
		});
		keepThread.setDaemon(true);
		keepThread.start();
		
		while(!found);
		
		playMusic();
	}
	
	protected static void keepThread(String username, String password) {
		while (true) {
			if (!LoginUtils.checkUser(LoginUtils.cookie))
				LoginUtils.login(username, password);
			
			System.out.println("心跳信息：" + new Date());
			try {
				Thread.sleep(1000 * 60);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static void startGrabThread(GrabCondition condition) {
		while (!found) {
			List<TicketInfo> ticketList = TicketUtils.grabTickt(condition);
			if (!ticketList.isEmpty()) {
				for (TicketInfo ticket : ticketList) {
					ticketQueue.offer(ticket);
				}
//				playMusic();
//				found = true;
			}
		}
		
	}

	private static void startOrderThread(GrabCondition condition) {
		boolean result= false;
		while (!result) {
			TicketInfo ticket = null;
			try {
				ticket = ticketQueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
			
			if (!LoginUtils.checkUser(LoginUtils.cookie))
				continue;
			
			if (!OrderUtils.beforeSubmit(ticket, LoginUtils.cookie))
				continue;
			Map<String, String> submitInfoMap = PassengerUtils.getSubmitInfo(LoginUtils.cookie);
			if (null == submitInfoMap)
				continue;
			
			String submitToken = submitInfoMap.get("submitToken");
			List<Passenger> allPassenger = PassengerUtils.getPassengers(submitToken, LoginUtils.cookie);
			List<Passenger> passengerList = new ArrayList<>();
			for (Passenger passenger : allPassenger)
				if (condition.getPassengerSet().contains(passenger.getPassengerName()))
					passengerList.add(passenger);
			if (passengerList.size() != condition.getPassengerSet().size()) {
				System.out.println("有乘车人不在当前账号中，请检查");
				continue;
			}
			
			for (String seatTypeName : condition.getSeatTypeSet()) {
				SeatType seatType = SeatType.getSeatForName(seatTypeName);
				if (!OrderUtils.checkOrderInfo(LoginUtils.cookie, seatType, passengerList, submitToken))
					continue;
				
				if (!OrderUtils.getQueueCount(LoginUtils.cookie, condition.getDateString(), ticket, seatType, submitInfoMap))
					continue;
				
				// TODO下单
				if (!OrderUtils.SubmitForQueue(ticket, LoginUtils.cookie, seatType, passengerList, submitInfoMap))
					continue;
				
				found = true;
				List<TicketInfo> tempList = new ArrayList<>();
				tempList.add(ticket);
				TicketUtils.printTickets(tempList);
				return;
			}
			
		}
	}
	
	private static void playMusic() {
		Thread musicThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					String filePath = TicketUtilsTest.class.getResource("/").getPath();
					File file = new File(filePath + File.separator + "youzhiwan.mp3");
					FileInputStream fis = new FileInputStream(file);
					BufferedInputStream bis = new BufferedInputStream(fis);
					Player player = new Player(bis);
					player.play();
				} catch (FileNotFoundException | JavaLayerException e) {
					e.printStackTrace();
				}
			}
		});
		musicThread.start();
	}
}
