package com.framework.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import com.framework.Exception.EvtLog;
import com.framework.Exception.MessageException;

import android.os.Environment;
import com.framework.R;


/**
 * 文件帮助类
 * 
 * @author 
 * 
 */
public class FileUtil {
	public static final int BUFSIZE = 256;
	public static final int COUNT = 320;
	private static final String TAG = "FileUtils";
	private static final String DOWNLOAD_PATH = "paidui/download";

	/**
	 * 获取下载文件
	 * 
	 * @return 文件
	 * @throws MessageException
	 *             异常信息
	 */
	public static File getDownloadDir() throws MessageException {
		File downloadFile = null;
		if (android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			downloadFile = new File(Environment.getExternalStorageDirectory(), DOWNLOAD_PATH);
		}

		if (downloadFile == null) {
			throw new MessageException(PackageUtil.getString(R.string.more_check_version_no_sdcard));
		}

		return downloadFile;
	}

	/**
	 * 在SD卡上面创建文件
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 文件
	 * @throws IOException
	 *             异常
	 */
	public static File createSDFile(String filePath) throws IOException {
		File file = new File(filePath);
		file.createNewFile();
		return file;
	}

	/**
	 * 在SD卡上面创建目录
	 * 
	 * @param dirName
	 *            目录名称
	 * @return 文件
	 */
	public static File createSDDir(String dirName) {
		File dir = new File(dirName);
		dir.mkdir();
		return dir;
	}

	/**
	 * 判断指定的文件是否存在
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 是否存在
	 */
	public static boolean isFileExist(String filePath) {
		File file = new File(filePath);
		return file.exists();
	}

	/**
	 * 删除指定的文件
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 是否删除
	 */
	public static boolean delete(String filePath) {
		File file = new File(filePath);
		return file.delete();
	}

	/**
	 * 将文件写入SD卡
	 * 
	 * @param path
	 *            路径
	 * @param fileName
	 *            文件名称
	 * @param input
	 *            输入流
	 * @return 文件
	 */
	public static File writeToSDCard(String path, String fileName, InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			createSDDir(path);
			file = createSDFile(path + fileName);
			output = new FileOutputStream(file);

			byte[] buffer = new byte[BUFSIZE];
			int readedLength = -1;
			while ((readedLength = input.read(buffer)) != -1) {
				output.write(buffer, 0, readedLength);
			}
			output.flush();

		} catch (Exception e) {
			EvtLog.e(TAG, e.getMessage());
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				EvtLog.e(TAG, e.getMessage());
			}
		}

		return file;
	}

	/**
	 * 判断SD卡是否已经准备好
	 * 
	 * @return 是否有SDCARD
	 */
	public static boolean isSDCardReady() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 使用Http下载文件，并保存在手机目录中
	 * 
	 * @param urlStr
	 *            url地址
	 * @param path
	 *            路径
	 * @param fileName
	 *            文件名称
	 * @param onDownloadingListener
	 *            下载监听器
	 * @return -1:文件下载出错 0:文件下载成功
	 * @throws MessageException
	 */
	public static boolean downFile(String urlStr, String path, String fileName,
			OnDownloadingListener onDownloadingListener) {
		InputStream inputStream = null;
		try {
			if (!path.endsWith("/")) {
				path += "/";
			}

			if (isFileExist(path + fileName)) {
				delete(path + fileName);
			}

			HttpClient client = new DefaultHttpClient();
			// 设置网络连接超时和读数据超时
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000)
					.setParameter(CoreConnectionPNames.SO_TIMEOUT, 600000);
			HttpGet httpget = new HttpGet(urlStr);
			HttpResponse response = client.execute(httpget);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				EvtLog.e(TAG, "http status code is: " + statusCode);
				return false;
			}

			InputStream fileStream = response.getEntity().getContent();
			FileOutputStream output = new FileOutputStream(path + fileName);
			byte[] buffer = new byte[BUFSIZE];
			int len = 0;
			int count = 0;
			int progress = 0;
			while ((len = fileStream.read(buffer)) > 0) {
				count += len;
				progress += len;
				EvtLog.d(TAG, "read " + len + " bytes, total read: " + count + " bytes");
				output.write(buffer, 0, len);
				if (onDownloadingListener != null && count >= BUFSIZE * COUNT) {
					EvtLog.d(TAG, "onDownloadingListener.onDownloading()");
					onDownloadingListener.onDownloading(progress);
					count = 0;
				}
			}
			if (onDownloadingListener != null && count >= 0) {
				EvtLog.d(TAG, "onDownloadingListener else)");
				onDownloadingListener.onDownloading(progress);
				count = 0;
			}
			fileStream.close();
			output.close();

		} catch (Exception e) {
			EvtLog.e(TAG, e);
			return false;
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				EvtLog.e(TAG, e);
			}
		}

		return true;
	}

	/**
	 * 
	 * @author Q.d
	 * 
	 */
	public interface OnDownloadingListener {
		/**
		 * 下载
		 * 
		 * @param progressInByte
		 *            已下载的字节长度
		 * 
		 */
		void onDownloading(int progressInByte);
	}

}
