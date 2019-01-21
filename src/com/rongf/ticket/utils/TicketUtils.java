package com.rongf.ticket.utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.entity.ContentType;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.rongf.ticket.entity.GrabCondition;
import com.rongf.ticket.entity.TicketInfo;

public class TicketUtils {
	
	/**
	 * 根据起始站和时间查找车票
	 * 
	 * @param fromStation：出发站点
	 * @param toStation：目的站点
	 * @param dateString：出发日期
	 * 
	 * @return：车票集合
	 */
	private static List<TicketInfo> queryTicket(String fromStation, String toStation, String dateString){
		Map<String, String> paramMap = new LinkedHashMap<>();
		paramMap.put("leftTicketDTO.train_date", dateString);
		paramMap.put("leftTicketDTO.from_station", CityInfo.getCityCode(fromStation));
		paramMap.put("leftTicketDTO.to_station", CityInfo.getCityCode(toStation));
		paramMap.put("purpose_codes", "ADULT");
		
		String url = PublicInfo.QUERY_TICKET_URL;
		StringBuilder paramString = new StringBuilder();
		for (String key : paramMap.keySet())
			paramString.append(key + "=" + paramMap.get(key) + "&");
		if (paramString.length() > 0) 
			url += "?" + paramString.substring(0, paramString.length() - 1);
		
		System.out.println("搜索【" + fromStation +  "】到【" + toStation + "】【" + dateString + "】的车票");
		String response = HttpUtils.get(url, ContentType.APPLICATION_JSON);
		JSONObject responseJson = null;
		
		try {
			responseJson = JSONObject.parseObject(response);
		} catch (JSONException e) {
			System.out.println("error url: " + url);
			e.printStackTrace();
		}
		
		if (null != responseJson && responseJson.getBooleanValue("status")) {
			JSONObject responseData = responseJson.getJSONObject("data");
			return TicketInfo.buildFrom(responseData.getJSONArray("result"));
		}
		
		return new ArrayList<>();
	}
	
