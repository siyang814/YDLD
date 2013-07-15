package com.framework.app;
import java.lang.reflect.Type;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import com.framework.Exception.MessageException;
import com.google.gson.Gson;


/**
 * json ���ؽ���İ�����
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
	 * ���캯��
	 * 
	 * @param jsonString
	 *            json��ʽ���ַ���
	 * @throws JSONException
	 *             json�쳣
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
	 * ���ؽ���Ƿ�����
	 * 
	 * @return ��������true�����򷵻�false
	 */
	public Boolean isOK() {
		return Status.equals(STATUS_IS_OK);
	}

	/**
	 * ����key�����ַ�����ֵ
	 * 
	 * @param key
	 *            �ؼ���
	 * @return �����ַ�����ֵ
	 * @throws JSONException
	 *             json�쳣
	 * @throws MessageException
	 *             ҵ���쳣
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
	 * ����key����ָ�����͵�ʵ��
	 * 
	 * @param <T>
	 *            ָ�����͵�ʵ������
	 * @param key
	 *            �ؼ���
	 * @param classOfT
	 *            ָ�����͵Ķ���
	 * @return ����ָ�����͵�ʵ��
	 * @throws JSONException
	 *             json�쳣
	 * @throws MessageException
	 *             ҵ���쳣
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
	 * ����key����ָ�����͵�ʵ��
	 * 
	 * @param <T>
	 *            ָ�����͵�ʵ������
	 * @param key
	 *            �ؼ���
	 * @param type
	 *            ָ�����͵Ķ���
	 * @return ����ָ�����͵�ʵ��
	 * @throws JSONException
	 *             json�쳣
	 * @throws MessageException
	 *             ҵ���쳣
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
	 * ����key����ָ�����͵�ʵ��
	 * 
	 * @param <T>
	 *            ָ�����͵�ʵ������
	 * @param classOfT
	 *            ָ�����͵Ķ���
	 * @return ����ָ�����͵�ʵ��
	 * @throws JSONException
	 *             json�쳣
	 * @throws MessageException
	 *             ҵ���쳣
	 */
	public <T> T getData(Class<T> classOfT) throws JSONException, MessageException {
		if (!isOK()) {
			throw new MessageException(Data);
		}

		Gson gson = new Gson();
		return gson.fromJson(Data, classOfT);
	}

	/**
	 * ��ԭԭ���ݵĵ�ת���ַ�
	 * 
	 * @param input
	 *            ���������
	 * @return ��ԭ����ַ�
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
