package com.greentech.wnd.android.util;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class GsonUtil {

	private static final GsonBuilder gsonb = new GsonBuilder();//只保持单例gsonb
	private static final Gson gson;//只保持单例gson
	private static final JsonParser jsonPaser = new JsonParser();//只保持单例JsonParser
	
	static {
		//给日期设置适配器，解决日期格式json反序列化成对象时的转化错误。Date只能是java.sql.Date类，而且传入的json字符串,日期字段还不能为空
		gsonb.setDateFormat("yyyy-MM-dd HH:mm:ss").registerTypeAdapter(Date.class, new CustomDateDeserializer());
		gson = gsonb.create();
	}
	
	private GsonUtil() {
		
	}

	/**
	 * 暂时不提供GsonBuilder，需要的时候再公开
	 * @return
	 */
	private static GsonBuilder getGsonBuilder() {
		return gsonb;
	}
	
	/**
	 * 根据传入的json字符串和类，将json转化为指定的对象。
	 * @param json
	 * @param c
	 * @return
	 * @throws JsonIOException 可能出现json解析异常，该异常属于运行时异常
	 */
	public static <T> T fromJson(String json, Type t) throws JsonIOException, JsonParseException {
		return gson.fromJson(json, t);
	}
	
	/**
	 * 根据传入的json和类，将json转化为指定的对象。
	 * @param json
	 * @param c
	 * @return
	 * @throws JsonIOException 可能出现json解析异常，该异常属于运行时异常
	 */
	public static <T> T fromJson(JsonElement json, Type t) throws JsonIOException, JsonParseException {
		return gson.fromJson(json, t);
	}
	
	/**
	 *将传入字符串解析成json
	 * @param json
	 * @return
	 */
	public static JsonElement parse(String json) {
		return jsonPaser.parse(json);
	}
	
	/**
	 * 自定义日期类型json反序列化类，默认的jsonbuilder就算设置了dateformat，指定的数据格式没问题了，但是当传入的日期字符串时空值时，还是报错
	 * @author wlj
	 *
	 */
	public static class CustomDateDeserializer implements JsonDeserializer<Date> {

		@Override
		public Date deserialize(JsonElement arg0, Type arg1,
				JsonDeserializationContext arg2) throws JsonParseException {
			
			if(arg0.getAsString() == null || arg0.getAsString().equals("")) {
				return null;
			} else {
				try {
					return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(arg0.getAsString());
				} catch (ParseException e) {
					return null;
				}
			}
		}

	}
}
