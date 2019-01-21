package com.rongf.ticket.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.ConnectionClosedException;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpUtils {
	public final static ContentType FORM_TYPE_UTF8 = ContentType.create(ContentType.APPLICATION_FORM_URLENCODED.getMimeType(), Consts.UTF_8);
	
	/**
	 * 发送post请求（不带cookie）
	 * 
	 * @param url：地址
	 * @param paramMap：参数列表
	 * @return
	 */
	public static String post(String url, Map<String, String> paramMap, ContentType contentType) {
		return post(url, paramMap, null, contentType);
	}
	
	/**
     * 发送post请求(带cookie)
     * 
     * @param url：地址
     * @param paramString：参数
     * @param cookie：请求cookie
     * @return
     */
    public static String post(String url, String paramString, CookieStore cookie, ContentType contentType) {
    	// 创建默认的httpClient实例. 
    	CloseableHttpClient httpclient = null;
        if (null != cookie)
        	httpclient = HttpClients.custom().setDefaultCookieStore(LoginUtils.cookie).build();
        else
        	httpclient = HttpClients.createDefault();
        
        // 创建httppost  
        HttpPost httppost = new HttpPost(url);
        // 设置以交互方式
//        if (null != contentType)
//        	httppost.setHeader("Content-Type", contentType.toString());
        // 创建参数队列  
        StringEntity stringParam = null;
		stringParam = new StringEntity(paramString, contentType);
        String result = null;
        try {
        	httppost.setEntity(stringParam);
            System.out.println("executing request " + httppost.getURI());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    System.out.println("--------------------------------------");
                    result = EntityUtils.toString(entity, "UTF-8");
                    System.out.println("Response content: " + result);
                    System.out.println("--------------------------------------");
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源  
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return result;
    }
    
    /**
     * 发送post请求(带cookie)
     * 
     * @param url：地址
     * @param paramMap：参数列表
     * @param cookie：请求cookie
     * @return
     */
    public static String post(String url, Map<String, String> paramMap, CookieStore cookie, ContentType contentType) {
        // 创建默认的httpClient实例. 
    	CloseableHttpClient httpclient = null;
    	if (null != cookie)
        	httpclient = HttpClients.custom().setDefaultCookieStore(LoginUtils.cookie).build();
        else
        	httpclient = HttpClients.createDefault();
        
        // 创建httppost  
        HttpPost httppost = new HttpPost(url);
        // 设置以json方式交互
//        if (null != contentType)
//        	httppost.setHeader("Content-Type", contentType.toString());
        httppost.setConfig(RequestConfig.custom().setRedirectsEnabled(false).build());
        // 创建参数队列  
        List<NameValuePair> formparams = new ArrayList<>();
        for (String key : paramMap.keySet())
        	formparams.add(new BasicNameValuePair(key, paramMap.get(key)));
        
        UrlEncodedFormEntity uefEntity;
        String result = null;
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
            httppost.setEntity(uefEntity);
//            System.out.println("executing request " + httppost.getURI());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
            	if (response.getStatusLine().getStatusCode() == 302) {
            		Header location = response.getFirstHeader("Location");
            		result = post(location.getValue(), paramMap, cookie, contentType);
            	} else {
            		HttpEntity entity = response.getEntity();
            		if (entity != null) {
//            			System.out.println("--------------------------------------");
            			result = EntityUtils.toString(entity, Consts.UTF_8);
//            			System.out.println("Response content: " + result);
//            			System.out.println("--------------------------------------");
            		}
            	}
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源  
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return result;
    }

    /**
     * 发送get请求(不带cookie）
     * 
     * @param url：地址
     * @return
     */
    public static String get(String url, ContentType contentType) {
    	return get(url, null, contentType);
    }
    
    /**
     * 发送get请求(带cookie）
     * 
     * @param url：地址
     * @param cookie：cookie
     * @return
     */
    public static String get(String url, CookieStore cookie, ContentType contentType) {
    	CloseableHttpClient httpclient = null;
    	if (null != cookie)
        	httpclient = HttpClients.custom().setDefaultCookieStore(LoginUtils.cookie).build();
        else
        	httpclient = HttpClients.createDefault();
    	
    	String result = null;
    	try {
            // 创建httpget.  
            HttpGet httpget = new HttpGet(url);
            // 设置交互方式
//            if (null != contentType)
//            	httpget.setHeader("Content-Type", contentType.toString());
            httpget.setConfig(RequestConfig.custom().setRedirectsEnabled(false).build());
            // 执行get请求.  
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
            	if (response.getStatusLine().getStatusCode() == 302) {
            		Header location = response.getFirstHeader("Location");
            		result = get(location.getValue(), cookie, contentType);
            	} else {
            		// 获取响应实体  
            		HttpEntity entity = response.getEntity();
//                System.out.println("--------------------------------------");
            		// 打印响应状态  
//                System.out.println(response.getStatusLine());
            		if (entity != null) {
            			// 打印响应内容长度  
//                    System.out.println("Response content length: " + entity.getContentLength());
            			// 打印响应内容  
            			result = EntityUtils.toString(entity, Consts.UTF_8);
//                    System.out.println("Response content: " + result);
            		}
//                System.out.println("------------------------------------");
            	}
            } catch (ConnectionClosedException e) {
            	e.printStackTrace();
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源  
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return result;
    }

    /**
     * 上传文件
     */
    public static long upload(String url, File file) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        long result = 0;
        try {
            HttpPost httppost = new HttpPost(url);

            FileBody bin = new FileBody(file);
            StringBody comment = new StringBody("A binary file of some kind", ContentType.TEXT_PLAIN);

            HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("bin", bin).addPart("comment", comment).build();

            httppost.setEntity(reqEntity);

            System.out.println("executing request " + httppost.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                	result = resEntity.getContentLength();
                    System.out.println("Response content length: " + result);
                }
                EntityUtils.consume(resEntity);
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return result;
    }
}