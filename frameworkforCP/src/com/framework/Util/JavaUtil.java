package com.framework.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * java相关的辅助工具类
 *
 * @author 
 *
 */
public class JavaUtil {
	
	private static char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
	
	private static int[] toInt = new int[128];

	static {
		for (int i = 0; i < alphabet.length; i++) {
			toInt[alphabet[i]] = i;
		}
	}

	/**
	 * 获取Throwable详情信息
	 * 
	 * @param throwable
	 * @return
	 */
	public static String getDetailFromThrowable(Throwable throwable) {
		String msg = "";
		if (throwable != null) {
			msg += throwable.toString() + "\n";
			StackTraceElement[] stackTraceElements = throwable.getStackTrace();
			if (stackTraceElements != null && stackTraceElements.length > 0) {
				for (StackTraceElement stackTraceElement : stackTraceElements) {
					if (stackTraceElement != null) {
						msg += "    at " + stackTraceElement.toString() + "\n";
					}
				}
			}
		}
		return msg;
	}

	/**
	 * 字符串md5加密
	 * 
	 * @param text
	 * @return
	 * @throws NoSuchAlgorithmException 
	 * @throws UnsupportedEncodingException 
	 */
	public static String md5(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		String md5 = "";
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.reset();
		messageDigest.update(text.trim().getBytes("UTF-8"));
		byte[] md5Byte = messageDigest.digest();
		int j = md5Byte.length;
		char str[] = new char[j * 2];
		int k = 0;
		for (int i = 0; i < j; i++) {
			byte byte0 = md5Byte[i];
			str[k++] = hexDigits[(byte0 >> 4) & 0xf];
			str[k++] = hexDigits[byte0 & 0xf];
		}
		md5 = new String(str);
		return md5;
	}
	
