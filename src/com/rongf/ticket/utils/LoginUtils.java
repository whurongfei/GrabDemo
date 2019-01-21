package com.rongf.ticket.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.client.CookieStore;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCookieStore;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class LoginUtils {
	public static CookieStore cookie = new BasicCookieStore();
	
	public static boolean login(String username, String password) {
		String url = PublicInfo.LOGIN_URL;
		
		String randCode = null;
		boolean randCheck = false;
		while (!randCheck) {
			randCode = RandUtils.getRandCode(cookie);
			if ("stop".equals(randCode))
				return false;
			
			randCheck = RandUtils.randCheck(randCode, cookie);
			
		}
		
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("username", username);
		paramMap.put("password", password);
		paramMap.put("appid", "otn");
		paramMap.put("answer", randCode);
		
		String response = HttpUtils.post(url, paramMap, cookie, HttpUtils.FORM_TYPE_UTF8);
		if (null == response || response.isEmpty())
			return false;
		
		JSONObject responseJson = null;
		try {
			responseJson = JSONObject.parseObject(response);
			if (responseJson.getInteger("result_code") == 0) {
				return initLogin(cookie);
			} else
				return false;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public static boolean asyLogin(String username, String password) {
		String url = PublicInfo.ASYLOGIN_URL;
		
		String randCode = null;
		boolean randCheck = false;
		while (!randCheck) {
			randCode = RandUtils.getRandCode(cookie);
			if ("stop".equals(randCode))
				return false;
			
			randCheck = RandUtils.randCheck(randCode, cookie);
			
		}
		
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("loginUserDTO.user_name", username);
		paramMap.put("userDTO.password", password);
		paramMap.put("randCode", randCode);
		
		String response = HttpUtils.post(url, paramMap, cookie, HttpUtils.FORM_TYPE_UTF8);
		if (null == response || response.isEmpty())
			return false;
		
		JSONObject responseJson = null;
		try {
			responseJson = JSONObject.parseObject(response);
			if (responseJson.getBooleanValue("status")) {
				initLogin(cookie);
				return true;
			} else
				return false;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private static boolean initLogin(CookieStore cookie) {
		String url = PublicInfo.USER_LOGIN_URL;
		HttpUtils.get(url, cookie, null);
		
		url = PublicInfo.UAMTK_URL;
		Map<String, String> paramMap = new LinkedHashMap<>();
		paramMap.put("appid", "otn");
		
		String response =  HttpUtils.post(url, paramMap, cookie, ContentType.APPLICATION_JSON);
		if (null == response || response.isEmpty())
			return false;
		
		JSONObject responseJson = null;
		String tk = null;
		try {
			responseJson = JSONObject.parseObject(response);
			if (responseJson.getInteger("result_code") == 0) {
				tk = responseJson.getString("newapptk");
				
				if (null == tk || tk.isEmpty())
					tk = responseJson.getString("apptk");
			} else
				return false;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		
		url = PublicInfo.UAMAUTHCLIENT_URL;
		paramMap = new LinkedHashMap<>();
		paramMap.put("tk", tk);
		
		response =  HttpUtils.post(url, paramMap, cookie, ContentType.APPLICATION_JSON);
		if (null == response || response.isEmpty())
			return false;
		
		responseJson = null;
		try {
			responseJson = JSONObject.parseObject(response);
			if (responseJson.getInteger("result_code") != 0)
				return false;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		
		url = PublicInfo.USER_LOGIN_URL;
		HttpUtils.get(url, cookie, null);
		
		url = PublicInfo.CONF_URL;
		HttpUtils.post(url, new HashMap<>(), cookie, ContentType.APPLICATION_JSON);
		
		url = PublicInfo.INIT_API_URL;
		HttpUtils.post(url, new HashMap<>(), cookie, ContentType.APPLICATION_JSON);

		return true;
	}

	public static boolean checkUser(CookieStore cookie) {
		String url = PublicInfo.CHECK_USER_URL;
		Map<String, String> paramMap = new LinkedHashMap<>();
		paramMap.put("_json_att", "");
		
		String response = HttpUtils.post(url, paramMap, cookie, HttpUtils.FORM_TYPE_UTF8);
		
		if (null == response || response.isEmpty())
			return false;
		
		JSONObject responseJson = null;
		try {
			responseJson = JSONObject.parseObject(response);
			return responseJson.getBooleanValue("status");
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}
}