	/**
	 * 查找车票
	 * 
	 * @param condition：抢票条件
	 * @return：车票集合（包含无票车次）
	 */
	public static List<TicketInfo> grabTickt(GrabCondition condition) {
		List<TicketInfo> ticketList = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(sdf.parse(condition.getDateString()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < condition.getDays(); i++) {
			ticketList.addAll(queryTicket(condition.getFromStation(), condition.getToStation(), sdf.format(calendar.getTime())));
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		List<TicketInfo> filterList = filterTrain(ticketList, condition);
		if (!filterList.isEmpty())
			return filterList;
		
		// 中间站补票
		if (condition.isAdjustFlag()) {
			// 所有可以补票的车次
			Set<String> stationTrainCodeSet = new HashSet<>();
			// 遍历所有车次，找出中间站集合
			for (TicketInfo ticketInfo : ticketList) {
				if (stationTrainCodeSet.contains(ticketInfo.getStationTrainCode()))
					continue;
				else
					stationTrainCodeSet.add(ticketInfo.getStationTrainCode());
			}
			
			List<String> middleStationList = adjustStation(ticketList, condition);
			for (String middleStation : middleStationList) {
				GrabCondition middleCondition = new GrabCondition();
				middleCondition.setFromStation(condition.getFromStation());
				middleCondition.setToStation(middleStation);
				middleCondition.setDateString(condition.getDateString());
				middleCondition.setDays(condition.getDays());
				middleCondition.setEarliestTime(condition.getEarliestTime());
				middleCondition.setLatestTime(condition.getLatestTime());
				middleCondition.setTrainTypeSet(condition.getTrainTypeSet());
				middleCondition.setSeatTypeSet(condition.getSeatTypeSet());
				
				filterList.addAll(grabMiddleTickt(middleCondition, stationTrainCodeSet));
				
				if (!filterList.isEmpty())
					return filterList;
			}
		}
		
		return filterList;
	}

	/**
	 * 补票
	 * 
	 * @param middleCondition：补票条件
	 * @param stationTrainCodeSet：可补票车次
	 * @return：补票列表
	 */
	private static List<TicketInfo> grabMiddleTickt(GrabCondition middleCondition, Set<String> stationTrainCodeSet) {
		List<TicketInfo> ticketList = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(sdf.parse(middleCondition.getDateString()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < middleCondition.getDays(); i++) {
			ticketList.addAll(queryTicket(middleCondition.getFromStation(), middleCondition.getToStation(), sdf.format(calendar.getTime())));
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		List<TicketInfo> filterList = new ArrayList<>();
		for (TicketInfo ticketInfo : ticketList)
			if (stationTrainCodeSet.contains(ticketInfo.getStationTrainCode()))
				filterList.add(ticketInfo);
		
		return filterTrain(filterList, middleCondition);
	}
	/**
	 * 查询列车车次信息
	 * 
	 * @param ticket：车票信息
	 * @return 中间站点详细信息
	 * 
	 * "arrive_time": "20:38",
	 * "station_name": "武昌",
	 * "start_time": "20:38",
	 * "stopover_time": "----",
	 * "station_no": "03",
	 * "isEnabled": true
	 */
	public static JSONArray queryTrainNo(TicketInfo ticket) {
		Map<String, String> paramMap = new LinkedHashMap<>();
		paramMap.put("train_no", ticket.getTrainNo());
		paramMap.put("from_station_telecode", ticket.getFromStationTelecode());
		paramMap.put("to_station_telecode", ticket.getToStationTelecode());
		
		SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calender = Calendar.getInstance();
		try {
			calender.setTime(yyyyMMdd.parse(ticket.getStartTrainDate()));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		paramMap.put("depart_date", yyyy_MM_dd.format(calender.getTime()));
		
		String url = PublicInfo.QUERY_TRAIN_NO_URL;
		StringBuilder paramString = new StringBuilder();
		for (String key : paramMap.keySet())
			paramString.append(key + "=" + paramMap.get(key) + "&");
		if (paramString.length() > 0) 
			url += "?" + paramString.substring(0, paramString.length() - 1);
		
		String response = HttpUtils.get(url, ContentType.APPLICATION_JSON);
		JSONObject responseJson = null;
		try {
			responseJson = JSONObject.parseObject(response);
		} catch (JSONException e) {
			System.out.println("error url: " + url);
			e.printStackTrace();
		}
		
		if (null == responseJson)
			return null;
		
		if (responseJson.getBooleanValue("status")) {
			return responseJson.getJSONObject("data").getJSONArray("data");
		}
		
		return null;
	}

	/**
	 * 依据火车类型和席位类型，筛选有票车次
	 * 
	 * @param ticketList：火车票列表
	 * @param condition : 抢票条件
	 */
	public static List<TicketInfo> filterTrain(List<TicketInfo> ticketList, GrabCondition condition) {
		List<TicketInfo> resultList = new ArrayList<>();
		for (TicketInfo ticket : ticketList) {
			// 火车类型筛选
			if (!condition.getTrainTypeSet().isEmpty() && !condition.getTrainTypeSet().contains(ticket.getStationTrainCode().substring(0, 1)))
				continue;
			
			// 火车出发时间筛选
			if (!condition.getEarliestTime().isEmpty() && condition.getEarliestTime().compareTo(ticket.getStartTime()) > 0)
				continue;
			if (!condition.getLatestTime().isEmpty() && condition.getLatestTime().compareTo(ticket.getStartTime()) < 0)
				continue;
			
			// 火车席位筛选
			boolean ticketRest = false;
			for (String seatType : condition.getSeatTypeSet()) {
				try {
					String getMethodName = "get" + seatType.substring(0, 1).toUpperCase() + seatType.substring(1).toLowerCase() + "Num"; 
					Method getMethod = ticket.getClass().getMethod(getMethodName);
					String value = (String) getMethod.invoke(ticket);
					if (!"--".equals(value) && !"无".equals(value)) {
						ticketRest = true;
						break;
					}
				} catch (NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			
			if (ticketRest)
				resultList.add(ticket);
		}
		return resultList;
	}
	
	/**
	 * <table>
	 * 	 <thead>
	 *     <tr>
	 *       <td>车次</td>
	 *       <td>始发站</td>
	 *       <td>终点站</td>
	 *       <td>出发站</td>
	 *       <td>目的站</td>
	 *       <td>出发时间</td>
	 *     </tr>
	 *   </thead>
	 * </table>
	 * 
	 * @param ticketList：车票列表
	 */
	public static void printTickets(List<TicketInfo> ticketList) {
		StringBuilder output = new StringBuilder();
		String outFormat = "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s";
		output.append(String.format(outFormat, rightPadToBytes("车次", 5), 
											   rightPadToBytes("始发站", 12), 
											   rightPadToBytes("终点站", 12), 
											   rightPadToBytes("出发站", 12), 
											   rightPadToBytes("目的站", 12),
//											   rightPadToBytes("商务座", 6),
											   rightPadToBytes("一等座", 6),
											   rightPadToBytes("二等座", 6),
											   rightPadToBytes("软卧", 4),
											   rightPadToBytes("硬卧", 4),
//											   rightPadToBytes("无座", 4),
											   rightPadToBytes("出发时间", 12)));
		
		for (TicketInfo ticket : ticketList) {
			output.append("\r\n");
			output.append(String.format(outFormat, rightPadToBytes(ticket.getStationTrainCode(), 5), 
												   rightPadToBytes(CityInfo.getCodeCity(ticket.getStartStationTelecode()), 12), 
												   rightPadToBytes(CityInfo.getCodeCity(ticket.getEndStationTelecode()), 12), 
												   rightPadToBytes(ticket.getFromStationName(), 12),
												   rightPadToBytes(ticket.getToStationName(), 12),
//												   rightPadToBytes(ticket.getSwzNum(), 6),
												   rightPadToBytes(ticket.getZyNum(), 6),
												   rightPadToBytes(ticket.getZeNum(), 6),
												   rightPadToBytes(ticket.getRwNum(), 4),
												   rightPadToBytes(ticket.getYwNum(), 4),
//												   rightPadToBytes(ticket.getWzNum(), 4),
												   rightPadToBytes(ticket.getStartTrainDate(), 12)));
		}
		
		System.out.println(output.toString());
	}
	
	public static String rightPadToBytes(String source, int bytes) {
		try {
			byte[] sourceByte = source.getBytes("gbk");
			if (sourceByte.length >= bytes)
				return source;
			
			String format = "%" + (sourceByte.length - bytes) + "s";
			source += String.format(format, "");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return source;
	}
	
	/**
	 * 查询上车补票信息
	 * 
	 * @param ticketList：真正目的地车票信息
	 * @param condition：抢票条件
	 * @return：中间车站
	 */
	public static List<String> adjustStation(List<TicketInfo> ticketList, GrabCondition condition) {
		// 列车编号集合
		Set<String> trainNoSet = new HashSet<>();
		// 出发站点集合
		Set<String> fromStationSet = new HashSet<>();
		// 目的站点集合
		Set<String> toStationSet = new HashSet<>();
		for (TicketInfo ticket : ticketList) {
			trainNoSet.add(ticket.getTrainNo());
			fromStationSet.add(ticket.getFromStationName());
			toStationSet.add(ticket.getToStationName());
		}
		
		// 可以补票的目的车站集合(value为距离目的车站的远近因子，剩余站数/总站数)
		Map<String, Double> trainStationMap = new HashMap<>();
		// 已经查找过的车次
		Set<String> trainScanedSet = new HashSet<>();
		// 遍历所有车次，找出中间站集合
		for (TicketInfo ticketInfo : ticketList) {
			if (trainScanedSet.contains(ticketInfo.getStationTrainCode()))
				continue;
			else
				trainScanedSet.add(ticketInfo.getStationTrainCode());
			
			// 火车类型筛选
			if (!condition.getTrainTypeSet().isEmpty() && !condition.getTrainTypeSet().contains(ticketInfo.getStationTrainCode().substring(0, 1)))
				continue;
			
			// 火车出发时间筛选
			if (!condition.getEarliestTime().isEmpty() && condition.getEarliestTime().compareTo(ticketInfo.getStartTime()) > 0)
				continue;
			if (!condition.getLatestTime().isEmpty() && condition.getLatestTime().compareTo(ticketInfo.getStartTime()) < 0)
				continue;
			
			// 火车席位筛选
			boolean seatHas = false;
			for (String seatType : condition.getSeatTypeSet()) {
				try {
					String getMethodName = "get" + seatType.substring(0, 1).toUpperCase() + seatType.substring(1).toLowerCase() + "Num"; 
					Method getMethod = ticketInfo.getClass().getMethod(getMethodName);
					String value = (String) getMethod.invoke(ticketInfo);
					if (!"--".equals(value)) {
						seatHas = true;
						break;
					}
				} catch (NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			
			if (!seatHas)
				continue;
						
			JSONArray stationArray = queryTrainNo(ticketInfo);
			
			if (null != stationArray) {
				boolean start = false;
				for (int i= 0; i < stationArray.size(); i++) {
					String curStation = stationArray.getJSONObject(i).getString("station_name");
					// 找到出发站
					if (!start) {
						if (fromStationSet.contains(curStation))
							start = true;
						continue;
					}
					
					// 到目的站跳出
					if (toStationSet.contains(curStation))
						break;
					
					// 忽略double比较的精度问题
					double destinationFact = 1 - (double) i / (stationArray.size() - 1);
					if (trainStationMap.containsKey(curStation) ) {
						if (destinationFact < trainStationMap.get(curStation))
							trainStationMap.put(curStation,  destinationFact);
					} else
						trainStationMap.put(curStation, destinationFact);
				}
			}
		}
		
		// 根据距离因子升序排列
		List<Entry<String, Double>> sortList = new ArrayList<>(trainStationMap.entrySet());
		Collections.sort(sortList, new Comparator<Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> e1, Entry<String, Double> e2) {
				if (e1.getValue() == e2.getValue())
					return 0;
				
				return e1.getValue() > e2.getValue() ? 1 : -1;
			}
		});
		
		List<String> resultList = new ArrayList<>();
		for (Entry<String, Double> entry : sortList)
			resultList.add(entry.getKey());
		
		return resultList;
	}
}
