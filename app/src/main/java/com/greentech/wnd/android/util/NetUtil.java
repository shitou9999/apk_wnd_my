package com.greentech.wnd.android.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.util.Log;

/**
 * 网络操作工具类
 * @author O-J-S
 * @version 1.0
 * @since 2012-8-19
 */
public class NetUtil {

	/**
	 * 根据主机名获取IP地址
	 * @param hostName 主机名
	 * @return IP地址
	 */
	public static String getIPByHostName(String hostName)
	{
		String ip = null;

		InetAddress address = null;
		try
		{
			address = InetAddress.getByName(hostName);
			ip = address.getHostAddress();
		}catch(UnknownHostException e)
		{
			ip = null;
			e.printStackTrace();
		}

		return ip;
	}
	
	/**
	 * 提交http post请求并得到相应结果
	 * @param url
	 * @param param
	 * @param cookie
	 * @return
	 */
	public static InputStream post(String url, String param, String cookie) {
		try {
            URL u = new URL(url);
            HttpURLConnection con = (HttpURLConnection)u.openConnection();
            con.setRequestProperty("Cookie", cookie);
            con.setRequestMethod("POST");
            con.setReadTimeout(30000);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestProperty("Pragma:", "no-cache");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:26.0) Gecko/20100101 Firefox/26.0");//注意：模拟http请求这个参数一定要加，不然有的网站有问题，请求不到
            //如果设置为text/xml，那么post模拟表单提交，write的参数后台获取不到，但是设置为multipart/form-data的话，普通的请求和带post参数的请求都出404错误，如findPageSupplyDemand.action出404错误。form表单默认提交的enctype为application/x-www-form-urlencoded，设置成application/x-www-form-urlencoded跟不设置application/x-www-form-urlencoded是一样的效果，普通的请求和post请求（write写的参数后台能接收到）都没问题。
//            con.setRequestProperty("Content-Type", "multipart/form-data");


//            OutputStreamWriter out = new OutputStreamWriter(con
//                    .getOutputStream(),"utf-8");
//            out.write((param==null?"":param));
//            out.write((param==null?"":param));
//            out.flush();
//            out.close();
            //由于输入被设置为true，所以服务器会去检查输入流的长度，如果不输入任何东西，服务器会返回状态码HTTP_LENGTH_REQUIRED
            if(param==null||param.equals("")) {
            	param = "";
            }
        	OutputStream os = con.getOutputStream();
            os.write(param.getBytes());
            os.flush();
            os.close();
            int responseCode = con.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK) {
                return con.getInputStream();
            }
        } catch (MalformedURLException e) {
        	Log.d("111", "222", e);
        } catch (IOException e) {
        	Log.d("111", "222", e);
        } catch (Exception e) {
        	Log.d("111", "222", e);
        }
		return null;
	}

	
	/**
	 * 提交http post请求并得到相应结果
	 * @param url
	 * @param param
	 * @return
	 */
	public static InputStream post(String url, String param) {
		return post(url, param, "");//第三个参数不能传入null，会让java判断不出想调用的是post(String url, String param, File file)还是post(String url, String param, String cookie)
	}

	
	/**
	 * 提交http post请求并得到相应结果
	 * @return
	 */
	public static InputStream post(String url) {
		return post(url, null, "");
	}

	
	/**
	 * 提交http post请求并得到相应结果
	 * 带文件提交，提交请求的Content-type为multipart/form-data
	 * 如果是直接传入文件的InputStream作为参数提交的话，一定要上传一个key为InputStream的key加上FileName作为参数提交。比如上传一个key为img的InputStream，那么一定要同时上传一个kye为imgFileName的值。
	 * @param url
	 * @param cookie
	 * @param map Map<String, String>或者Map<String, File>
	 * @return
	 */
	public static InputStream post(String url, String cookie, Map map) {
		String br = "\r\n";
		String end = "--";
		String boundaryStr = "testboundary";
		String boundary = "--" + boundaryStr + br;
        StringBuffer sb = new StringBuffer();
		try {
            URL u = new URL(url);
            HttpURLConnection con = (HttpURLConnection)u.openConnection();
            if(cookie != null && cookie != "") {
                con.setRequestProperty("Cookie", cookie);
            }
            con.setRequestMethod("POST");
            con.setReadTimeout(30000);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestProperty("Pragma:", "no-cache");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundaryStr);
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:26.0) Gecko/20100101 Firefox/26.0");//注意：模拟http请求这个参数一定要加，不然有的网站有问题，请求不到
            
            OutputStream os = con.getOutputStream();
            
            String t = "";
            
            if(!map.isEmpty()) {
            	Iterator<String> it = map.keySet().iterator();
            	while(it.hasNext()) {
            		String key = it.next();
            		t += key;
            		Object o = map.get(key);
            		if(o instanceof File) {
            			//如果文件不存在，则跳过
            			if(!((File) o).exists()) {
            				continue;
            			}

                    	sb.append(boundary);
                    	//为了文件流的分段传输，这里就不直接一次性写入sb了。
                    	os.write(boundary.getBytes());
            			//上传文件
            			String str1 = "Content-Disposition: form-data;name=\""+ key + "\";filename=\"" + ((File)o).getPath() + "\""  + br+ "Content-Type:application/octet-stream" + br;
            			sb.append(str1)
	        				.append(br)
//	        				.append(o.toString())
	        				.append(br);
            			os.write((str1+br).getBytes());
            			//分段传输文件流
            			byte[] buffer = new byte[8192];
            			int count = 0;
            			FileInputStream fis = new FileInputStream((File) o);
            			while((count=fis.read(buffer)) != -1){
            				os.write(buffer, 0, count);
            			}
            			fis.close();
            			os.write((br).getBytes());
            		}else if(o instanceof InputStream) {
            			sb.append(boundary);
                    	//为了文件流的分段传输，这里就不直接一次性写入sb了。
                    	os.write(boundary.getBytes());
            			//上传文件
            			String str1 = "Content-Disposition: form-data;name=\""+ key + "\";filename=\"" + map.get(key+"FileName") + "\""  + br+ "Content-Type:application/octet-stream" + br;
            			sb.append(str1)
	        				.append(br)
//	        				.append(o.toString())
	        				.append(br);
            			os.write((str1+br).getBytes());
            			//分段传输文件流
            			byte[] buffer = new byte[8192];
            			int count = 0;
            			InputStream is = (InputStream)o;
            			while((count=is.read(buffer)) != -1){
            				os.write(buffer, 0, count);
            			}
            			is.close();
            			os.write((br).getBytes());
            		} else {
            			//字符串
            			//value和name=""之间必须有一行\r\n，不然会出404错误。
            			//****http请求正文内容如下****//
//            			--testboundary
//            			Content-Disposition: form-data;name="supplyDemand.name"
//
//            			asdf
//            			--testboundary
//            			Content-Disposition: form-data;name="tt"
//
//            			111
//            			--testboundary
//            			Content-Disposition: form-data;name="file1";filename="/storage/sdcard0/DCIM/Camera/IMG_20140125_165733.jpg"
//            			Content-Type:application/octet-stream
//
//						文件的字节流
//            			--testboundary--
            			//****http请求正文内容如上****//
            			
            			String str1 = "Content-Disposition: form-data;name=\""+ key + "\"" + br;
            			sb.append(boundary)
            				.append(str1)
	        				.append(br)
	        				.append(o.toString())
	        				.append(br);
            			os.write((boundary+str1+br+o.toString()+br).getBytes());
            		}
            	}
            	if(sb.length() > 0) {
                	sb.append(boundary.replace(br, "")+end);
                	os.write((boundary.replace(br, "")+end).getBytes());
                	os.flush();
            	}
            }
            os.close();
            int responseCode = con.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK) {
                return con.getInputStream();//如果请求对应的server后台出错了，那么是没有相应的Stream返回的，比如说后台action方法出Exception，页面出404等错误。
            }
        } catch (MalformedURLException e) {
        	Log.d("111", "222", e);
        } catch (IOException e) {
        	Log.d("111", "222", e);
        } catch (Exception e) {
        	Log.d("111", "222", e);
        }
		return null;
	}

	
	/**
	 * 提交http post请求并得到相应结果
	 * 带文件提交，提交请求的Content-type为multipart/form-data
	 * @param url
	 * @param map Map<String, String>或者Map<String, File>
	 * @return
	 */
	public static InputStream post(String url, Map map) {
		return post(url, null, map);
	}
	

	/**
	 * 提交http get请求并得到相应结果
	 * @param url
	 * @param param
	 * @return
	 */
	public static InputStream get(String url, String param) {
		return get(url, param, "");//第三个参数不能传入null，会让java判断不出想调用的是post(String url, String param, File file)还是post(String url, String param, String cookie)
	}

	
	/**
	 * 提交http get请求并得到相应结果
	 * @return
	 */
	public static InputStream get(String url) {
		return get(url, null, "");
	}
	
	
	/**
	 * 提交http get请求并得到相应结果
	 * @param url
	 * @param param
	 * @param cookie
	 * @return
	 */
	public static InputStream get(String url, String param, String cookie) {
		try {
            URL u = new URL(url + "?" + param);
            HttpURLConnection con = (HttpURLConnection)u.openConnection();
            con.setRequestProperty("Cookie", cookie);
            con.setRequestMethod("GET");
            con.setReadTimeout(30000);
            con.setDoInput(true);
            con.setRequestProperty("Pragma:", "no-cache");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:26.0) Gecko/20100101 Firefox/26.0");//注意：模拟http请求这个参数一定要加，不然有的网站有问题，请求不到

            int responseCode = con.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK) {
                return con.getInputStream();
            } else {
            	Log.wtf("", getStringFromInputStream(con.getErrorStream(), "gb2312"));
            }
        } catch (MalformedURLException e) {
        	Log.d("111", "222", e);
        } catch (IOException e) {
        	Log.d("111", "222", e);
        } catch (Exception e) {
        	Log.d("111", "222", e);
        }
		return null;
	}
	
	/**
	 * 解析输入流，返回Object对象
	 * is中需包含被序列化过的对象，否则用此方法必定返回null
	 * @param InputStream is
	 * @return Object
	 */
	public static Object getObjectFromInputStream(InputStream is) {
		try
		{
			ObjectInputStream ois = new ObjectInputStream(is);//is中需包含被序列化过的对象。StreamCorruptedException  if the source stream does not contain serialized objects that can be read 
			Object obj = ois.readObject();
			if(ois != null)
			{
				ois.close();
			}
			return obj;
		}catch(Exception e)
		{
        	Log.d("111", "222", e);
		}
		return null;
	}
	
	/**
	 * 解析输入流，返回String对象
	 * @param InputStream is
	 * @return Object
	 */
	public static String getStringFromInputStream(InputStream is, String charset) {
		try
		{
			BufferedReader br;
			if(charset != null) {
				br = new BufferedReader(new InputStreamReader(is, charset));
			} else {
				br = new BufferedReader(new InputStreamReader(is));
			}
			String str = "";
			StringBuffer sb = new StringBuffer();
			do{
				str=br.readLine();
				if(str!=null) {
					sb.append(str);
				}
			}while(str!=null);
			br.close();
			is.close();
			return sb.toString();
		}catch(Exception e)
		{
        	Log.d("111", "222", e);
		}
		return null;
	}
	
	/**
	 * 解析输入流，返回String对象
	 * @param InputStream is
	 * @return Object
	 */
	public static String getStringFromInputStream(InputStream is) {
		return getStringFromInputStream(is, null);
	}
}
