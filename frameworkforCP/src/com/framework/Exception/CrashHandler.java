package com.framework.Exception;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.framework.R;
import com.framework.Util.PackageUtil;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;



/**
 * �쳣��Ϣ�����࣬����ᴦ��δ��׽���쳣
 * 
 * @author 
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = "CrashHandler";
	private static final int SLEEP_INTERVAL = 3000;
	// CrashHandlerʵ��
	private static CrashHandler INSTANCE = new CrashHandler();

	/** ���󱨸��ļ�����չ�� */
	private String mCrashReportExt = ".log";
	private String mCrashPath = "/sdcard/myApp/crash/";

	// ���ڸ�ʽ������,��Ϊ��־�ļ�����һ����
	private DateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	// �����Context����
	private Context mContext;
	// �����洢�豸��Ϣ���쳣��Ϣ
	private Map<String, String> mInfos = new HashMap<String, String>();
	// ϵͳĬ�ϵ�UncaughtException������
	private Thread.UncaughtExceptionHandler mDefaultHandler;

	/** ��ֻ֤��һ��CrashHandlerʵ�� */
	private CrashHandler() {
	}

	/**
	 * ��ȡCrashHandlerʵ�� ,����ģʽ
	 * 
	 * @return boolean ���ص�һʵ��
	 */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * ��ʼ��
	 * 
	 * @param context
	 *            ������
	 * @param crashPath
	 *            �쳣���ļ�Ŀ¼
	 */
	public void init(Context context, String crashPath) {
		mContext = context;
		this.mCrashPath = crashPath;
		this.mCrashReportExt = PackageUtil.getConfigString("crash_report_ext");

		// ��ȡϵͳĬ�ϵ�UncaughtException������
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// ���ø�CrashHandlerΪ�����Ĭ�ϴ�����
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * ��UncaughtException����ʱ��ת��ú���������
	 * 
	 * @param thread
	 *            �����쳣���߳�
	 * @param ex
	 *            �쳣��Ϣ
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// ����û�û�д�������ϵͳĬ�ϵ��쳣������������
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(SLEEP_INTERVAL);
			} catch (InterruptedException e) {
				EvtLog.e(TAG, e);
			}

			// �˳�����
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	/**
	 * �Զ��������,�ռ�������Ϣ ���ʹ��󱨸�Ȳ������ڴ����.
	 * 
	 * @param ex
	 *            �쳣��Ϣ
	 * @return true:��������˸��쳣��Ϣ;���򷵻�false.
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return true;
		}

		// ʹ��Toast����ʾ�쳣��Ϣ
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext, mContext.getResources().getString(R.string.exit_prompt), Toast.LENGTH_LONG)
				.show();
				Looper.loop();
			}
		}.start();

		return saveInformation(ex);
	}

	/**
	 * �����쳣��Ϣ
	 * 
	 * @param ex
	 *            �쳣��Ϣ
	 * @return boolean ����״̬
	 */
	public boolean saveInformation(Throwable ex) {

		try {
			// �ռ��豸������Ϣ
			collectDeviceInfo(mContext);
			// ������־�ļ�
			saveCrashInfo2File(ex);
			// ������󱨸��ļ�
			String errorText = getLogContent(ex);
			EvtLog.w(TAG, "errorText : \n" + errorText);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/**
	 * ��ȡ���󱨸��ļ���
	 * 
	 * @param ctx
	 *            ������
	 * @return �����쳣��Ϣ�ļ����б�
	 */
	public File[] getCrashReportFiles(Context ctx) {
		File file[] = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

			File filesDirs = new File(mCrashPath);
			file = filesDirs.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					return filename.endsWith(mCrashReportExt);
				}
			});
		}

		return file;

	}

	/**
	 * �ռ��豸������Ϣ
	 * 
	 * @param ctx
	 *            ������
	 */
	private void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				mInfos.put("versionName", versionName);
				mInfos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			EvtLog.e(TAG, e);
		}
		EvtLog.d(TAG, "device information--------------------------------->");
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				mInfos.put(field.getName(), field.get(null).toString());
				EvtLog.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				EvtLog.e(TAG, e);
			}
		}
		EvtLog.d(TAG, "device information---------------------------------<");
	}

	/**
	 * ���������Ϣ���ļ���
	 * 
	 * @param ex
	 *            �쳣��Ϣ
	 * @return �����ļ�����,���ڽ��ļ����͵�������
	 */
	public String saveCrashInfo2File(Throwable ex) {

		String logContent = getLogContent(ex);
		try {
			long timestamp = System.currentTimeMillis();
			String time = mFormatter.format(new Date());
			String fileName = "crash-" + time + "-" + timestamp + mCrashReportExt;
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				File dir = new File(mCrashPath);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				File errFile = new File(dir, fileName);
				if (!errFile.exists()) {
					errFile.createNewFile();
				}
				FileOutputStream fos = new FileOutputStream(errFile);
				fos.write(logContent.getBytes());
				fos.close();
			}
			return fileName;
		} catch (Exception e) {
			EvtLog.w(TAG, e);
		}
		return null;
	}

	/**
	 * ��ȡ�������Ѻõ��쳣��Ϣ
	 * 
	 * @param ex
	 *            �쳣��Ϣ
	 * @return ���ı��ķ�ʽ�����쳣��Ϣ����
	 */
	private String getLogContent(Throwable ex) {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : mInfos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);

		return sb.toString();
	}

}
