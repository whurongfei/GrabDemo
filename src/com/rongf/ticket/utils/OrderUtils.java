package com.rongf.ticket.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.CookieStore;
import org.apache.http.entity.ContentType;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.rongf.ticket.entity.Passenger;
import com.rongf.ticket.entity.TicketInfo;
import com.rongf.ticket.utils.PublicInfo.SeatType;

public class OrderUtils {
	public static boolean beforeSubmit(TicketInfo ticket, CookieStore cookie) {
		String url = PublicInfo.BEFORE_SUBMIT_URL;
		StringBuilder dateString = new StringBuilder(ticket.getStartTrainDate());
		dateString.insert(6, "-");
		dateString.insert(4, "-");
	
		StringBuilder paramString = new StringBuilder();
		paramString.append("secretStr=")
				   .append(ticket.getSecretStr())
		           .append("&train_date=")
		           .append(dateString.toString())
		           .append("&back_train_date=")
		           .append(dateString.toString())
		           .append("&tour_flag=dc&purpose_codes=ADULT&query_from_station_name=")
		           .append(ticket.getFromStationName())
		           .append("&query_to_station_name=")
		           .append(ticket.getToStationName())
		           .append("&undefined");
		url += "?" + paramString;
		
		String response = HttpUtils.get(url, cookie, HttpUtils.FORM_TYPE_UTF8);
		if (null == response || response.isEmpty())
			return false;
		
		try {
			JSONObject responseJson = JSONObject.parseObject(response);
			return responseJson.getBooleanValue("status");
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 订单预检查
	 * 
	 * @param cookie：cookie
	 * @param seatType：座位类型
	 * @param passengerList：乘客列表
	 * @param token：token
	 * @return：检查结果
	 */
	public static boolean checkOrderInfo(CookieStore cookie, SeatType seatType, List<Passenger> passengerList, String token) {
		String url = PublicInfo.CHECK_ORDER_URL;
		
		Map<String, String> paramMap = new LinkedHashMap<>();
		// 固定值
		paramMap.put("cancel_flag", "2");
		// 订单号
		paramMap.put("bed_level_order_num", "000000000000000000000000000000");
		// 座位类型，0，车票类型(1 成人票)，姓名，身份正号，电话，N（多个的话，以_分隔）
		StringBuilder passengerTicketStr = new StringBuilder();
		// 姓名，证件类别，证件号码，用户类型（多个的话直接拼接，最后有一个_）
		StringBuilder oldPassengerStr = new StringBuilder();
		for (Passenger passenger : passengerList) {
			passengerTicketStr.append(seatType.getSeatTypeCode())
							  .append(",0,1,")
							  .append(passenger.getPassengerName())
							  .append(",")
							  .append(passenger.getPassengerIdTypeCode())
							  .append(",")
							  .append(passenger.getPassengerIdNo())
							  .append(",")
							  .append(passenger.getMobileNo())
							  .append(",N_");
			
			oldPassengerStr.append(passenger.getPassengerName())
						   .append(",")
						   .append(passenger.getPassengerIdTypeCode())
						   .append(",")
						   .append(passenger.getPassengerIdNo())
						   .append(",")
						   .append(passenger.getPassengerType())
						   .append("_");
		}
		if (passengerTicketStr.length() <= 0)
			return false;
		else
			passengerTicketStr.deleteCharAt(passengerTicketStr.length() - 1);
		paramMap.put("passengerTicketStr", passengerTicketStr.toString());
		paramMap.put("oldPassengerStr", oldPassengerStr.toString());
		// 单程or往返（这里先默认单程）
		paramMap.put("tour_flag", "dc");
		// 验证码（不输入）
		paramMap.put("randCode", "");
		// 固定值
		paramMap.put("whatsSelect", "1");
		// 提交订单的token
		paramMap.put("_json_att", "");
		paramMap.put("REPEAT_SUBMIT_TOKEN", token);

		String response = HttpUtils.post(url, paramMap, cookie, ContentType.APPLICATION_JSON);
		
		try {
			JSONObject jsonObject = JSONObject.parseObject(response);
			if (!jsonObject.getBoolean("status")) {
				System.out.println(jsonObject.getString("messages"));
			}
			return jsonObject.getBooleanValue("status");
		} catch (JSONException e) {
			return false;
		}
	}
	
	public static boolean getQueueCount(CookieStore cookie, String dateString, TicketInfo ticket, SeatType seatType, Map<String, String> submitInfoMap) {
		String url = PublicInfo.GET_QUEUE_COUNT_URL;
		
		Map<String, String> paramMap = new LinkedHashMap<>();
		// 乘车时间（Tue Jan 29 2019 00:00:00 GMT+0800 (中国标准时间)）
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(sdf.parse(dateString));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		paramMap.put("train_date", calendar.getTime().toString());
		
		// 列车编号
		paramMap.put("train_no", ticket.getTrainNo());
		// 列车站点车次
		paramMap.put("stationTrainCode", ticket.getStationTrainCode());
		// 座位类型
		paramMap.put("seatType", seatType.getSeatTypeCode());
		// 出发站点编号
		paramMap.put("fromStationTelecode", ticket.getFromStationTelecode());
		// 目的站点标号
		paramMap.put("toStationTelecode", ticket.getToStationTelecode());
		// 从ticketInfoForPassengerForm取值
		JSONObject ticketInfoForPassengerForm = JSONObject.parseObject(submitInfoMap.get("ticketInfoForPassengerForm"));
		paramMap.put("leftTicket", ticketInfoForPassengerForm.getJSONObject("queryLeftTicketRequestDTO").getString("ypInfoDetail"));
		// TODO
		paramMap.put("purpose_codes", "00");
		// 列车位置
		paramMap.put("train_location", ticket.getLocationCode());
		// token
		paramMap.put("_json_att", "");
		paramMap.put("REPEAT_SUBMIT_TOKEN", submitInfoMap.get("submitToken"));

		String response = HttpUtils.post(url, paramMap, cookie, ContentType.APPLICATION_JSON);
		
		try {
			JSONObject jsonObject = JSONObject.parseObject(response);
			if (jsonObject.getBoolean("status")) {
				System.out.println("车次【" + ticket.getStationTrainCode() + "】"
								+ "余票【" + jsonObject.getJSONObject("data").getString("count") + "】"
								+ "当前排队【" + jsonObject.getJSONObject("data").getString("ticket") + "】");
				
				return !jsonObject.getJSONObject("data").getBooleanValue("op_2");
			} else
				System.out.println(jsonObject.getString("messages"));
			
			return jsonObject.getBoolean("status");
		} catch (JSONException e) {
			return false;
		}
	}

	public static boolean SubmitForQueue(TicketInfo ticket, CookieStore cookie, SeatType seatType, List<Passenger> passengerList, Map<String, String> submitInfoMap) {
		String url = PublicInfo.CONFIRM_FOR_QUEUE;
		StringBuilder dateString = new StringBuilder(ticket.getStartTrainDate());
		dateString.insert(6, "-");
		dateString.insert(4, "-");

		Map<String, String> paramMap = new LinkedHashMap<>();
		// 座位类型，0，车票类型(1 成人票)，姓名，身份正号，电话，N（多个的话，以_分隔）
		StringBuilder passengerTicketStr = new StringBuilder();
		// 姓名，证件类别，证件号码，用户类型（多个的话直接拼接，最后有一个_）
		StringBuilder oldPassengerStr = new StringBuilder();
		for (Passenger passenger : passengerList) {
			passengerTicketStr.append(seatType.getSeatTypeCode())
							  .append(",0,1,")
							  .append(passenger.getPassengerName())
							  .append(",")
							  .append(passenger.getPassengerIdTypeCode())
							  .append(",")
							  .append(passenger.getPassengerIdNo())
							  .append(",")
							  .append(passenger.getMobileNo())
							  .append(",N_");
			
			oldPassengerStr.append(passenger.getPassengerName())
						   .append(",")
						   .append(passenger.getPassengerIdTypeCode())
						   .append(",")
						   .append(passenger.getPassengerIdNo())
						   .append(",")
						   .append(passenger.getPassengerType())
						   .append("_");
		}
		if (passengerTicketStr.length() <= 0)
			return false;
		else
			passengerTicketStr.deleteCharAt(passengerTicketStr.length() - 1);
		paramMap.put("passengerTicketStr", passengerTicketStr.toString());
		paramMap.put("oldPassengerStr", oldPassengerStr.toString());
		paramMap.put("randCode", "");
		paramMap.put("purpose_codes", "ADULT");
		
		JSONObject ticketInfoForPassengerForm = JSONObject.parseObject(submitInfoMap.get("ticketInfoForPassengerForm"));
		paramMap.put("key_check_isChange", ticketInfoForPassengerForm.getString("key_check_isChange"));
		paramMap.put("leftTicketStr", ticketInfoForPassengerForm.getString("leftTicketStr"));
		paramMap.put("train_location", ticketInfoForPassengerForm.getString("train_location"));
		paramMap.put("choose_seats", "");
		paramMap.put("seatDetailType", "000");
		paramMap.put("whatsSelect", "0");
		
		String response = HttpUtils.post(url, paramMap, cookie, ContentType.APPLICATION_JSON);
		if (null == response || response.isEmpty())
			return false;
		
		try {
			JSONObject responseJson = JSONObject.parseObject(response);
			if (responseJson.getBooleanValue("status")) {
				if (!responseJson.getJSONObject("data").getBooleanValue("submitStatus")) {
					System.out.println(responseJson.getJSONObject("data").getString("errMsg"));
					return false;
				} else
					return true;
			} else {
				System.out.println("出票失败");
				return false;
			}
				
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean waitForOrder(CookieStore cookie) {
		String url  = PublicInfo.WAIT_ORDER_URL + (new Date()).getTime() + "&tourFlag=dc";
		
		String response = HttpUtils.get(url, cookie, null);
		if (null == response || response.isEmpty())
			return false;
		
		try {
			JSONObject responseJson = JSONObject.parseObject(response);
			if (responseJson.getJSONObject("data").getBooleanValue("queryOrderWaitTimeStatus")) {
				System.out.println(response);
				return false;
			} else
				return true;
				
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}
}
