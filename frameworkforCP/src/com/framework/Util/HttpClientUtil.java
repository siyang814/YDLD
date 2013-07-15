package com.framework.Util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import com.framework.R;
import com.framework.Exception.EvtLog;
import com.framework.Exception.MessageException;
import com.framework.Exception.NetworkException;
import com.framework.app.BottomTab;
import com.framework.app.JsonResult;


/**
 * 网络访问辅助类
 * 
 * @author wang.xy
 * 
 */
/**
 * @author 
 * 
 */
public class HttpClientUtil {
	private static final String TAG = "HttpClientUtil";
	private static final int TIMEOUT_SHORT_IN_MS = 5000;
	private static final int TIMEOUT_MIDDLE_IN_MS = 10000;
	private static String CHARSET = "UTF-8";
	private static int BUFFERSIZE = 4096;
	private static final String INTERROGATION = "?";
	private static final String AJAX_APPEND_HEADER = "ajax";

	/**
	 * 登录成功后的Cookie信息
	 */
	private static CookieStore COOKIE_STORE;

	/**
	 * 
	 * @param cookieStore
	 *            存储coockie
	 */
	public static void setCookieStore(CookieStore cookieStore) {
		COOKIE_STORE = cookieStore;
	}

	/**
	 * 通过post方式，跟服务器进行数据交互。该方法已经进行了网络检查
	 * 
	 * @param url
	 *            url地址
	 * @param httpParams
	 *            http参数
	 * @param postParams
	 *            参数
	 * @return json数据
	 * @throws ClientProtocolException
	 *             协议异常
	 * @throws IOException
	 *             IO流异常
	 * @throws JSONException
	 *             json数据异常
	 * @throws NetworkException
	 *             网络异常
	 * @throws MessageException
	 * @throws ParseException
	 */
	public static JsonResult post(String url, HttpParams httpParams, List<NameValuePair> postParams)
			throws NetworkException, MessageException {
		EvtLog.d(TAG, "post begin, " + url);
		if (!NetUtil.isNetworkAvailable()) {
			throw new NetworkException(PackageUtil.getString(R.string.network_is_not_available));
		}

		JsonResult jsonResult = null;
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader(AJAX_APPEND_HEADER, "true");
		if (httpParams != null) {
			httpPost.setParams(httpParams);
		}

		if (postParams != null && postParams.size() > 0) {
			if (EvtLog.isDebugLoggable) {
				printPostData(postParams);
			}
			try {
				HttpEntity httpentity = new UrlEncodedFormEntity(postParams, CHARSET);
				httpPost.setEntity(httpentity);
			} catch (UnsupportedEncodingException e) {
				EvtLog.e(TAG, e);
			}
		}

		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT_SHORT_IN_MS);
		HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_MIDDLE_IN_MS);

		DefaultHttpClient client = new DefaultHttpClient(httpParameters);
		client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
		if (COOKIE_STORE != null) {
			client.setCookieStore(COOKIE_STORE);
		}
		EvtLog.d(TAG, "client.execute begin ");
		try {
			HttpResponse httpResponse = client.execute(httpPost);
			int status = httpResponse.getStatusLine().getStatusCode();
			EvtLog.d(TAG, "client.execute end, status: " + status);
			String returnString = EntityUtils.toString(httpResponse.getEntity());
			if (status == HttpStatus.SC_OK) {
				try {
					jsonResult = new JsonResult(returnString);
					if (EvtLog.isDebugLoggable) {
						printResponse(returnString);
					}
				} catch (JSONException e) {
					EvtLog.e(TAG, e);
				}
				if (client.getCookieStore().getCookies() != null && client.getCookieStore().getCookies().size() > 0) {
					COOKIE_STORE = client.getCookieStore();
				}
			} else {
				EvtLog.e(TAG, "server response: " + returnString + ";  status:" + status, false);
				BottomTab.toast(PackageUtil.getString(R.string.msg_operate_fail_try_again));
			}
		} catch (Exception e) {
			if ((e instanceof IOException) || (e instanceof ClientProtocolException)) {
				EvtLog.e(TAG, "NetworkException", false);
				throw new NetworkException(PackageUtil.getString(R.string.network_is_not_available));
			} else {
				EvtLog.e(TAG, e);
			}
		} finally {
			EvtLog.d(TAG, "post end, " + url);
		}

		return jsonResult;
	}

	/**
	 * 通过get方式，跟服务器进行数据交互。该方法已经进行了网络检查
	 * 
	 * @param url
	 *            url地址
	 * @param getParams
	 *            附加在Url后面的参数
	 * @return json数据
	 * @throws MessageException
	 * @throws IOException
	 *             IO流异常
	 * @throws JSONException
	 *             json数据异常
	 * @throws NetworkException
	 *             异常信息
	 */
	public static JsonResult get(String url, List<NameValuePair> getParams) throws NetworkException, MessageException {
		return get(url, null, getParams);
	}

	/**
	 * 通过get方式，跟服务器进行数据交互。该方法已经进行了网络检查
	 * 
	 * @param url
	 *            url地址
	 * @param httpParams
	 *            http参数
	 * @param getParams
	 *            附加在Url后面的参数
	 * @return json数据
	 * @throws IOException
	 *             IO流异常
	 * @throws JSONException
	 *             json数据异常
	 * @throws NetworkException
	 *             异常信息
	 * @throws MessageException
	 * @throws ParseException
	 */
	public static JsonResult get(String url, HttpParams httpParams, List<NameValuePair> getParams)
			throws NetworkException, MessageException {
		if (!NetUtil.isNetworkAvailable()) {
			throw new NetworkException(PackageUtil.getString(R.string.network_is_not_available));
		}

		String buildUrl = buildUrl(url, getParams);
		JsonResult jsonResult = null;
		EvtLog.d(TAG, "get begin, url:" + buildUrl);
		HttpGet httpGet = new HttpGet(buildUrl);
		httpGet.setHeader(AJAX_APPEND_HEADER, "true");
		if (httpParams != null) {
			httpGet.setParams(httpParams);
		}
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT_SHORT_IN_MS);
		HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_MIDDLE_IN_MS);

		DefaultHttpClient client = new DefaultHttpClient(httpParameters);
		client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
		if (COOKIE_STORE != null) {
			client.setCookieStore(COOKIE_STORE);
		}
		EvtLog.d(TAG, "client.execute begin");
		try {
			HttpResponse httpResponse = client.execute(httpGet);
			int status = httpResponse.getStatusLine().getStatusCode();
			if (status == HttpStatus.SC_OK) {
				try {
					String returnString = EntityUtils.toString(httpResponse.getEntity());
					jsonResult = new JsonResult(returnString);
					if (EvtLog.isDebugLoggable) {
						printResponse(returnString);
					}
					if (client.getCookieStore().getCookies() != null && client.getCookieStore().getCookies().size() > 0) {
						COOKIE_STORE = client.getCookieStore();
					}
				} catch (JSONException e) {
					EvtLog.e(TAG, e);
				} catch (ParseException e) {
					EvtLog.e(TAG, e);
				} catch (IOException e) {
					EvtLog.e(TAG, e);
				}
			} else {
				EvtLog.e(TAG, "server response:  " + httpResponse.toString() + "; status:" + status, false);
				BottomTab.toast(PackageUtil.getString(R.string.msg_operate_fail_try_again));
			}
		} catch (Exception e) {
			if ((e instanceof IOException) || (e instanceof ClientProtocolException)) {
				throw new NetworkException(PackageUtil.getString(R.string.network_is_not_available));
			} else {
				EvtLog.e(TAG, e);
			}
		} finally {
			EvtLog.d(TAG, "get end, url:" + buildUrl);
		}

		return jsonResult;
	}

	@SuppressWarnings("null")
	private static String buildUrl(String url, List<NameValuePair> getParams) {
		if (getParams != null || getParams.size() > 0) {
			String returnUrl = url;
			if (url.indexOf(INTERROGATION) < 0) {
				returnUrl = url + INTERROGATION;
			}
			String tempParamters = "";
			for (int i = 0; i < getParams.size(); i++) {
				NameValuePair nameValuePair = getParams.get(i);
				tempParamters = tempParamters + "&" + nameValuePair.getName() + "="
						+ URLEncoder.encode(nameValuePair.getValue());
			}
			returnUrl = returnUrl + tempParamters.substring(1);
			EvtLog.d(TAG, returnUrl);

			return returnUrl;
		}

		return url;
	}

	private static void printPostData(List<NameValuePair> params) {
		if (params == null || params.size() < 1) {
			return;
		}
		EvtLog.d(TAG, "PostData: " + params.size());
		for (int i = 0; i < params.size(); ++i) {
			EvtLog.d(TAG, params.get(i).getName() + ":" + params.get(i).getValue());
		}
	}

	private static void printResponse(String s) {
		if (s == null || s.length() == 0 && !EvtLog.isDebugLoggable) {
			return;
		}
		EvtLog.d(TAG, "server response:\n");
		int idxBegin = 0;
		int idxEnd = 0;
		int iStep = 1024;
		int length = s.length();
		while (idxBegin < length) {
			if (idxEnd + iStep > length) {
				idxEnd = length;
			} else {
				idxEnd += iStep;
			}
			EvtLog.d(TAG, ">>" + s.substring(idxBegin, idxEnd));

			idxBegin = idxEnd;
		}
	}

	/**
	 * 
	 * @param url
	 *            url地址
	 * @param httpParams
	 *            参数
	 * @return 字节码
	 * @throws ClientProtocolException
	 *             协议异常
	 * @throws IOException
	 *             IO流异常
	 */
	public static byte[] getBytes(String url, HttpParams httpParams) throws ClientProtocolException, IOException {
		byte[] result;
		HttpGet httpGet = new HttpGet(url);
		if (httpParams != null) {
			httpGet.setParams(httpParams);
		} else {
			httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_SHORT_IN_MS);
			HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MIDDLE_IN_MS);
			ConnManagerParams.setMaxTotalConnections(httpParams, 10);
		}

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

		final ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
		DefaultHttpClient client = new DefaultHttpClient(cm, httpParams);

		if (COOKIE_STORE != null) {
			client.setCookieStore(COOKIE_STORE);
		}

		// 尝试3次
		HttpResponse httpResponse = null;
		try {
			httpResponse = client.execute(httpGet);
		} catch (Exception e) {
			try {
				httpResponse = client.execute(httpGet);
			} catch (Exception ex) {
				httpResponse = client.execute(httpGet);
			}
		}

		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity entity = httpResponse.getEntity();
			InputStream inputStream = entity.getContent();

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int bytesRead;
			byte[] data = new byte[BUFFERSIZE];
			while ((bytesRead = inputStream.read(data)) != -1) {
				buffer.write(data, 0, bytesRead);
			}
			buffer.flush();
			buffer.close();
			inputStream.close();
			result = buffer.toByteArray();
			entity.consumeContent();

			return result;
		}

		return null;
	}

	/**
	 * @param url
	 * @return http响应
	 * @throws Exception
	 */
	public static HttpResponse GetResponse(String url) throws Exception {
		EvtLog.d(TAG, "GetResponse begin, " + url);
		HttpResponse response = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(url));
			response = client.execute(request);

		} catch (Exception e) {
			EvtLog.e(TAG, e);
		}
		return response;
	}

	/**
	 * @param response
	 * @return response中的相应内容
	 * @throws IOException
	 */
	public static String GetResponseData(HttpResponse response) throws IOException {
		BufferedReader in = null;
		StringBuffer sb = null;
		try {
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
		} catch (Exception e) {
			EvtLog.e(TAG, e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

}
