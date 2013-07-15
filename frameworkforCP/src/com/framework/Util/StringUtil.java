package com.framework.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author qiu.d
 * 
 */
public class StringUtil {
	/**
	 * 字符串去空格，回车，换行，制表符
	 * 
	 * @param str
	 *            要修改的字符串
	 * @return 修改完成的字符串
	 */
	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 将输入的字符串进行html编码
	 * 
	 * @param input
	 *            输入的字符串
	 * @return html编码后的结果
	 */
	public static String htmEncode(String input) {
		if (null == input || "".equals(input)) {
			return input;
		}

		StringBuffer stringbuffer = new StringBuffer();
		int j = input.length();
		for (int i = 0; i < j; i++) {
			char c = input.charAt(i);
			switch (c) {
				case 60:
					stringbuffer.append("&lt;");
					break;
				case 62:
					stringbuffer.append("&gt;");
					break;
				case 38:
					stringbuffer.append("&amp;");
					break;
				case 34:
					stringbuffer.append("&quot;");
					break;
				case 169:
					stringbuffer.append("&copy;");
					break;
				case 174:
					stringbuffer.append("&reg;");
					break;
				case 165:
					stringbuffer.append("&yen;");
					break;
				case 8364:
					stringbuffer.append("&euro;");
					break;
				case 8482:
					stringbuffer.append("&#153;");
					break;
				case 13:
					if (i < j - 1 && input.charAt(i + 1) == 10) {
						stringbuffer.append("<br>");
						i++;
					}
					break;
				case 32:
					if (i < j - 1 && input.charAt(i + 1) == ' ') {
						stringbuffer.append(" &nbsp;");
						i++;
						break;
					}
				default:
					stringbuffer.append(c);
					break;
			}
		}
		return new String(stringbuffer.toString());
	}

	/**
	 * 判断字符串是否为null或者空字符串
	 * 
	 * @param input
	 *            输入的字符串
	 * @return 如果为null或者空字符串，返回true；否则返回false
	 */
	public static boolean isNullOrEmpty(String input) {
		if (null == input || "".equals(input)) {
			return true;
		} else {
			return false;
		}
	}
}
