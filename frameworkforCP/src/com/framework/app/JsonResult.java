package com.framework.app;
import java.lang.reflect.Type;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import com.framework.Exception.MessageException;
import com.google.gson.Gson;


/**
 * json 返回结果的帮助类
 * 
 * @author 
 * 
 */
public class JsonResult {

	private static final String BLANK_STRING = "";
	private static final String STATUS_IS_OK = "1";
	public String Status = "0";
	public String Data;
	public String Detail;
	public String JsonString;

	/**
	 * 构造函数
	 * 
	 * @param jsonString
	 *            json格式的字符串
	 * @throws JSONException
	 *             json异常
	 */
	public JsonResult(String jsonString) throws JSONException {
		JSONObject jobj = new JSONObject(formatString(jsonString));
		JsonString = jsonString;
		Status = jobj.getString("status");
		Data = jobj.getString("data");
		if (jobj.has("detail")) {
			Detail = jobj.getString("detail");
		}
	}

	/**
	 * 返回结果是否正常
	 * 
	 * @return 正常返回true，否则返回false
	 */
	public Boolean isOK() {
		return Status.equals(STATUS_IS_OK);
	}

	/**
	 * 根据key或者字符串的值
	 * 
	 * @param key
	 *            关键字
	 * @return 返回字符串的值
	 * @throws JSONException
	 *             json异常
	 * @throws MessageException
	 *             业务异常
	 */
	public String getDataString(String key) throws JSONException, MessageException {
		if (!isOK()) {
			throw new MessageException(Data);
		}

		JSONObject jobj = new JSONObject(Data);
		String returnString = jobj.optString(key);

		return returnString;
	}

	/**
	 * 根据key返回指定类型的实例
	 * 
	 * @param <T>
	 *            指定类型的实例定义
	 * @param key
	 *            关键字
	 * @param classOfT
	 *            指定类型的定义
	 * @return 返回指定类型的实例
	 * @throws JSONException
	 *             json异常
	 * @throws MessageException
	 *             业务异常
	 */
	public <T> T getData(String key, Class<T> classOfT) throws JSONException, MessageException {
		if (!isOK()) {
			throw new MessageException(Data);
		}

		JSONObject jobj = new JSONObject(Data);
		String returnString = jobj.optString(key);
		if (returnString == null || returnString.trim().equals(BLANK_STRING)) {
			return null;
		}

		Gson gson = new Gson();
		return gson.fromJson(returnString, classOfT);
	}

	/**
	 * 根据key返回指定类型的实例
	 * 
	 * @param <T>
	 *            指定类型的实例定义
	 * @param key
	 *            关键字
	 * @param type
	 *            指定类型的定义
	 * @return 返回指定类型的实例
	 * @throws JSONException
	 *             json异常
	 * @throws MessageException
	 *             业务异常
	 */
	public <T> T getData(String key, Type type) throws JSONException, MessageException {
		if (!isOK()) {
			throw new MessageException(Data);
		}

		JSONObject jobj = new JSONObject(Data);
		String returnString = jobj.optString(key);
		if (returnString == null || returnString.trim().equals(BLANK_STRING)) {
			return null;
		}

		Gson gson = new Gson();
		return gson.fromJson(returnString, type);
	}

	/**
	 * 根据key返回指定类型的实例
	 * 
	 * @param <T>
	 *            指定类型的实例定义
	 * @param classOfT
	 *            指定类型的定义
	 * @return 返回指定类型的实例
	 * @throws JSONException
	 *             json异常
	 * @throws MessageException
	 *             业务异常
	 */
	public <T> T getData(Class<T> classOfT) throws JSONException, MessageException {
		if (!isOK()) {
			throw new MessageException(Data);
		}

		Gson gson = new Gson();
		return gson.fromJson(Data, classOfT);
	}

	/**
	 * 还原原数据的的转义字符
	 * 
	 * @param input
	 *            输入的数据
	 * @return 还原后的字符
	 */
	private String formatString(String input) {
		String result = null;
		if (null != input && !"".equals(input.trim())) {
			result = input.replace("\\\\", "\\");
			result = result.replace("\\\"", "\"");
		}

		return result;
	}
}
