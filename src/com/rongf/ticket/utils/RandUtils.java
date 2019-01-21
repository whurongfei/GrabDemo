package com.rongf.ticket.utils;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.client.CookieStore;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class RandUtils {
	public static String getRandCode(CookieStore cookie) {
		String url = PublicInfo.GET_RAND_URL + (new Date()).getTime();
		String response = HttpUtils.get(url, cookie, null);
		System.out.println(response);
		
		String path = RandUtils.class.getResource("/").getPath() + "rand.jpg";
		JSONObject responseJson = JSONObject.parseObject(response);
		return ImageUtils.deCodeImage(responseJson.getString("image"), path);
	}
	
	public static boolean randCheck(String randCode, CookieStore cookie) {
		Map<String, String> paramMap = new LinkedHashMap<>();
		paramMap.put("answer", randCode);
		paramMap.put("rand", "sjrand");
		paramMap.put("login_site", "E");
		
		String url = PublicInfo.RAND_CHECK_URL;
		StringBuilder paramString = new StringBuilder();
		for (String key : paramMap.keySet())
			paramString.append(key + "=" + paramMap.get(key) + "&");
		if (paramString.length() > 0) 
			url += "?" + paramString.substring(0, paramString.length() - 1);
		
		String response = HttpUtils.get(url, cookie, null);
		
		if (null == response)
			return false;
		
		
		JSONObject responseJson = null;
		
		try {
			responseJson = JSONObject.parseObject(response);
			return responseJson.getInteger("result_code") == 4;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}
}