	/**
	 * base64编码
	 * 
	 * @param data
	 *            需要编码的字节数组
	 * @return base64编码后的字符串
	 */
	public static String base64(byte[] data) {
		// TODO 实现base64编码
		char[] out = new char[((data.length + 2) / 3) * 4];

		for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
			boolean quad = false;
			boolean trip = false;
			int val = (0xFF & (int) data[i]);
			val <<= 8;
			if ((i + 1) < data.length) {
				val |= (0xFF & (int) data[i + 1]);
				trip = true;
			}
			val <<= 8;
			if ((i + 2) < data.length) {
				val |= (0xFF & (int) data[i + 2]);
				quad = true;
			}
			out[index + 3] = alphabet[(quad ? (val & 0x3F) : 64)];
			val >>= 6;
			out[index + 2] = alphabet[(trip ? (val & 0x3F) : 64)];
			val >>= 6;
			out[index + 1] = alphabet[val & 0x3F];
			val >>= 6;
			out[index + 0] = alphabet[val & 0x3F];
		}
		String encode = new String(out);
		/*
		 * // 末尾=改为’ ’(空格)填充 String blank = " "; String endsWith = ""; while (encode.endsWith("=")) {
		 * encode = encode.replaceAll("=$", ""); endsWith = endsWith + blank; } encode = encode +
		 * endsWith; // 将标准base 64中的 ‘+’ 和 ‘/’ 分别替换为 ‘-’ 和 ‘_’ encode = encode.replace("+", "-");
		 * encode = encode.replace("/", "_");
		 */
		return encode;
	}
	
	/**
	 * 对文件base64编码
	 * 
	 * @param file
	 *            需要编码的文件
	 * @return base64编码后的字符串
	 * @throws IOException 
	 */
	public static String base64WithFile(File file) throws IOException {
		FileInputStream fileInputStream = null;
		byte[] buffer = new byte[1024];
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		if (file != null && file.isFile() && file.canRead()) {
		fileInputStream = new FileInputStream(file);
		int temp = 0;
		while ((temp = fileInputStream.read(buffer)) != -1) {
			byteArrayOutputStream.write(buffer, 0, temp);
			temp = 0;
		}
		byteArrayOutputStream.flush();
			
		}
		byte[] result = byteArrayOutputStream.toByteArray();
		
		fileInputStream.close();
		byteArrayOutputStream.close();
		return base64(result);
	}
	
	
	/**
	 * Translates the specified Base64 string into a byte array.
	 *
	 * @param s the Base64 string (not null)
	 * @return the byte array (not null)
	 */
	public static byte[] decode(String s) {
		int delta = s.endsWith("==") ? 2 : s.endsWith("=") ? 1 : 0;
		byte[] buffer = new byte[s.length() * 3 / 4 - delta];
		int mask = 0xFF;
		int index = 0;
		for (int i = 0; i < s.length(); i += 4) {
			int c0 = toInt[s.charAt(i)];
			int c1 = toInt[s.charAt(i + 1)];
			buffer[index++] = (byte) (((c0 << 2) | (c1 >> 4)) & mask);
			if (index >= buffer.length) {
				return buffer;
			}
			int c2 = toInt[s.charAt(i + 2)];
			buffer[index++] = (byte) (((c1 << 4) | (c2 >> 2)) & mask);
			if (index >= buffer.length) {
				return buffer;
			}
			int c3 = toInt[s.charAt(i + 3)];
			buffer[index++] = (byte) (((c2 << 6) | c3) & mask);
		}
		return buffer;
	}


	public static String getPinYin(String input) throws UnsupportedEncodingException {
		byte[] bytes;
		int heightbyte = 0;
		int lowbyte = 0;
		int china_int = 0;
		String PYSX = " ";

		for (int i = 0; i < input.length(); i++) {
			bytes = input.substring(i, i + 1).getBytes("GBK");
			int a = bytes.length;
			if (bytes.length == 2) {
				heightbyte = ((short) (bytes[0] & (byte) 127)) + (short) 128;
				lowbyte = ((short) (bytes[1] & (byte) 127)) + (short) 128;
				china_int = heightbyte * 256 + lowbyte;
				// System.out.println( "china_int= "+china_int);
				if (china_int >= 45217 && china_int <= 45252) {
					PYSX += "A";
				} else if (china_int >= 45253 && china_int <= 45760) {
					PYSX += "B";
				} else if (china_int >= 45761 && china_int <= 46317) {
					PYSX += "C";
				} else if (china_int >= 46318 && china_int <= 46825) {
					PYSX += "D";
				} else if (china_int >= 46826 && china_int <= 47009) {
					PYSX += "E";
				} else if (china_int >= 47010 && china_int <= 47296) {
					PYSX += "F";
				} else if (china_int >= 47297 && china_int <= 47613) {
					PYSX += "G";
				} else if (china_int >= 47614 && china_int <= 48118) {
					PYSX += "H";
				} else if (china_int >= 48119 && china_int <= 49061) {
					PYSX += "J";
				} else if (china_int >= 49062 && china_int <= 49323) {
					PYSX += "K";
				} else if (china_int >= 49324 && china_int <= 49895) {
					PYSX += "L";
				} else if (china_int >= 49896 && china_int <= 50370) {
					PYSX += "M";
				} else if (china_int >= 50371 && china_int <= 50613) {
					PYSX += "N";
				} else if (china_int >= 50614 && china_int <= 50621) {
					PYSX += "O";
				} else if (china_int >= 50622 && china_int <= 50905) {
					PYSX += "P";
				} else if (china_int >= 50906 && china_int <= 51386) {
					PYSX += "Q";
				} else if (china_int >= 51387 && china_int <= 51445) {
					PYSX += "R";
				} else if (china_int >= 51446 && china_int <= 52217) {
					PYSX += "S";
				} else if (china_int >= 52218 && china_int <= 52697) {
					PYSX += "T";
				} else if (china_int >= 52698 && china_int <= 52979) {
					PYSX += "W";
				} else if (china_int >= 52980 && china_int <= 53640) {
					PYSX += "X";
				} else if (china_int >= 53689 && china_int <= 54480) {
					PYSX += "Y";
				} else if (china_int >= 54481 && china_int <= 55289) {
					PYSX += "Z";
				}
			} else {
				PYSX += new String(bytes);
			}
		}
		return PYSX;
	}
	
	
	 private static final double EARTH_RADIUS = 6378137.0;

	//计算出来的结果单位为千米。
	public static double gps2km(double lat_a, double lng_a, double lat_b, double lng_b) {
		double radLat1 = (lat_a * Math.PI / 180.0);
		double radLat2 = (lat_b * Math.PI / 180.0);
		double a = radLat1 - radLat2;
		double b = (lng_a - lng_b) * Math.PI / 180.0;
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		s = s / 1000;
		java.text.NumberFormat format = java.text.NumberFormat.getInstance(); 
		format.setMaximumFractionDigits(1); 
		try {
			s = Double.valueOf(format.format(s));
		} catch (NumberFormatException e) {
			return s;
		} 
		return s;
	}
}
