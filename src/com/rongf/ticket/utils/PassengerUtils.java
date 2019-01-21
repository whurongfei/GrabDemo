package com.rongf.ticket.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.CookieStore;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.rongf.ticket.entity.Passenger;

public class PassengerUtils {
	public static List<Passenger> getPassengers(String token, CookieStore cookie) {
		String url = PublicInfo.GET_PASSENGER_URL;
		
		Map<String, String> paramMap = new LinkedHashMap<>();
		paramMap.put("_json_att", "");
		paramMap.put("REPEAT_SUBMIT_TOKEN", token);
		
		String response = HttpUtils.post(url, paramMap, cookie, null);
		
		JSONObject responseJson = null;
		try {
			responseJson = JSONObject.parseObject(response);
		} catch (JSONException e) {
			System.out.println("error url: " + url);
			e.printStackTrace();
		}
		
		if (null == responseJson)
			return new ArrayList<>();
		
		if (responseJson.getBooleanValue("status")) {
			JSONArray passengerArray =  responseJson.getJSONObject("data").getJSONArray("normal_passengers");
			return Passenger.buildPassengerFromJsonArray(passengerArray);
		}
		
		return new ArrayList<>();
	}
	
	public static Map<String, String> getSubmitInfo(CookieStore cookie) {
		Map<String, String> result = new HashMap<>();
		
		String url = PublicInfo.ORDER_INIT_URL;
		Map<String, String> paramMap = new HashMap<>();
//		paramMap.put("_json_att", "");
		String response =  HttpUtils.post(url, paramMap, cookie, HttpUtils.FORM_TYPE_UTF8);
		if (null == response || response.isEmpty())
			return result;
		
		Pattern pattern = Pattern.compile("globalRepeatSubmitToken = \'(\\w+)\';");
		Matcher matcher = pattern.matcher(response);
		if (matcher.find()) 
			result.put("submitToken", matcher.group(1));
		else
			return null;
		
		Pattern pattern2 = Pattern.compile("ticketInfoForPassengerForm=\\{(.+)\\};");
		Matcher matcher2 = pattern2.matcher(response);
		
		if (matcher2.find()) {
			String ticketInfoForPassengerForm = matcher2.group(1).replaceAll("\'", "\"");
			result.put("ticketInfoForPassengerForm", "{" + ticketInfoForPassengerForm + "}");
		}
		else
			return null;
		
		return result;
	}
}
